package org.exodusstudio.stellaris.client.cinematic;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.client.effects.ParasiteCameraShake;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity;
import org.exodusstudio.stellaris.common.network.packets.StarCrawlerBossIntroStartPacket;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class StarCrawlerBossIntroController {
    public static final float DISCOVERY_END_TICK = 30.0F;
    public static final float REVEAL_END_TICK = 70.0F;
    public static final float LOCK_ON_END_TICK = 78.0F;
    public static final float ROAR_END_TICK = 106.0F;
    public static final float TITLE_END_TICK = 136.0F;
    public static final float RETURN_END_TICK = 150.0F;

    private static final long NANOS_PER_TICK = 50_000_000L;
    private static final long CAMERA_CATCH_UP_NANOS = 250_000_000L;
    private static final long HUD_REVEAL_NANOS = 750_000_000L;
    private static final long HUD_REVEAL_RETENTION_NANOS = 2_000_000_000L;

    private static final int TARGET_LOAD_GRACE_TICKS = 20;
    private static final double TELEPORT_CLEANUP_DISTANCE_SQR = 16.0D * 16.0D;
    private static final double CAMERA_RADIUS = 0.14D;

    private static final Map<UUID, Timeline> TIMELINES = new HashMap<>();
    private static final Map<UUID, Long> HUD_REVEAL_STARTS = new HashMap<>();

    private static UUID visualOwner;
    private static ActiveVisual activeVisual;
    private static boolean initialized;
    private static long suppressCameraShakeUntilNanos;

    private StarCrawlerBossIntroController() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        ClientTickEvent.CLIENT_POST.register(StarCrawlerBossIntroController::clientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> clearAll());
        ClientLifecycleEvent.CLIENT_STOPPING.register(minecraft -> clearAll());
        ClientRawInputEvent.KEY_PRESSED.register(StarCrawlerBossIntroController::handleKey);
        ClientRawInputEvent.MOUSE_CLICKED_PRE.register(StarCrawlerBossIntroController::handleMouseClick);
    }

    public static void start(StarCrawlerBossIntroStartPacket packet) {
        if (packet == null || packet.bossUuid() == null || packet.durationTicks() <= 0) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        long now = System.nanoTime();
        Timeline previousTimeline = TIMELINES.get(packet.bossUuid());
        if (previousTimeline != null
                && previousTimeline.serverStartGameTime() == packet.serverStartGameTime()) {
            return;
        }

        float elapsedAtSend = Math.max(
                0.0F,
                packet.serverGameTimeAtSend() - packet.serverStartGameTime()
        );
        float elapsedFromClientClock = minecraft.level == null
                ? 0.0F
                : Math.max(0.0F, minecraft.level.getGameTime() - packet.serverStartGameTime());
        float elapsedAtReceipt = Math.min(
                packet.durationTicks(),
                Math.max(elapsedAtSend, elapsedFromClientClock)
        );

        Timeline timeline = new Timeline(
                packet.bossEntityId(),
                packet.serverStartGameTime(),
                packet.durationTicks(),
                now - (long) (elapsedAtReceipt * NANOS_PER_TICK)
        );
        TIMELINES.put(packet.bossUuid(), timeline);
        HUD_REVEAL_STARTS.remove(packet.bossUuid());

        if (packet.bossUuid().equals(visualOwner)) {
            return;
        }

        if (visualOwner != null) {
            Timeline current = TIMELINES.get(visualOwner);
            if (current != null && current.isActive(now)) {
                return;
            }
            discardVisual(now);
        }

        if (!timeline.isActive(now) || elapsedAtReceipt >= RETURN_END_TICK) {
            return;
        }

        ResourceKey<Level> dimension = minecraft.level == null
                ? null
                : minecraft.level.dimension();
        Vec3 playerStart = minecraft.player == null
                ? null
                : minecraft.player.position();

        visualOwner = packet.bossUuid();
        activeVisual = new ActiveVisual(
                packet.bossEntityId(),
                packet.bossUuid(),
                dimension,
                playerStart,
                new Vec3(packet.bossX(), packet.bossY(), packet.bossZ())
        );
        releaseActionMappings(minecraft);
    }

    public static void finish(UUID bossUuid) {
        if (bossUuid == null) {
            return;
        }

        long now = System.nanoTime();
        TIMELINES.remove(bossUuid);
        HUD_REVEAL_STARTS.putIfAbsent(bossUuid, now);

        if (bossUuid.equals(visualOwner)) {
            discardVisual(now);
        }
    }

    public static void suspendVisualForDeath() {
        discardVisual(System.nanoTime());
        suppressCameraShakeUntilNanos = 0L;
    }

    public static void suppressHudRevealForDeath(UUID bossUuid) {
        if (bossUuid != null) {
            HUD_REVEAL_STARTS.remove(bossUuid);
        }
    }

    public static void clientTick(Minecraft minecraft) {
        long now = System.nanoTime();

        HUD_REVEAL_STARTS.entrySet().removeIf(
                entry -> now - entry.getValue() > HUD_REVEAL_RETENTION_NANOS
        );

        if (minecraft.player == null || minecraft.level == null) {
            clearAll();
            return;
        }

        Iterator<Map.Entry<UUID, Timeline>> timelineIterator = TIMELINES.entrySet().iterator();
        while (timelineIterator.hasNext()) {
            Map.Entry<UUID, Timeline> entry = timelineIterator.next();
            Entity trackedEntity = minecraft.level.getEntity(entry.getValue().bossEntityId);
            if (trackedEntity instanceof StarCrawlerBossEntity trackedBoss
                    && trackedBoss.getUUID().equals(entry.getKey())
                    && trackedBoss.getIntroState() == StarCrawlerBossEntity.IntroState.COMPLETE) {
                HUD_REVEAL_STARTS.putIfAbsent(entry.getKey(), now);
                timelineIterator.remove();
            }
        }

        if (visualOwner != null && !TIMELINES.containsKey(visualOwner)) {
            discardVisual(now);
        }

        if (visualOwner != null
                && elapsedTicks(visualOwner, now) >= RETURN_END_TICK) {
            discardVisual(now);
        }

        if (isAuthoritativelyLocked()) {
            releaseActionMappings(minecraft);
        }

        ActiveVisual visual = activeVisual;
        if (visual == null) {
            return;
        }

        if (!minecraft.player.isAlive()
                || minecraft.player.isRemoved()
                || (visual.dimension != null
                && !visual.dimension.equals(minecraft.level.dimension()))) {
            clearAll();
            return;
        }

        if (visual.playerStartPosition != null) {
            double deltaX = minecraft.player.getX()
                    - visual.playerStartPosition.x;
            double deltaZ = minecraft.player.getZ()
                    - visual.playerStartPosition.z;

            if (deltaX * deltaX + deltaZ * deltaZ
                    > TELEPORT_CLEANUP_DISTANCE_SQR) {
                clearAll();
                return;
            }
        }

        Entity entity = minecraft.level.getEntity(visual.bossEntityId);
        if (!(entity instanceof StarCrawlerBossEntity boss)) {
            visual.missingTargetTicks++;
            if (visual.missingTargetTicks > TARGET_LOAD_GRACE_TICKS) {
                finish(visual.bossUuid);
            }
            return;
        }

        visual.missingTargetTicks = 0;
        if (!boss.getUUID().equals(visual.bossUuid)
                || !boss.isAlive()
                || boss.isRemoved()) {
            finish(visual.bossUuid);
            return;
        }

        if (boss.getIntroState() == StarCrawlerBossEntity.IntroState.COMPLETE) {
            finish(visual.bossUuid);
            return;
        }

        if (visual.skipped) {
            return;
        }

        float elapsed = elapsedTicks(visual.bossUuid, now);
        if (!visual.roarShakeStarted && elapsed >= 90.0F) {
            visual.roarShakeStarted = true;
            int remaining = Mth.clamp((int) Math.ceil(105.0F - elapsed), 0, 15);
            if (remaining > 0) {
                float strength = 1.95F * (remaining / 15.0F);
                ParasiteCameraShake.start(remaining, strength);
                visual.introShakeUntilNanos = Math.max(
                        visual.introShakeUntilNanos,
                        now + remaining * NANOS_PER_TICK
                );
            }
        }

        if (!visual.titleShakeStarted && elapsed >= 106.0F) {
            visual.titleShakeStarted = true;
            int remaining = Mth.clamp((int) Math.ceil(112.0F - elapsed), 0, 6);
            if (remaining > 0) {
                ParasiteCameraShake.start(remaining, 0.52F * (remaining / 6.0F));
                visual.introShakeUntilNanos = Math.max(
                        visual.introShakeUntilNanos,
                        now + remaining * NANOS_PER_TICK
                );
            }
        }
    }

    public static boolean isAuthoritativelyLocked() {
        return !TIMELINES.isEmpty();
    }

    public static boolean isVisualActive() {
        if (StarCrawlerBossDeathController.isVisualActive()) {
            return false;
        }
        ActiveVisual visual = activeVisual;
        if (visual == null || visual.skipped) {
            return false;
        }

        Timeline timeline = TIMELINES.get(visual.bossUuid);
        long now = System.nanoTime();
        return timeline != null
                && timeline.isActive(now)
                && timeline.elapsedTicks(now) < RETURN_END_TICK;
    }

    public static boolean shouldHideBossHud(StarCrawlerBossEntity boss) {
        if (boss == null) {
            return false;
        }

        Timeline timeline = TIMELINES.get(boss.getUUID());
        return boss.getIntroState() != StarCrawlerBossEntity.IntroState.COMPLETE
                || (timeline != null && timeline.isActive(System.nanoTime()));
    }

    public static float hudRevealProgress(UUID bossUuid) {
        if (bossUuid == null) {
            return 1.0F;
        }

        Timeline timeline = TIMELINES.get(bossUuid);
        long now = System.nanoTime();
        if (timeline != null && timeline.isActive(now)) {
            return 0.0F;
        }

        Long start = HUD_REVEAL_STARTS.get(bossUuid);
        if (start == null) {
            return 1.0F;
        }

        float progress = Mth.clamp(
                (now - start) / (float) HUD_REVEAL_NANOS,
                0.0F,
                1.0F
        );
        return smootherStep(progress);
    }

    public static float getIntroElapsedTicks(float partialTick) {
        UUID owner = visualOwner;
        if (owner == null) {
            return -1.0F;
        }
        return elapsedTicks(owner, System.nanoTime());
    }

    public static CameraPose sampleCamera(
            Vec3 normalPosition,
            float normalYaw,
            float normalPitch,
            float partialTick
    ) {
        if (StarCrawlerBossDeathController.isVisualActive()) {
            return null;
        }
        ActiveVisual visual = activeVisual;
        if (visual == null || visual.skipped || normalPosition == null) {
            return null;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return null;
        }

        Timeline timeline = TIMELINES.get(visual.bossUuid);
        long now = System.nanoTime();
        if (timeline == null || !timeline.isActive(now)) {
            return null;
        }

        float elapsed = timeline.elapsedTicks(now);
        if (elapsed >= RETURN_END_TICK) {
            return null;
        }

        Entity entity = minecraft.level.getEntity(visual.bossEntityId);
        if (!(entity instanceof StarCrawlerBossEntity boss)
                || !boss.getUUID().equals(visual.bossUuid)
                || !boss.isAlive()
                || boss.isRemoved()) {
            return null;
        }

        if (!visual.pathInitialized) {
            visual.initializePath(
                    boss,
                    normalPosition,
                    normalYaw,
                    normalPitch,
                    partialTick
            );
        }

        PathSample sample = visual.samplePath(elapsed, normalPosition);
        float returnProgress = Mth.clamp(
                (elapsed - TITLE_END_TICK) / (RETURN_END_TICK - TITLE_END_TICK),
                0.0F,
                1.0F
        );

        Vec3 safePosition = collisionSafe(
                minecraft,
                boss,
                sample.focus,
                sample.position,
                normalPosition,
                visual.lastCameraPosition,
                returnProgress
        );

        CameraPose lookPose = lookAt(
                safePosition,
                sample.focus,
                normalYaw,
                normalPitch
        );
        float yaw = lookPose.yaw;
        float pitch = lookPose.pitch;

        if (returnProgress > 0.0F) {
            float easedReturn = smootherStep(returnProgress);
            yaw = Mth.rotLerp(easedReturn, visual.returnStartYaw, normalYaw);
            pitch = Mth.lerp(easedReturn, visual.returnStartPitch, normalPitch);

            float convergence = smootherStep(
                    Mth.clamp((returnProgress - 0.62F) / 0.38F, 0.0F, 1.0F)
            );
            safePosition = safePosition.lerp(normalPosition, convergence);
        }

        if (elapsed >= RETURN_END_TICK - 0.05F) {
            safePosition = normalPosition;
            yaw = normalYaw;
            pitch = normalPitch;
        }

        float catchUp = smootherStep(Mth.clamp(
                (now - visual.cameraActivatedNanos) / (float) CAMERA_CATCH_UP_NANOS,
                0.0F,
                1.0F
        ));
        if (catchUp < 1.0F) {
            safePosition = visual.activationPosition.lerp(safePosition, catchUp);
            yaw = Mth.rotLerp(catchUp, visual.activationYaw, yaw);
            pitch = Mth.lerp(catchUp, visual.activationPitch, pitch);
        }

        pitch = Mth.clamp(pitch, -89.5F, 89.5F);
        visual.lastCameraPosition = safePosition;
        return new CameraPose(safePosition, yaw, pitch);
    }

    public static boolean shouldSuppressCameraShake() {
        return System.nanoTime() < suppressCameraShakeUntilNanos;
    }

    private static EventResult handleKey(Minecraft minecraft, int action, KeyEvent keyEvent) {
        if (action == GLFW.GLFW_PRESS && isVisualActive()) {
            if (keyEvent.key() == GLFW.GLFW_KEY_SPACE
                    || keyEvent.key() == GLFW.GLFW_KEY_ESCAPE) {
                skipVisual();
                return EventResult.interruptTrue();
            }
        }

        if (isAuthoritativelyLocked() && isBlockedGameplayKey(minecraft, keyEvent)) {
            return EventResult.interruptTrue();
        }
        return EventResult.pass();
    }

    private static EventResult handleMouseClick(
            Minecraft minecraft,
            MouseButtonInfo buttonInfo,
            int action
    ) {
        if (!isAuthoritativelyLocked() || minecraft.screen != null) {
            return EventResult.pass();
        }

        MouseButtonEvent event = new MouseButtonEvent(0.0D, 0.0D, buttonInfo);
        if (minecraft.options.keyAttack.matchesMouse(event)
                || minecraft.options.keyUse.matchesMouse(event)
                || minecraft.options.keyPickItem.matchesMouse(event)) {
            return EventResult.interruptTrue();
        }
        return EventResult.pass();
    }

    private static boolean isBlockedGameplayKey(Minecraft minecraft, KeyEvent event) {
        if (minecraft == null) {
            return false;
        }

        return minecraft.options.keyAttack.matches(event)
                || minecraft.options.keyUse.matches(event)
                || minecraft.options.keyPickItem.matches(event)
                || minecraft.options.keyDrop.matches(event)
                || minecraft.options.keySwapOffhand.matches(event)
                || minecraft.options.keyInventory.matches(event);
    }

    private static void skipVisual() {
        ActiveVisual visual = activeVisual;
        if (visual == null || visual.skipped) {
            return;
        }

        visual.skipped = true;
        suppressCameraShakeUntilNanos = Math.max(
                suppressCameraShakeUntilNanos,
                visual.introShakeUntilNanos
        );
    }

    private static void releaseActionMappings(Minecraft minecraft) {
        if (minecraft == null) {
            return;
        }

        release(minecraft.options.keyAttack);
        release(minecraft.options.keyUse);
        release(minecraft.options.keyPickItem);
        release(minecraft.options.keyDrop);
        release(minecraft.options.keySwapOffhand);
    }

    private static void release(KeyMapping mapping) {
        mapping.setDown(false);
        while (mapping.consumeClick()) {
            // Discard actions queued immediately before the authoritative lock.
        }
    }

    private static float elapsedTicks(UUID bossUuid, long now) {
        Timeline timeline = TIMELINES.get(bossUuid);
        return timeline == null
                ? -1.0F
                : timeline.elapsedTicks(now);
    }

    private static void discardVisual(long now) {
        if (activeVisual != null && activeVisual.introShakeUntilNanos > now) {
            suppressCameraShakeUntilNanos = Math.max(
                    suppressCameraShakeUntilNanos,
                    activeVisual.introShakeUntilNanos
            );
        }
        activeVisual = null;
        visualOwner = null;
    }

    private static void clearAll() {
        discardVisual(System.nanoTime());
        TIMELINES.clear();
        HUD_REVEAL_STARTS.clear();
    }

    private static Vec3 collisionSafe(
            Minecraft minecraft,
            StarCrawlerBossEntity boss,
            Vec3 focus,
            Vec3 desired,
            Vec3 normalPosition,
            Vec3 previous,
            float returnProgress
    ) {
        Level level = minecraft.level;
        if (level == null) {
            return desired;
        }

        Vec3 result = desired;
        float focusCollisionWeight = 1.0F - smootherStep(
                Mth.clamp((returnProgress - 0.35F) / 0.65F, 0.0F, 1.0F)
        );
        if (focusCollisionWeight > 0.0F) {
            Vec3 clipped = clipCameraRay(minecraft, focus, result);
            result = result.lerp(clipped, focusCollisionWeight);
        }

        if (previous != null && previous.distanceToSqr(result) > 1.0E-5D) {
            result = clipCameraRay(minecraft, previous, result);
        }

        result = pushOutsideBoss(boss, result);
        if (cameraSpaceIsFree(level, result)) {
            return result;
        }

        for (int i = 1; i <= 8; i++) {
            Vec3 raised = result.add(0.0D, i * 0.16D, 0.0D);
            raised = pushOutsideBoss(boss, raised);
            if (cameraSpaceIsFree(level, raised)) {
                return raised;
            }
        }

        for (int i = 1; i <= 8; i++) {
            Vec3 towardFocus = result.lerp(focus, i / 10.0D);
            towardFocus = pushOutsideBoss(boss, towardFocus);
            if (cameraSpaceIsFree(level, towardFocus)) {
                return towardFocus;
            }
        }

        return normalPosition;
    }

    private static Vec3 clipCameraRay(Minecraft minecraft, Vec3 from, Vec3 to) {
        if (minecraft.level == null || minecraft.player == null || from.distanceToSqr(to) < 1.0E-6D) {
            return to;
        }

        BlockHitResult hit = minecraft.level.clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                minecraft.player
        ));
        if (hit.getType() == HitResult.Type.MISS) {
            return to;
        }

        Vec3 towardFrom = from.subtract(to);
        if (towardFrom.lengthSqr() < 1.0E-6D) {
            return hit.getLocation();
        }
        return hit.getLocation().add(towardFrom.normalize().scale(0.22D));
    }

    private static boolean cameraSpaceIsFree(Level level, Vec3 position) {
        AABB cameraBounds = new AABB(
                position.x - CAMERA_RADIUS,
                position.y - CAMERA_RADIUS,
                position.z - CAMERA_RADIUS,
                position.x + CAMERA_RADIUS,
                position.y + CAMERA_RADIUS,
                position.z + CAMERA_RADIUS
        );
        return level.noCollision(cameraBounds);
    }

    private static Vec3 pushOutsideBoss(StarCrawlerBossEntity boss, Vec3 position) {
        AABB forbidden = boss.getBoundingBox().inflate(0.42D);
        if (!forbidden.contains(position)) {
            return position;
        }

        Vec3 center = forbidden.getCenter();
        Vec3 horizontal = position.subtract(center).multiply(1.0D, 0.0D, 1.0D);
        if (horizontal.lengthSqr() < 1.0E-5D) {
            horizontal = new Vec3(0.0D, 0.0D, 1.0D);
        }

        double radius = Math.max(forbidden.getXsize(), forbidden.getZsize()) * 0.5D + 0.08D;
        Vec3 outside = center.add(horizontal.normalize().scale(radius));
        return new Vec3(outside.x, position.y, outside.z);
    }

    private static CameraPose lookAt(
            Vec3 position,
            Vec3 target,
            float fallbackYaw,
            float fallbackPitch
    ) {
        Vec3 delta = target.subtract(position);
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        if (delta.lengthSqr() < 1.0E-7D) {
            return new CameraPose(position, fallbackYaw, fallbackPitch);
        }

        float yaw = (float) Math.toDegrees(Mth.atan2(delta.z, delta.x)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Mth.atan2(delta.y, horizontal));
        return new CameraPose(position, yaw, pitch);
    }

    private static Vec3 catmullRom(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, float progress) {
        double t = Mth.clamp(progress, 0.0F, 1.0F);
        double t2 = t * t;
        double t3 = t2 * t;
        return new Vec3(
                catmullComponent(p0.x, p1.x, p2.x, p3.x, t, t2, t3),
                catmullComponent(p0.y, p1.y, p2.y, p3.y, t, t2, t3),
                catmullComponent(p0.z, p1.z, p2.z, p3.z, t, t2, t3)
        );
    }

    private static double catmullComponent(
            double p0,
            double p1,
            double p2,
            double p3,
            double t,
            double t2,
            double t3
    ) {
        return 0.5D * (
                2.0D * p1
                        + (-p0 + p2) * t
                        + (2.0D * p0 - 5.0D * p1 + 4.0D * p2 - p3) * t2
                        + (-p0 + 3.0D * p1 - 3.0D * p2 + p3) * t3
        );
    }

    private static float range(float value, float start, float end) {
        if (end <= start) {
            return 1.0F;
        }
        return Mth.clamp((value - start) / (end - start), 0.0F, 1.0F);
    }

    private static float smootherStep(float value) {
        float t = Mth.clamp(value, 0.0F, 1.0F);
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }

    public record CameraPose(Vec3 position, float yaw, float pitch) {
    }

    private record Timeline(
            int bossEntityId,
            long serverStartGameTime,
            int durationTicks,
            long startNanos
    ) {
        private float elapsedTicks(long now) {
            return Math.max(0.0F, (now - startNanos) / (float) NANOS_PER_TICK);
        }

        private boolean isActive(long now) {
            return elapsedTicks(now) < durationTicks;
        }
    }

    private record PathSample(Vec3 position, Vec3 focus) {
    }

    private static final class ActiveVisual {
        private final int bossEntityId;
        private final UUID bossUuid;
        private final ResourceKey<Level> dimension;
        private final Vec3 playerStartPosition;
        private final Vec3 packetBossAnchor;

        private boolean skipped;
        private boolean pathInitialized;
        private boolean roarShakeStarted;
        private boolean titleShakeStarted;
        private int missingTargetTicks;
        private long introShakeUntilNanos;

        private Vec3 activationPosition;
        private long cameraActivatedNanos;
        private float activationYaw;
        private float activationPitch;
        private Vec3 lastCameraPosition;
        private Vec3[] positions;
        private Vec3[] focuses;
        private float returnStartYaw;
        private float returnStartPitch;

        private ActiveVisual(
                int bossEntityId,
                UUID bossUuid,
                ResourceKey<Level> dimension,
                Vec3 playerStartPosition,
                Vec3 packetBossAnchor
        ) {
            this.bossEntityId = bossEntityId;
            this.bossUuid = bossUuid;
            this.dimension = dimension;
            this.playerStartPosition = playerStartPosition;
            this.packetBossAnchor = packetBossAnchor;
        }

        private void initializePath(
                StarCrawlerBossEntity boss,
                Vec3 normalPosition,
                float normalYaw,
                float normalPitch,
                float partialTick
        ) {
            this.activationPosition = normalPosition;
            this.cameraActivatedNanos = System.nanoTime();
            this.activationYaw = normalYaw;
            this.activationPitch = normalPitch;
            this.lastCameraPosition = normalPosition;

            Vec3 bossOrigin = boss.getPosition(partialTick);
            if (bossOrigin.distanceToSqr(this.packetBossAnchor) > 64.0D) {
                bossOrigin = this.packetBossAnchor;
            }

            Vec3 playerGround = this.playerStartPosition == null
                    ? normalPosition.add(0.0D, -1.62D, 0.0D)
                    : this.playerStartPosition;
            Vec3 radial = playerGround.subtract(bossOrigin).multiply(1.0D, 0.0D, 1.0D);
            if (radial.lengthSqr() < 1.0E-5D) {
                radial = Vec3.directionFromRotation(0.0F, normalYaw + 180.0F)
                        .multiply(1.0D, 0.0D, 1.0D);
            }
            radial = radial.normalize();

            Vec3 right = new Vec3(-radial.z, 0.0D, radial.x);
            Vec3 bossForward = boss.getViewVector(partialTick).multiply(1.0D, 0.0D, 1.0D);
            if (bossForward.lengthSqr() < 1.0E-5D) {
                bossForward = radial;
            } else {
                bossForward = bossForward.normalize();
            }

            double cross = bossForward.x * radial.z - bossForward.z * radial.x;
            double side = Math.abs(cross) < 0.08D
                    ? ((this.bossUuid.getLeastSignificantBits() & 1L) == 0L ? 1.0D : -1.0D)
                    : Math.signum(cross);
            Vec3 sideVector = right.scale(side);

            double halfWidth = boss.getBbWidth() * 0.5D;
            double height = boss.getBbHeight();
            Vec3 bodyFocus = bossOrigin.add(0.0D, height * 0.48D, 0.0D);
            Vec3 crystalFocus = bossOrigin
                    .add(bossForward.scale(-0.42D))
                    .add(0.0D, height * 0.92D, 0.0D);
            Vec3 initialFocus = normalPosition.add(
                    Vec3.directionFromRotation(normalPitch, normalYaw).scale(8.0D)
            );

            Vec3 shoulder = normalPosition.add(radial.scale(0.85D)).add(0.0D, 0.38D, 0.0D);
            Vec3 lowApproach = bossOrigin
                    .add(radial.scale(halfWidth + 3.0D))
                    .add(sideVector.scale(0.38D))
                    .add(0.0D, Math.max(0.90D, height * 0.34D), 0.0D);
            Vec3 sideReveal = bossOrigin
                    .add(radial.scale(halfWidth + 2.15D))
                    .add(sideVector.scale(halfWidth + 0.95D))
                    .add(0.0D, Math.max(1.02D, height * 0.39D), 0.0D);
            Vec3 crystalReveal = bossOrigin
                    .add(bossForward.scale(-(halfWidth + 2.25D)))
                    .add(sideVector.scale(halfWidth + 1.25D))
                    .add(0.0D, height + 0.85D, 0.0D);
            Vec3 fullReveal = bossOrigin
                    .add(radial.scale(halfWidth + 4.40D))
                    .add(sideVector.scale(1.15D))
                    .add(0.0D, height + 1.32D, 0.0D);
            Vec3 lockOn = bossOrigin
                    .add(radial.scale(halfWidth + 4.05D))
                    .add(sideVector.scale(1.72D))
                    .add(0.0D, height * 0.78D, 0.0D);
            Vec3 frontal = bossOrigin
                    .add(radial.scale(halfWidth + 2.72D))
                    .add(sideVector.scale(-0.82D))
                    .add(0.0D, Math.max(0.82D, height * 0.31D), 0.0D);
            Vec3 recoil = bossOrigin
                    .add(radial.scale(halfWidth + 3.78D))
                    .add(sideVector.scale(-1.05D))
                    .add(0.0D, Math.max(1.04D, height * 0.39D), 0.0D);
            Vec3 titleHold = bossOrigin
                    .add(radial.scale(halfWidth + 4.28D))
                    .add(sideVector.scale(-1.42D))
                    .add(0.0D, Math.max(1.28D, height * 0.48D), 0.0D);

            this.positions = new Vec3[]{
                    normalPosition,
                    shoulder,
                    lowApproach,
                    sideReveal,
                    crystalReveal,
                    fullReveal,
                    lockOn,
                    frontal,
                    recoil,
                    titleHold
            };
            this.focuses = new Vec3[]{
                    initialFocus,
                    bodyFocus,
                    bossOrigin.add(0.0D, height * 0.38D, 0.0D),
                    bodyFocus,
                    crystalFocus,
                    bossOrigin.add(0.0D, height * 0.58D, 0.0D),
                    bossOrigin.add(0.0D, height * 0.55D, 0.0D),
                    bossOrigin.add(0.0D, height * 0.46D, 0.0D),
                    bossOrigin.add(0.0D, height * 0.52D, 0.0D),
                    bossOrigin.add(0.0D, height * 0.55D, 0.0D)
            };

            CameraPose returnLook = lookAt(titleHold, this.focuses[9], normalYaw, normalPitch);
            this.returnStartYaw = returnLook.yaw;
            this.returnStartPitch = returnLook.pitch;
            this.pathInitialized = true;
        }

        private PathSample samplePath(float elapsed, Vec3 normalPosition) {
            if (elapsed < 12.0F) {
                return catmullSample(0, 0, 1, 2, range(elapsed, 0.0F, 12.0F));
            }
            if (elapsed < 30.0F) {
                return catmullSample(0, 1, 2, 3, range(elapsed, 12.0F, 30.0F));
            }
            if (elapsed < 48.0F) {
                return catmullSample(1, 2, 3, 4, range(elapsed, 30.0F, 48.0F));
            }
            if (elapsed < 60.0F) {
                return catmullSample(2, 3, 4, 5, range(elapsed, 48.0F, 60.0F));
            }
            if (elapsed < 70.0F) {
                return catmullSample(3, 4, 5, 6, range(elapsed, 60.0F, 70.0F));
            }
            if (elapsed < 78.0F) {
                return lerpSample(5, 6, smootherStep(range(elapsed, 70.0F, 78.0F)));
            }
            if (elapsed < 91.0F) {
                return catmullSample(5, 6, 7, 8, range(elapsed, 78.0F, 91.0F));
            }
            if (elapsed < 106.0F) {
                return catmullSample(6, 7, 8, 9, range(elapsed, 91.0F, 106.0F));
            }
            if (elapsed < TITLE_END_TICK) {
                return lerpSample(8, 9, smootherStep(range(elapsed, 106.0F, TITLE_END_TICK)));
            }

            float t = smootherStep(range(elapsed, TITLE_END_TICK, RETURN_END_TICK));
            Vec3 position = this.positions[9].lerp(normalPosition, t);
            return new PathSample(position, this.focuses[9]);
        }

        private PathSample lerpSample(int from, int to, float progress) {
            return new PathSample(
                    this.positions[from].lerp(this.positions[to], progress),
                    this.focuses[from].lerp(this.focuses[to], progress)
            );
        }

        private PathSample catmullSample(int p0, int p1, int p2, int p3, float progress) {
            return new PathSample(
                    catmullRom(
                            this.positions[p0],
                            this.positions[p1],
                            this.positions[p2],
                            this.positions[p3],
                            progress
                    ),
                    catmullRom(
                            this.focuses[p0],
                            this.focuses[p1],
                            this.focuses[p2],
                            this.focuses[p3],
                            progress
                    )
            );
        }
    }
}

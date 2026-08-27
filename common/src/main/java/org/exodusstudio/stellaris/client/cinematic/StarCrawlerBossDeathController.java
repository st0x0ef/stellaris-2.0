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
import org.exodusstudio.stellaris.common.network.packets.StarCrawlerBossDeathStartPacket;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class StarCrawlerBossDeathController {
    public static final float REALIZATION_END_TICK = 14.0F;
    public static final float CRYSTAL_END_TICK = 48.0F;
    public static final float STAGGER_END_TICK = 78.0F;
    public static final float COLLAPSE_END_TICK = 104.0F;
    public static final float RELEASE_END_TICK = 122.0F;
    public static final float RETURN_END_TICK = 140.0F;

    private static final float COLLAPSE_IMPACT_TICK = 82.0F;
    private static final float CRYSTAL_RELEASE_TICK = 110.0F;

    private static final long NANOS_PER_TICK = 50_000_000L;
    private static final long CAMERA_CATCH_UP_NANOS = 220_000_000L;
    private static final float CLOCK_RESOLUTION_FALLBACK_TICKS = 2.0F;
    private static final int TARGET_LOAD_GRACE_TICKS = 20;
    private static final double TELEPORT_CLEANUP_DISTANCE_SQR = 20.0D * 20.0D;
    private static final double CAMERA_RADIUS = 0.14D;

    private static final Map<UUID, Timeline> TIMELINES = new HashMap<>();
    private static final Set<UUID> SKIPPED_TIMELINES = new HashSet<>();
    private static UUID visualOwner;
    private static ActiveVisual activeVisual;
    private static boolean initialized;
    private static long suppressCameraShakeUntilNanos;

    private StarCrawlerBossDeathController() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        ClientTickEvent.CLIENT_POST.register(StarCrawlerBossDeathController::clientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> clearAll());
        ClientLifecycleEvent.CLIENT_STOPPING.register(minecraft -> clearAll());
        ClientRawInputEvent.KEY_PRESSED.register(StarCrawlerBossDeathController::handleKey);
        ClientRawInputEvent.MOUSE_CLICKED_PRE.register(StarCrawlerBossDeathController::handleMouseClick);
    }

    public static void start(StarCrawlerBossDeathStartPacket packet) {
        if (packet == null || packet.bossUuid() == null || packet.durationTicks() <= 0) {
            return;
        }

        // A command-kill or another exceptional lethal hit can arrive while
        // the introduction is still logically active. Death always wins.
        StarCrawlerBossIntroController.finish(packet.bossUuid());
        StarCrawlerBossIntroController.suppressHudRevealForDeath(packet.bossUuid());
        StarCrawlerBossIntroController.suspendVisualForDeath();

        Minecraft minecraft = Minecraft.getInstance();
        long now = System.nanoTime();
        float elapsedAtSend = Math.max(
                0.0F,
                packet.serverGameTimeAtSend() - packet.serverStartGameTime()
        );
        float elapsedAtReceipt = Math.min(
                packet.durationTicks(),
                elapsedAtSend
        );

        Timeline previous = TIMELINES.get(packet.bossUuid());
        if (previous != null) {
            elapsedAtReceipt = Math.min(
                    packet.durationTicks(),
                    Math.max(
                            elapsedAtReceipt,
                            previous.elapsedTicks(packet.bossUuid(), now, 0.0F)
                    )
            );
        }

        Timeline timeline = new Timeline(
                packet.bossEntityId(),
                packet.serverStartGameTime(),
                packet.durationTicks(),
                elapsedAtReceipt,
                now
        );
        TIMELINES.put(packet.bossUuid(), timeline);

        if (SKIPPED_TIMELINES.contains(packet.bossUuid())) {
            return;
        }

        if (packet.bossUuid().equals(visualOwner)) {
            return;
        }

        discardVisual(now);
        if (!timeline.isActive(packet.bossUuid(), now, 0.0F)
                || elapsedAtReceipt >= packet.durationTicks()) {
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
                new Vec3(packet.bossX(), packet.bossY(), packet.bossZ()),
                packet.durationTicks()
        );
        releaseActionMappings(minecraft);
    }

    public static void finish(UUID bossUuid) {
        if (bossUuid == null) {
            return;
        }

        TIMELINES.remove(bossUuid);
        SKIPPED_TIMELINES.remove(bossUuid);
        if (bossUuid.equals(visualOwner)) {
            discardVisual(System.nanoTime());
        }
    }

    private static void clientTick(Minecraft minecraft) {
        long now = System.nanoTime();
        if (minecraft.player == null || minecraft.level == null) {
            clearAll();
            return;
        }

        Iterator<Map.Entry<UUID, Timeline>> iterator = TIMELINES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Timeline> entry = iterator.next();
            Entity trackedEntity = minecraft.level.getEntity(entry.getValue().bossEntityId);
            if (trackedEntity instanceof StarCrawlerBossEntity trackedBoss
                    && trackedBoss.getUUID().equals(entry.getKey())) {
                entry.getValue().missingTargetTicks = 0;
                StarCrawlerBossEntity.DeathCinematicState state =
                        trackedBoss.getDeathCinematicState();
                if (state == StarCrawlerBossEntity.DeathCinematicState.FINALIZED
                        || (state == StarCrawlerBossEntity.DeathCinematicState.ALIVE
                        && entry.getValue().elapsedTicks(entry.getKey(), now, 0.0F)
                        > TARGET_LOAD_GRACE_TICKS)) {
                    iterator.remove();
                }
            } else if (++entry.getValue().missingTargetTicks > TARGET_LOAD_GRACE_TICKS) {
                iterator.remove();
            }
        }

        SKIPPED_TIMELINES.retainAll(TIMELINES.keySet());

        if (visualOwner != null && !TIMELINES.containsKey(visualOwner)) {
            discardVisual(now);
        }

        if (visualOwner != null) {
            Timeline ownerTimeline = TIMELINES.get(visualOwner);
            if (ownerTimeline != null
                    && !ownerTimeline.isActive(visualOwner, now, 0.0F)) {
                discardVisual(now);
            }
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
        if (!(entity instanceof StarCrawlerBossEntity boss)
                || !boss.getUUID().equals(visual.bossUuid)
                || boss.isRemoved()) {
            visual.missingTargetTicks++;
            int missingTargetGrace = visual.pathInitialized
                    ? 2
                    : TARGET_LOAD_GRACE_TICKS;

            if (visual.missingTargetTicks > missingTargetGrace) {
                SKIPPED_TIMELINES.add(visual.bossUuid);
                discardVisual(now);
                return;
            }
        } else {
            visual.missingTargetTicks = 0;
        }

        if (visual.skipped) {
            return;
        }

        float elapsed = elapsedTicks(visual.bossUuid, now, 0.0F);
        if (!visual.lethalShakeStarted) {
            visual.lethalShakeStarted = true;
            int remaining = Mth.clamp((int) Math.ceil(8.0F - elapsed), 0, 8);
            if (remaining > 0) {
                ParasiteCameraShake.start(remaining, 0.48F * remaining / 8.0F);
                visual.shakeUntilNanos = Math.max(
                        visual.shakeUntilNanos,
                        now + remaining * NANOS_PER_TICK
                );
            }
        }

        if (!visual.collapseShakeStarted && elapsed >= COLLAPSE_IMPACT_TICK) {
            visual.collapseShakeStarted = true;
            int remaining = Mth.clamp((int) Math.ceil(98.0F - elapsed), 0, 16);
            if (remaining > 0) {
                ParasiteCameraShake.start(remaining, 2.20F * remaining / 16.0F);
                visual.shakeUntilNanos = Math.max(
                        visual.shakeUntilNanos,
                        now + remaining * NANOS_PER_TICK
                );
            }
        }

        if (!visual.releaseShakeStarted && elapsed >= CRYSTAL_RELEASE_TICK) {
            visual.releaseShakeStarted = true;
            int remaining = Mth.clamp((int) Math.ceil(121.0F - elapsed), 0, 11);
            if (remaining > 0) {
                ParasiteCameraShake.start(remaining, 0.88F * remaining / 11.0F);
                visual.shakeUntilNanos = Math.max(
                        visual.shakeUntilNanos,
                        now + remaining * NANOS_PER_TICK
                );
            }
        }
    }

    public static boolean isAuthoritativelyLocked() {
        return !TIMELINES.isEmpty();
    }

    public static boolean isVisualActive() {
        ActiveVisual visual = activeVisual;
        if (visual == null || visual.skipped) {
            return false;
        }

        Timeline timeline = TIMELINES.get(visual.bossUuid);
        long now = System.nanoTime();
        return timeline != null
                && timeline.isActive(visual.bossUuid, now, 0.0F);
    }

    public static boolean isActiveFor(UUID bossUuid) {
        Timeline timeline = bossUuid == null ? null : TIMELINES.get(bossUuid);
        return timeline != null
                && timeline.isActive(bossUuid, System.nanoTime(), 0.0F);
    }

    public static float getDeathElapsedTicks(float partialTick) {
        UUID owner = visualOwner;
        return owner == null
                ? -1.0F
                : elapsedTicks(owner, System.nanoTime(), partialTick);
    }

    public static float getDeathVisualTicks(
            StarCrawlerBossEntity boss,
            float partialTick
    ) {
        if (boss == null) {
            return 0.0F;
        }
        Timeline timeline = TIMELINES.get(boss.getUUID());
        return timeline == null
                ? boss.getDeathCinematicTicks()
                : timeline.elapsedTicks(boss.getUUID(), System.nanoTime(), partialTick);
    }

    public static float hudFade(UUID bossUuid) {
        Timeline timeline = bossUuid == null ? null : TIMELINES.get(bossUuid);
        if (timeline == null) {
            return 1.0F;
        }

        float elapsed = timeline.elapsedTicks(bossUuid, System.nanoTime(), 0.0F);
        float fadeStart = Math.min(RELEASE_END_TICK, timeline.durationTicks - 18.0F);
        return 1.0F - smootherStep(range(elapsed, fadeStart, timeline.durationTicks - 2.0F));
    }

    public static float hudImpact(UUID bossUuid) {
        Timeline timeline = bossUuid == null ? null : TIMELINES.get(bossUuid);
        if (timeline == null) {
            return 0.0F;
        }
        float elapsed = timeline.elapsedTicks(bossUuid, System.nanoTime(), 0.0F);
        return 1.0F - smootherStep(range(elapsed, 0.0F, 18.0F));
    }

    public static CameraPose sampleCamera(
            Vec3 normalPosition,
            float normalYaw,
            float normalPitch,
            float partialTick
    ) {
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
        if (timeline == null) {
            return null;
        }

        float elapsed = timeline.elapsedTicks(visual.bossUuid, now, partialTick);
        if (elapsed >= timeline.durationTicks) {
            return null;
        }
        Entity entity = minecraft.level.getEntity(visual.bossEntityId);
        StarCrawlerBossEntity boss = entity instanceof StarCrawlerBossEntity candidate
                && candidate.getUUID().equals(visual.bossUuid)
                && !candidate.isRemoved()
                ? candidate
                : null;

        if (!visual.pathInitialized) {
            if (boss == null) {
                return null;
            }
            visual.initializePath(boss, normalPosition, normalYaw, normalPitch, partialTick);
        }

        PathSample sample = visual.samplePath(elapsed, normalPosition);
        float returnStart = visual.returnStartTick();
        float returnProgress = range(elapsed, returnStart, visual.durationTicks);

        Vec3 bossDisplacement =
                visual.bossDisplacement(boss, partialTick)
                        .scale(
                                1.0F
                                        - smootherStep(returnProgress)
                        );

        if (bossDisplacement.lengthSqr() > 1.0E-6D) {
            sample = new PathSample(
                    sample.position.add(bossDisplacement),
                    sample.focus.add(bossDisplacement)
            );
        }

        Vec3 safePosition = collisionSafe(
                minecraft,
                boss,
                sample.focus,
                sample.position,
                normalPosition,
                visual.lastCameraPosition,
                returnProgress
        );
        CameraPose lookPose = lookAt(safePosition, sample.focus, normalYaw, normalPitch);
        float yaw = lookPose.yaw;
        float pitch = lookPose.pitch;

        if (returnProgress > 0.0F) {
            float eased = smootherStep(returnProgress);
            yaw = Mth.rotLerp(eased, visual.returnStartYaw, normalYaw);
            pitch = Mth.lerp(eased, visual.returnStartPitch, normalPitch);
            float convergence = smootherStep(range(returnProgress, 0.55F, 1.0F));
            safePosition = safePosition.lerp(normalPosition, convergence);
        }

        if (elapsed >= visual.durationTicks - 0.05F) {
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
        return !SKIPPED_TIMELINES.isEmpty()
                || System.nanoTime() < suppressCameraShakeUntilNanos;
    }

    private static EventResult handleKey(Minecraft minecraft, int action, KeyEvent keyEvent) {
        if (action == GLFW.GLFW_PRESS && isVisualActive()
                && (keyEvent.key() == GLFW.GLFW_KEY_SPACE
                || keyEvent.key() == GLFW.GLFW_KEY_ESCAPE)) {
            skipVisual();
            return EventResult.interruptTrue();
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
        return minecraft != null
                && (minecraft.options.keyAttack.matches(event)
                || minecraft.options.keyUse.matches(event)
                || minecraft.options.keyPickItem.matches(event)
                || minecraft.options.keyDrop.matches(event)
                || minecraft.options.keySwapOffhand.matches(event)
                || minecraft.options.keyInventory.matches(event));
    }

    private static void skipVisual() {
        ActiveVisual visual = activeVisual;
        if (visual == null || visual.skipped) {
            return;
        }
        visual.skipped = true;
        SKIPPED_TIMELINES.add(visual.bossUuid);
        suppressCameraShakeUntilNanos = Math.max(
                suppressCameraShakeUntilNanos,
                visual.shakeUntilNanos
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
            // Empty on purpose do not touch!!!
        }
    }

    private static float elapsedTicks(UUID bossUuid, long now, float partialTick) {
        Timeline timeline = TIMELINES.get(bossUuid);
        return timeline == null
                ? -1.0F
                : timeline.elapsedTicks(bossUuid, now, partialTick);
    }

    private static void discardVisual(long now) {
        if (activeVisual != null && activeVisual.shakeUntilNanos > now) {
            suppressCameraShakeUntilNanos = Math.max(
                    suppressCameraShakeUntilNanos,
                    activeVisual.shakeUntilNanos
            );
        }
        activeVisual = null;
        visualOwner = null;
    }

    private static void clearAll() {
        discardVisual(System.nanoTime());
        TIMELINES.clear();
        SKIPPED_TIMELINES.clear();
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
        float focusWeight = 1.0F - smootherStep(range(returnProgress, 0.30F, 1.0F));
        if (focusWeight > 0.0F) {
            result = result.lerp(clipCameraRay(minecraft, focus, result), focusWeight);
        }
        if (previous != null && previous.distanceToSqr(result) > 1.0E-5D) {
            result = clipCameraRay(minecraft, previous, result);
        }
        if (boss != null) {
            result = pushOutsideBoss(boss, result);
        }
        if (cameraSpaceIsFree(level, result)) {
            return result;
        }

        for (int i = 1; i <= 8; i++) {
            Vec3 raised = result.add(0.0D, i * 0.16D, 0.0D);
            if (boss != null) {
                raised = pushOutsideBoss(boss, raised);
            }
            if (cameraSpaceIsFree(level, raised)) {
                return raised;
            }
        }
        for (int i = 1; i <= 8; i++) {
            Vec3 towardFocus = result.lerp(focus, i / 10.0D);
            if (boss != null) {
                towardFocus = pushOutsideBoss(boss, towardFocus);
            }
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
        return towardFrom.lengthSqr() < 1.0E-6D
                ? hit.getLocation()
                : hit.getLocation().add(towardFrom.normalize().scale(0.22D));
    }

    private static boolean cameraSpaceIsFree(Level level, Vec3 position) {
        return level.noCollision(new AABB(
                position.x - CAMERA_RADIUS,
                position.y - CAMERA_RADIUS,
                position.z - CAMERA_RADIUS,
                position.x + CAMERA_RADIUS,
                position.y + CAMERA_RADIUS,
                position.z + CAMERA_RADIUS
        ));
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

    private static CameraPose lookAt(Vec3 position, Vec3 target, float fallbackYaw, float fallbackPitch) {
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
        double t = smootherStep(progress);
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
        return 0.5D * (2.0D * p1
                + (-p0 + p2) * t
                + (2.0D * p0 - 5.0D * p1 + 4.0D * p2 - p3) * t2
                + (-p0 + 3.0D * p1 - 3.0D * p2 + p3) * t3);
    }

    private static float range(float value, float start, float end) {
        if (end <= start) {
            return value >= end ? 1.0F : 0.0F;
        }
        return Mth.clamp((value - start) / (end - start), 0.0F, 1.0F);
    }

    private static float smootherStep(float value) {
        float t = Mth.clamp(value, 0.0F, 1.0F);
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }

    public record CameraPose(Vec3 position, float yaw, float pitch) {
    }

    private static final class Timeline {
        private final int bossEntityId;
        private final long serverStartGameTime;
        private final int durationTicks;
        private final float receiptElapsedTicks;
        private final long receiptNanos;

        private boolean resolvedAuthoritativeClock;
        private int lastSyncedTicks = Integer.MIN_VALUE;
        private long syncedTickObservedNanos;
        private float lastAuthoritativeElapsed;
        private float lastReturnedElapsed;
        private int missingTargetTicks;

        private Timeline(
                int bossEntityId,
                long serverStartGameTime,
                int durationTicks,
                float receiptElapsedTicks,
                long receiptNanos
        ) {
            this.bossEntityId = bossEntityId;
            this.serverStartGameTime = serverStartGameTime;
            this.durationTicks = durationTicks;
            this.receiptElapsedTicks = Math.max(0.0F, receiptElapsedTicks);
            this.receiptNanos = receiptNanos;
            this.lastReturnedElapsed = this.receiptElapsedTicks;
        }

        private float elapsedTicks(UUID bossUuid, long now, float partialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level != null) {
                Entity entity = minecraft.level.getEntity(this.bossEntityId);
                if (entity instanceof StarCrawlerBossEntity boss
                        && boss.getUUID().equals(bossUuid)
                        && !boss.isRemoved()
                        && boss.getDeathCinematicStartGameTime() == this.serverStartGameTime
                        && boss.getDeathCinematicState()
                        != StarCrawlerBossEntity.DeathCinematicState.ALIVE) {
                    int syncedTicks = Math.max(0, boss.getDeathCinematicTicks());
                    if (!this.resolvedAuthoritativeClock
                            || syncedTicks != this.lastSyncedTicks) {
                        this.lastSyncedTicks = syncedTicks;
                        this.syncedTickObservedNanos = now;
                    }

                    float interpolation = Mth.clamp(
                            (now - this.syncedTickObservedNanos) / (float) NANOS_PER_TICK,
                            0.0F,
                            1.0F
                    );
                    float authoritative = syncedTicks + interpolation;
                    this.resolvedAuthoritativeClock = true;
                    this.lastAuthoritativeElapsed = authoritative;
                    this.lastReturnedElapsed = Math.max(
                            this.lastReturnedElapsed,
                            authoritative
                    );
                    return this.lastReturnedElapsed;
                }
            }

            float fallback;
            if (this.resolvedAuthoritativeClock) {
                float interpolation = Mth.clamp(
                        (now - this.syncedTickObservedNanos) / (float) NANOS_PER_TICK,
                        0.0F,
                        1.0F
                );
                fallback = this.lastSyncedTicks + interpolation;
            } else {
                float loadAdvance = Mth.clamp(
                        (now - this.receiptNanos) / (float) NANOS_PER_TICK,
                        0.0F,
                        CLOCK_RESOLUTION_FALLBACK_TICKS
                );
                fallback = this.receiptElapsedTicks + loadAdvance;
            }

            this.lastReturnedElapsed = Math.max(this.lastReturnedElapsed, fallback);
            return this.lastReturnedElapsed;
        }

        private boolean isActive(UUID bossUuid, long now, float partialTick) {
            return elapsedTicks(bossUuid, now, partialTick) < this.durationTicks;
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
        private final float durationTicks;

        private boolean skipped;
        private boolean pathInitialized;
        private boolean lethalShakeStarted;
        private boolean collapseShakeStarted;
        private boolean releaseShakeStarted;
        private int missingTargetTicks;
        private long shakeUntilNanos;

        private Vec3 activationPosition;
        private Vec3 trackedBossOrigin;
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
                Vec3 packetBossAnchor,
                int durationTicks
        ) {
            this.bossEntityId = bossEntityId;
            this.bossUuid = bossUuid;
            this.dimension = dimension;
            this.playerStartPosition = playerStartPosition;
            this.packetBossAnchor = packetBossAnchor;
            this.durationTicks = durationTicks;
        }

        private float returnStartTick() {
            return Math.min(RELEASE_END_TICK, Math.max(0.0F, this.durationTicks - 18.0F));
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

            Vec3 liveBossOrigin = boss.getPosition(partialTick);
            this.trackedBossOrigin = liveBossOrigin;

            Vec3 bossOrigin = liveBossOrigin;
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
            bossForward = bossForward.lengthSqr() < 1.0E-5D ? radial : bossForward.normalize();
            double cross = bossForward.x * radial.z - bossForward.z * radial.x;
            double side = Math.abs(cross) < 0.08D
                    ? ((this.bossUuid.getMostSignificantBits() & 1L) == 0L ? 1.0D : -1.0D)
                    : Math.signum(cross);
            Vec3 sideVector = right.scale(side);

            double halfWidth = boss.getBbWidth() * 0.5D;
            double height = boss.getBbHeight();
            Vec3 bodyFocus = bossOrigin.add(0.0D, height * 0.47D, 0.0D);
            Vec3 crystalFocus = bossOrigin
                    .add(bossForward.scale(-0.48D))
                    .add(0.0D, height * 0.91D, 0.0D);
            Vec3 groundFocus = bossOrigin.add(0.0D, height * 0.22D, 0.0D);
            Vec3 playerFocus = playerGround.add(0.0D, 1.0D, 0.0D);

            Vec3 hitHold = normalPosition.add(sideVector.scale(0.32D)).add(0.0D, 0.12D, 0.0D);
            Vec3 closeWitness = bossOrigin
                    .add(radial.scale(halfWidth + 3.25D))
                    .add(sideVector.scale(0.72D))
                    .add(0.0D, height * 0.43D, 0.0D);
            Vec3 crystalClose = bossOrigin
                    .add(bossForward.scale(-(halfWidth + 1.85D)))
                    .add(sideVector.scale(1.15D))
                    .add(0.0D, height + 0.62D, 0.0D);
            Vec3 lowStagger = bossOrigin
                    .add(radial.scale(halfWidth + 3.05D))
                    .add(sideVector.scale(-1.65D))
                    .add(0.0D, Math.max(0.72D, height * 0.24D), 0.0D);
            Vec3 collapseTrack = bossOrigin
                    .add(radial.scale(halfWidth + 4.65D))
                    .add(sideVector.scale(-2.15D))
                    .add(0.0D, Math.max(1.0D, height * 0.38D), 0.0D);
            Vec3 wideAftermath = bossOrigin
                    .add(radial.scale(halfWidth + 7.1D))
                    .add(sideVector.scale(2.5D))
                    .add(0.0D, height + 1.5D, 0.0D);
            Vec3 finalRelease = bossOrigin
                    .add(radial.scale(halfWidth + 8.35D))
                    .add(sideVector.scale(0.65D))
                    .add(0.0D, height + 2.55D, 0.0D);

            this.positions = new Vec3[]{
                    normalPosition,
                    hitHold,
                    closeWitness,
                    crystalClose,
                    lowStagger,
                    collapseTrack,
                    wideAftermath,
                    finalRelease
            };
            this.focuses = new Vec3[]{
                    normalPosition.add(Vec3.directionFromRotation(normalPitch, normalYaw).scale(7.0D)),
                    bodyFocus,
                    bodyFocus,
                    crystalFocus,
                    bossOrigin.add(0.0D, height * 0.58D, 0.0D),
                    groundFocus,
                    groundFocus.lerp(playerFocus, 0.18D),
                    groundFocus.lerp(crystalFocus, 0.42D)
            };

            CameraPose returnLook = lookAt(finalRelease, this.focuses[7], normalYaw, normalPitch);
            this.returnStartYaw = returnLook.yaw;
            this.returnStartPitch = returnLook.pitch;
            this.pathInitialized = true;
        }

        private Vec3 bossDisplacement(
                StarCrawlerBossEntity boss,
                float partialTick
        ) {
            if (boss == null
                    || this.trackedBossOrigin == null) {

                return Vec3.ZERO;
            }

            Vec3 displacement =
                    boss.getPosition(partialTick)
                            .subtract(this.trackedBossOrigin);

            /* A real teleport should never drag the detached camera away. */
            return displacement.lengthSqr() <= 64.0D
                    ? displacement
                    : Vec3.ZERO;
        }

        private PathSample samplePath(float elapsed, Vec3 normalPosition) {
            if (elapsed < 10.0F) {
                return lerpSample(0, 1, smootherStep(range(elapsed, 0.0F, 10.0F)));
            }
            if (elapsed < 24.0F) {
                return catmullSample(0, 1, 2, 3, range(elapsed, 10.0F, 24.0F));
            }
            if (elapsed < CRYSTAL_END_TICK) {
                return catmullSample(1, 2, 3, 4, range(elapsed, 24.0F, CRYSTAL_END_TICK));
            }
            if (elapsed < STAGGER_END_TICK) {
                return catmullSample(2, 3, 4, 5, range(elapsed, CRYSTAL_END_TICK, STAGGER_END_TICK));
            }
            if (elapsed < COLLAPSE_END_TICK) {
                return catmullSample(3, 4, 5, 6, range(elapsed, STAGGER_END_TICK, COLLAPSE_END_TICK));
            }
            if (elapsed < returnStartTick()) {
                return catmullSample(4, 5, 6, 7, range(elapsed, COLLAPSE_END_TICK, returnStartTick()));
            }

            float t = smootherStep(range(elapsed, returnStartTick(), this.durationTicks));
            return new PathSample(
                    this.positions[7].lerp(normalPosition, t),
                    this.focuses[7]
            );
        }

        private PathSample lerpSample(int from, int to, float progress) {
            return new PathSample(
                    this.positions[from].lerp(this.positions[to], progress),
                    this.focuses[from].lerp(this.focuses[to], progress)
            );
        }

        private PathSample catmullSample(int p0, int p1, int p2, int p3, float progress) {
            return new PathSample(
                    catmullRom(this.positions[p0], this.positions[p1], this.positions[p2], this.positions[p3], progress),
                    catmullRom(this.focuses[p0], this.focuses[p1], this.focuses[p2], this.focuses[p3], progress)
            );
        }
    }
}

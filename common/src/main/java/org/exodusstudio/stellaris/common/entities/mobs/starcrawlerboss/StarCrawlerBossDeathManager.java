package org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.network.packets.StarCrawlerBossDeathEndPacket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class StarCrawlerBossDeathManager {

    private static final double POSITION_EPSILON_SQR = 0.0025D;
    private static final double TELEPORT_HORIZONTAL_DISTANCE_SQR =
            16.0D * 16.0D;
    private static final double TELEPORT_VERTICAL_DISTANCE = 5.5D;
    private static final double FALLBACK_DROP_DISTANCE = 7.0D;
    private static final int SAFE_GROUND_PROBE_DEPTH = 8;
    private static final long LOCK_FAILSAFE_MARGIN_TICKS = 80L;

    private static final Map<UUID, ParticipantLock> PARTICIPANTS =
            new HashMap<>();

    private StarCrawlerBossDeathManager() {
    }

    public static boolean tryClaim(
            StarCrawlerBossEntity boss,
            ServerPlayer player
    ) {
        
        if (player.isPassenger()) {
            return false;
        }

        UUID playerUuid =
                player.getUUID();

        boolean safeGrounded =
                isSafeGrounded(player);

        Vec3 safeGround =
                safeGrounded
                        ? player.position()
                        : findSafeGroundBelow(player);

        if (safeGround == null
                && player.gameMode() != GameType.CREATIVE) {

            return false;
        }

        if (PARTICIPANTS.containsKey(playerUuid)
                || StarCrawlerBossIntroManager.isClaimed(playerUuid)) {

            return false;
        }

        Vec3 position =
                player.position();

        PARTICIPANTS.put(
                playerUuid,
                new ParticipantLock(
                        boss.getUUID(),
                        player.level().dimension(),
                        position.x,
                        position.z,
                        position.y,
                        safeGround,
                        safeGround == null,
                        player.level().getGameTime()
                                + StarCrawlerBossEntity.DEATH_CINEMATIC_DURATION_TICKS
                                + LOCK_FAILSAFE_MARGIN_TICKS
                )
        );

        stopHorizontalMovement(player);

        return true;
    }

    public static boolean isClaimed(
            UUID playerUuid
    ) {
        return PARTICIPANTS.containsKey(playerUuid);
    }

    public static boolean isClaimedBy(
            UUID playerUuid,
            UUID bossUuid
    ) {
        ParticipantLock lock =
                PARTICIPANTS.get(playerUuid);

        return lock != null
                && lock.bossUuid.equals(bossUuid);
    }

    public static boolean shouldProtect(
            LivingEntity entity,
            DamageSource source
    ) {
        return entity instanceof ServerPlayer player
                && PARTICIPANTS.containsKey(player.getUUID())
                && !source.is(
                DamageTypeTags.BYPASSES_INVULNERABILITY
        );
    }

    public static boolean shouldBlockAttack(
            Player player
    ) {
        return player instanceof ServerPlayer serverPlayer
                && PARTICIPANTS.containsKey(
                serverPlayer.getUUID()
        );
    }

    public static void tickPlayer(
            ServerPlayer player
    ) {
        ParticipantLock lock =
                PARTICIPANTS.get(
                        player.getUUID()
                );

        if (lock == null) {
            return;
        }

        if (player.level().getGameTime()
                >= lock.failsafeReleaseGameTime) {

            releasePlayer(player, true);
            return;
        }

        if (!player.isAlive()
                || player.isRemoved()
                || player.isSpectator()
                || player.isPassenger()
                || !player.level()
                .dimension()
                .equals(lock.dimension)) {

            releasePlayer(player, true);
            return;
        }

        ServerLevel bossLevel =
                player.level()
                        .getServer()
                        .getLevel(lock.dimension);

        Entity bossEntity =
                bossLevel == null
                        ? null
                        : bossLevel.getEntityInAnyDimension(
                                lock.bossUuid
                        );

        if (!(bossEntity instanceof StarCrawlerBossEntity boss)
                || !boss.isDeathCinematicPlaying()) {

            releasePlayer(player, true);
            return;
        }

        double xDifference =
                player.getX()
                        - lock.lockedX;

        double zDifference =
                player.getZ()
                        - lock.lockedZ;

        double horizontalDifferenceSqr =
                xDifference * xDifference
                        + zDifference * zDifference;

        double verticalStep =
                Math.abs(
                        player.getY()
                                - lock.lastObservedY
                );

        if (horizontalDifferenceSqr
                > TELEPORT_HORIZONTAL_DISTANCE_SQR
                || verticalStep
                > TELEPORT_VERTICAL_DISTANCE) {

            releasePlayer(player, true);
            return;
        }

        Vec3 velocity =
                player.getDeltaMovement();

        boolean needsFallback =
                lock.lastSafeGround != null
                        && (
                        player.getY()
                                < lock.lastSafeGround.y
                                - FALLBACK_DROP_DISTANCE
                                || player.isInLava()
                                || player.getY()
                                < player.level().getMinY()
                                + 4.0D
                );

        if (needsFallback) {
            Vec3 fallback =
                    lock.lastSafeGround;

            if (!isSafeStandPosition(player, fallback)) {
                fallback = findSafeGroundBelow(player);
            }

            if (fallback == null) {
                releasePlayer(player, true);
                return;
            }

            player.teleportTo(
                    fallback.x,
                    fallback.y,
                    fallback.z
            );

            player.setDeltaMovement(Vec3.ZERO);
            player.resetFallDistance();

            lock.lockedX = fallback.x;
            lock.lockedZ = fallback.z;
            lock.lastObservedY = fallback.y;

            return;
        }

        if (horizontalDifferenceSqr
                > POSITION_EPSILON_SQR) {

            player.teleportTo(
                    lock.lockedX,
                    lock.lockVertical
                            ? lock.lockedY
                            : player.getY(),
                    lock.lockedZ
            );
        } else if (lock.lockVertical
                && Math.abs(player.getY() - lock.lockedY)
                > POSITION_EPSILON_SQR) {

            player.teleportTo(
                    lock.lockedX,
                    lock.lockedY,
                    lock.lockedZ
            );
        }

        if (lock.lockVertical) {
            player.setDeltaMovement(Vec3.ZERO);
            player.resetFallDistance();
        } else {
            player.setDeltaMovement(
                    0.0D,
                    velocity.y,
                    0.0D
            );
        }

        player.hurtMarked = true;
        lock.lastObservedY = player.getY();

        if (isSafeGrounded(player)) {
            lock.lastSafeGround =
                    new Vec3(
                            lock.lockedX,
                            player.getY(),
                            lock.lockedZ
                    );
        }
    }

    public static void releasePlayer(
            ServerPlayer player,
            boolean notifyClient
    ) {
        ParticipantLock lock =
                PARTICIPANTS.remove(
                        player.getUUID()
                );

        if (lock != null
                && notifyClient) {

            player.resetFallDistance();

            NetworkManager.sendToPlayer(
                    player,
                    new StarCrawlerBossDeathEndPacket(
                            lock.bossUuid
                    )
            );
        }
    }

    public static void releasePlayer(
            UUID playerUuid,
            MinecraftServer server,
            boolean notifyClient
    ) {
        ParticipantLock lock =
                PARTICIPANTS.remove(playerUuid);

        if (lock == null
                || !notifyClient) {

            return;
        }

        ServerPlayer player =
                server.getPlayerList()
                        .getPlayer(playerUuid);

        if (player != null) {
            player.resetFallDistance();

            NetworkManager.sendToPlayer(
                    player,
                    new StarCrawlerBossDeathEndPacket(
                            lock.bossUuid
                    )
            );
        }
    }

    public static void releaseBoss(
            StarCrawlerBossEntity boss,
            boolean notifyClients
    ) {
        if (!(boss.level()
                instanceof ServerLevel serverLevel)) {

            return;
        }

        UUID bossUuid =
                boss.getUUID();

        Iterator<Map.Entry<UUID, ParticipantLock>> iterator =
                PARTICIPANTS.entrySet()
                        .iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, ParticipantLock> entry =
                    iterator.next();

            ParticipantLock lock =
                    entry.getValue();

            if (!lock.bossUuid.equals(bossUuid)) {
                continue;
            }

            iterator.remove();

            if (!notifyClients) {
                continue;
            }

            ServerPlayer player =
                    serverLevel.getServer()
                            .getPlayerList()
                            .getPlayer(entry.getKey());

            if (player != null) {
                player.resetFallDistance();

                NetworkManager.sendToPlayer(
                        player,
                        new StarCrawlerBossDeathEndPacket(
                                bossUuid
                        )
                );
            }
        }
    }

    public static void clear(
            MinecraftServer server
    ) {
        PARTICIPANTS.clear();
    }

    private static boolean isSafeGrounded(
            ServerPlayer player
    ) {
        return player.onGround()
                && !player.isInLava();
    }

    private static Vec3 findSafeGroundBelow(
            ServerPlayer player
    ) {
        ServerLevel level =
                (ServerLevel) player.level();

        int x = BlockPos.containing(player.position()).getX();
        int z = BlockPos.containing(player.position()).getZ();
        int startY = BlockPos.containing(player.position()).getY();
        int minimumY = Math.max(
                level.getMinY() + 1,
                startY - SAFE_GROUND_PROBE_DEPTH
        );

        BlockPos.MutableBlockPos position =
                new BlockPos.MutableBlockPos();

        for (int standY = startY;
             standY >= minimumY;
             standY--) {

            if (isSafeStandPosition(
                    level,
                    position,
                    x,
                    standY,
                    z
            )) {
                return new Vec3(
                        player.getX(),
                        standY,
                        player.getZ()
                );
            }
        }

        return null;
    }

    private static boolean isSafeStandPosition(
            ServerPlayer player,
            Vec3 position
    ) {
        if (position == null) {
            return false;
        }

        BlockPos blockPosition =
                BlockPos.containing(position);

        return isSafeStandPosition(
                (ServerLevel) player.level(),
                new BlockPos.MutableBlockPos(),
                blockPosition.getX(),
                blockPosition.getY(),
                blockPosition.getZ()
        );
    }

    private static boolean isSafeStandPosition(
            ServerLevel level,
            BlockPos.MutableBlockPos position,
            int x,
            int standY,
            int z
    ) {
        position.set(x, standY - 1, z);

        if (!level.getBlockState(position)
                .isFaceSturdy(level, position, Direction.UP)) {
            return false;
        }

        position.set(x, standY, z);

        if (!level.getBlockState(position)
                .getCollisionShape(level, position)
                .isEmpty()
                || !level.getFluidState(position).is(Fluids.EMPTY)) {
            return false;
        }

        position.set(x, standY + 1, z);

        return level.getBlockState(position)
                .getCollisionShape(level, position)
                .isEmpty()
                && level.getFluidState(position).is(Fluids.EMPTY);
    }

    private static void stopHorizontalMovement(
            ServerPlayer player
    ) {
        Vec3 velocity =
                player.getDeltaMovement();

        player.setDeltaMovement(
                0.0D,
                velocity.y,
                0.0D
        );

        player.hurtMarked = true;
    }

    private static final class ParticipantLock {

        private final UUID bossUuid;
        private final net.minecraft.resources.ResourceKey<Level> dimension;

        private double lockedX;
        private double lockedZ;
        private final double lockedY;
        private double lastObservedY;

        private Vec3 lastSafeGround;
        private final boolean lockVertical;
        private final long failsafeReleaseGameTime;

        private ParticipantLock(
                UUID bossUuid,
                net.minecraft.resources.ResourceKey<Level> dimension,
                double lockedX,
                double lockedZ,
                double lastObservedY,
                Vec3 lastSafeGround,
                boolean lockVertical,
                long failsafeReleaseGameTime
        ) {
            this.bossUuid = bossUuid;
            this.dimension = dimension;
            this.lockedX = lockedX;
            this.lockedZ = lockedZ;
            this.lockedY = lastObservedY;
            this.lastObservedY = lastObservedY;
            this.lastSafeGround = lastSafeGround;
            this.lockVertical = lockVertical;
            this.failsafeReleaseGameTime =
                    failsafeReleaseGameTime;
        }
    }
}

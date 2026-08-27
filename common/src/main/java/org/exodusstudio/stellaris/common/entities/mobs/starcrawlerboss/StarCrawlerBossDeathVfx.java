package org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss;

import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.network.packets.ParasiteCameraShakePacket;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class StarCrawlerBossDeathVfx {

    private static final double TAU = Math.PI * 2.0D;
    private static final double WORLD_VIEW_RADIUS_SQR = 72.0D * 72.0D;

    private StarCrawlerBossDeathVfx() {
    }

    public static void lethalImpact(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            Iterable<ServerPlayer> participants
    ) {
        List<ServerPlayer> lockedPlayers =
                copyPlayers(participants);

        List<ServerPlayer> viewers =
                collectViewers(
                        level,
                        boss,
                        lockedPlayers
                );

        Vec3 body =
                boss.position()
                        .add(
                                0.0D,
                                boss.getBbHeight() * 0.58D,
                                0.0D
                        );

        Vec3 floor =
                boss.position()
                        .add(0.0D, 0.16D, 0.0D);

        ring(
                level,
                viewers,
                ParticleTypes.END_ROD,
                body,
                0.85D,
                14,
                0.0D,
                0.18D
        );

        ring(
                level,
                viewers,
                ParticleTypes.REVERSE_PORTAL,
                body,
                1.75D,
                18,
                0.18D,
                0.22D
        );

        ring(
                level,
                viewers,
                ParticleTypes.DUST_PLUME,
                floor,
                1.35D,
                16,
                0.0D,
                0.025D
        );

        sendParticles(
                level,
                viewers,
                ParticleTypes.ELECTRIC_SPARK,
                body,
                28,
                0.95D,
                0.72D,
                0.95D,
                0.15D
        );

        sendParticles(
                level,
                viewers,
                ParticleTypes.POOF,
                floor.add(0.0D, 0.22D, 0.0D),
                24,
                1.15D,
                0.28D,
                1.15D,
                0.10D
        );

        sound(
                level,
                viewers,
                boss.position(),
                SoundEvents.WARDEN_ATTACK_IMPACT,
                1.65F,
                0.46F
        );

        sound(
                level,
                viewers,
                crystalPosition(boss),
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                0.88F,
                1.32F
        );

        cameraShake(
                lockedPlayers,
                10,
                0.62F
        );
    }

    public static void tick(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            int tick,
            Iterable<ServerPlayer> participants
    ) {
        List<ServerPlayer> lockedPlayers =
                copyPlayers(participants);

        List<ServerPlayer> viewers =
                collectViewers(
                        level,
                        boss,
                        lockedPlayers
                );

        Vec3 crystal =
                crystalPosition(boss);

        if (tick >= 8
                && tick <= 72
                && (tick & 1) == 0) {

            double instability =
                    clamp01(
                            (tick - 8.0D) / 64.0D
                    );

            double pulse =
                    0.58D
                            + Math.sin(tick * 0.56D)
                            * (0.10D + instability * 0.20D);

            if ((tick & 7) == 0) {
                ring(
                        level,
                        viewers,
                        (tick & 15) == 0
                                ? ParticleTypes.ELECTRIC_SPARK
                                : ParticleTypes.END_ROD,
                        crystal,
                        pulse,
                        8 + (int) (instability * 4.0D),
                        tick * 0.31D,
                        0.12D + instability * 0.12D
                );
            }

            sendParticles(
                    level,
                    viewers,
                    (tick % 6 == 0)
                            ? ParticleTypes.ELECTRIC_SPARK
                            : ParticleTypes.END_ROD,
                    crystal,
                    2 + (int) (instability * 3.0D),
                    0.30D + instability * 0.34D,
                    0.25D + instability * 0.34D,
                    0.30D + instability * 0.34D,
                    0.035D + instability * 0.055D
            );

            if (tick % 8 == 0) {
                escapingSpokes(
                        level,
                        viewers,
                        crystal,
                        0.75D + instability * 2.25D,
                        4 + (int) (instability * 3.0D),
                        tick * 0.17D
                );
            }
        }

        if (tick == 16) {
            sound(
                    level,
                    viewers,
                    crystal,
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    0.82F,
                    0.76F
            );
        }

        if (tick == 34) {
            sound(
                    level,
                    viewers,
                    crystal,
                    SoundEvents.AMETHYST_BLOCK_RESONATE,
                    1.05F,
                    0.58F
            );

            crystalFracture(
                    level,
                    viewers,
                    crystal,
                    0.72D
            );
        }

        if (tick == 52) {
            sound(
                    level,
                    viewers,
                    boss.position(),
                    SoundEvents.WARDEN_DEATH,
                    1.45F,
                    0.54F
            );
        }

        if (tick == 64) {
            sound(
                    level,
                    viewers,
                    crystal,
                    SoundEvents.END_PORTAL_FRAME_FILL,
                    0.76F,
                    0.48F
            );
        }

        if (tick == 82) {
            collapseImpact(
                    level,
                    boss,
                    viewers,
                    lockedPlayers
            );
        }

        if (tick >= 82
                && tick <= 92
                && tick % 4 == 2) {

            double age =
                    tick - 82.0D;

            ring(
                    level,
                    viewers,
                    ParticleTypes.DUST_PLUME,
                    boss.position()
                            .add(0.0D, 0.13D, 0.0D),
                    1.20D + age * 0.82D,
                    18,
                    age * 0.11D,
                    0.025D
            );

            if (tick == 86) {
                ring(
                        level,
                        viewers,
                        ParticleTypes.SCULK_SOUL,
                        boss.position()
                                .add(0.0D, 0.24D, 0.0D),
                        1.55D + age * 0.70D,
                        14,
                        -age * 0.14D,
                        0.07D
                );
            }
        }

        if (tick == 89
                || tick == 97) {

            delayedDebris(
                    level,
                    boss,
                    viewers,
                    tick == 89 ? 0.72D : 0.48D
            );
        }

        if (tick >= 99
                && tick <= 106
                && (tick == 101 || tick == 105)) {

            ring(
                    level,
                    viewers,
                    ParticleTypes.END_ROD,
                    crystal,
                    0.48D - (tick - 99.0D) * 0.035D,
                    8,
                    tick * 0.45D,
                    0.10D
            );
        }

        if (tick == 110) {
            finalCrystalRelease(
                    level,
                    boss,
                    viewers,
                    lockedPlayers
            );
        }

        if (tick >= 110
                && tick <= 120
                && tick % 4 == 2) {

            double age =
                    tick - 110.0D;

            ring(
                    level,
                    viewers,
                    ParticleTypes.END_ROD,
                    crystal,
                    1.05D + age * 0.72D,
                    16,
                    -age * 0.13D,
                    0.15D
            );
        }

        if (tick == 118) {
            sound(
                    level,
                    viewers,
                    crystal,
                    SoundEvents.BEACON_DEACTIVATE,
                    0.82F,
                    0.58F
            );
        }

        if (tick >= 112
                && tick < StarCrawlerBossEntity.DEATH_CINEMATIC_DURATION_TICKS
                && tick % 4 == 0) {

            sendParticles(
                    level,
                    viewers,
                    (tick & 8) == 0
                            ? ParticleTypes.ASH
                            : ParticleTypes.WHITE_ASH,
                    boss.position()
                            .add(
                                    0.0D,
                                    boss.getBbHeight() * 0.52D,
                                    0.0D
                            ),
                    7,
                    1.65D,
                    0.92D,
                    1.65D,
                    0.012D
            );

            sendParticles(
                    level,
                    viewers,
                    ParticleTypes.SMOKE,
                    crystal,
                    3,
                    0.34D,
                    0.28D,
                    0.34D,
                    0.018D
            );
        }
    }

    private static void crystalFracture(
            ServerLevel level,
            List<ServerPlayer> viewers,
            Vec3 crystal,
            double scale
    ) {
        ring(
                level,
                viewers,
                ParticleTypes.ELECTRIC_SPARK,
                crystal,
                scale,
                20,
                0.0D,
                0.24D
        );

        sendParticles(
                level,
                viewers,
                ParticleTypes.END_ROD,
                crystal,
                24,
                scale,
                scale * 0.85D,
                scale,
                0.12D
        );
    }

    private static void collapseImpact(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            List<ServerPlayer> viewers,
            List<ServerPlayer> lockedPlayers
    ) {
        Vec3 floor =
                boss.position()
                        .add(0.0D, 0.16D, 0.0D);

        ring(level, viewers, ParticleTypes.DUST_PLUME,
                floor, 1.25D, 18, 0.0D, 0.02D);
        ring(level, viewers, ParticleTypes.DUST_PLUME,
                floor, 2.65D, 24, 0.16D, 0.03D);
        ring(level, viewers, ParticleTypes.SCULK_SOUL,
                floor.add(0.0D, 0.18D, 0.0D),
                3.90D, 20, -0.12D, 0.09D);

        sendParticles(
                level,
                viewers,
                ParticleTypes.POOF,
                floor.add(0.0D, 0.25D, 0.0D),
                58,
                2.05D,
                0.40D,
                2.05D,
                0.16D
        );

        sendParticles(
                level,
                viewers,
                ParticleTypes.LARGE_SMOKE,
                floor.add(0.0D, 0.38D, 0.0D),
                22,
                1.65D,
                0.38D,
                1.65D,
                0.055D
        );

        blockDebris(
                level,
                boss,
                viewers,
                4.6D,
                22,
                5
        );

        sound(
                level,
                viewers,
                floor,
                SoundEvents.WARDEN_ATTACK_IMPACT,
                2.25F,
                0.38F
        );

        sound(
                level,
                viewers,
                floor,
                SoundEvents.GENERIC_EXPLODE.value(),
                0.92F,
                0.52F
        );

        cameraShake(
                lockedPlayers,
                20,
                1.30F
        );
    }

    private static void delayedDebris(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            List<ServerPlayer> viewers,
            double strength
    ) {
        Vec3 floor =
                boss.position()
                        .add(0.0D, 0.18D, 0.0D);

        sendParticles(
                level,
                viewers,
                ParticleTypes.DUST_PLUME,
                floor,
                (int) (18.0D * strength),
                1.85D,
                0.22D,
                1.85D,
                0.055D
        );

        blockDebris(
                level,
                boss,
                viewers,
                3.8D,
                10,
                Math.max(2, (int) (4.0D * strength))
        );

        sound(
                level,
                viewers,
                floor,
                SoundEvents.BREEZE_LAND,
                (float) strength,
                0.46F
        );
    }

    private static void finalCrystalRelease(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            List<ServerPlayer> viewers,
            List<ServerPlayer> lockedPlayers
    ) {
        Vec3 crystal =
                crystalPosition(boss);

        ring(level, viewers, ParticleTypes.END_ROD,
                crystal, 0.72D, 18, 0.0D, 0.20D);
        ring(level, viewers, ParticleTypes.ELECTRIC_SPARK,
                crystal, 1.45D, 22, 0.16D, 0.28D);
        ring(level, viewers, ParticleTypes.REVERSE_PORTAL,
                crystal, 2.65D, 26, -0.22D, 0.34D);
        ring(level, viewers, ParticleTypes.SCULK_SOUL,
                crystal, 4.20D, 28, 0.10D, 0.42D);

        sendParticles(
                level,
                viewers,
                ParticleTypes.END_ROD,
                crystal,
                68,
                1.55D,
                1.25D,
                1.55D,
                0.18D
        );

        sendParticles(
                level,
                viewers,
                ParticleTypes.ELECTRIC_SPARK,
                crystal,
                46,
                1.35D,
                1.10D,
                1.35D,
                0.20D
        );

        for (int i = 0; i < 8; i++) {
            Vec3 point =
                    crystal.add(
                            Math.sin(i * 1.71D) * 0.20D,
                            i * 0.38D,
                            Math.cos(i * 1.37D) * 0.20D
                    );

            sendParticles(
                    level,
                    viewers,
                    i % 3 == 0
                            ? ParticleTypes.ELECTRIC_SPARK
                            : ParticleTypes.END_ROD,
                    point,
                    2,
                    0.08D,
                    0.12D,
                    0.08D,
                    0.045D
            );
        }

        sound(
                level,
                viewers,
                crystal,
                SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(),
                1.60F,
                0.54F
        );

        sound(
                level,
                viewers,
                crystal,
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                0.72F,
                0.62F
        );

        sound(
                level,
                viewers,
                crystal,
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                1.08F,
                1.52F
        );

        cameraShake(
                lockedPlayers,
                14,
                0.88F
        );
    }

    private static void escapingSpokes(
            ServerLevel level,
            List<ServerPlayer> viewers,
            Vec3 crystal,
            double radius,
            int points,
            double phase
    ) {
        for (int i = 0; i < points; i++) {
            double angle =
                    TAU * i / points + phase;

            double vertical =
                    Math.sin(angle * 1.7D + phase)
                            * radius * 0.30D;

            Vec3 point =
                    crystal.add(
                            Math.cos(angle) * radius,
                            vertical,
                            Math.sin(angle) * radius
                    );

            sendParticles(
                    level,
                    viewers,
                    i % 2 == 0
                            ? ParticleTypes.END_ROD
                            : ParticleTypes.ELECTRIC_SPARK,
                    point,
                    1,
                    0.04D,
                    0.04D,
                    0.04D,
                    0.02D
            );
        }
    }

    private static void blockDebris(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            List<ServerPlayer> viewers,
            double radius,
            int samples,
            int particlesPerSample
    ) {
        for (int i = 0; i < samples; i++) {
            double angle =
                    TAU * i / samples
                            + (i % 3) * 0.11D;

            double distance =
                    radius
                            * (0.22D
                            + 0.78D
                            * ((i % 7) / 6.0D));

            double x =
                    boss.getX()
                            + Math.cos(angle) * distance;

            double z =
                    boss.getZ()
                            + Math.sin(angle) * distance;

            BlockPos surface =
                    BlockPos.containing(
                            x,
                            boss.getY() - 0.10D,
                            z
                    );

            BlockState state =
                    level.getBlockState(surface);

            int search = 0;
            while (state.isAir()
                    && search++ < 5) {

                surface = surface.below();
                state = level.getBlockState(surface);
            }

            if (state.isAir()) {
                continue;
            }

            sendParticles(
                    level,
                    viewers,
                    new BlockParticleOption(
                            ParticleTypes.BLOCK,
                            state
                    ),
                    new Vec3(
                            x,
                            surface.getY() + 1.12D,
                            z
                    ),
                    particlesPerSample,
                    0.20D,
                    0.25D,
                    0.20D,
                    0.15D
            );
        }
    }

    private static Vec3 crystalPosition(
            StarCrawlerBossEntity boss
    ) {
        Vec3 forward =
                Vec3.directionFromRotation(
                                0.0F,
                                boss.getYRot()
                        )
                        .horizontal();

        if (forward.horizontalDistanceSqr()
                > 1.0E-6D) {
            forward = forward.normalize();
        } else {
            forward = new Vec3(0.0D, 0.0D, 1.0D);
        }

        return boss.position()
                .add(forward.scale(-0.62D))
                .add(
                        0.0D,
                        boss.getBbHeight() * 0.80D,
                        0.0D
                );
    }

    private static List<ServerPlayer> collectViewers(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            Iterable<ServerPlayer> participants
    ) {
        Map<UUID, ServerPlayer> viewers =
                new LinkedHashMap<>();

        for (ServerPlayer participant :
                participants) {

            if (participant.connection != null) {
                viewers.put(
                        participant.getUUID(),
                        participant
                );
            }
        }

        for (ServerPlayer player :
                level.players()) {

            if (player.connection == null
                    || player.isRemoved()
                    || PlayerHooks.isFake(player)
                    || player.distanceToSqr(boss)
                    > WORLD_VIEW_RADIUS_SQR) {

                continue;
            }

            viewers.putIfAbsent(
                    player.getUUID(),
                    player
            );
        }

        return new ArrayList<>(
                viewers.values()
        );
    }

    private static List<ServerPlayer> copyPlayers(
            Iterable<ServerPlayer> players
    ) {
        List<ServerPlayer> copy =
                new ArrayList<>();

        for (ServerPlayer player : players) {
            if (player.connection != null) {
                copy.add(player);
            }
        }

        return copy;
    }

    private static void ring(
            ServerLevel level,
            Iterable<ServerPlayer> viewers,
            ParticleOptions particle,
            Vec3 center,
            double radius,
            int points,
            double phase,
            double verticalWave
    ) {
        int sampleCount =
                Math.max(6, points);

        for (int i = 0; i < sampleCount; i++) {
            double angle =
                    TAU * i / sampleCount + phase;

            Vec3 point =
                    new Vec3(
                            center.x
                                    + Math.cos(angle) * radius,
                            center.y
                                    + Math.sin(
                                    angle * 2.0D + phase
                            ) * verticalWave,
                            center.z
                                    + Math.sin(angle) * radius
                    );

            sendParticles(
                    level,
                    viewers,
                    particle,
                    point,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }
    }

    private static void sendParticles(
            ServerLevel level,
            Iterable<ServerPlayer> viewers,
            ParticleOptions particle,
            Vec3 position,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed
    ) {
        for (ServerPlayer viewer :
                viewers) {

            level.sendParticles(
                    viewer,
                    particle,
                    true,
                    true,
                    position.x,
                    position.y,
                    position.z,
                    count,
                    offsetX,
                    offsetY,
                    offsetZ,
                    speed
            );
        }
    }

    private static void sound(
            ServerLevel level,
            Iterable<ServerPlayer> viewers,
            Vec3 position,
            SoundEvent sound,
            float volume,
            float pitch
    ) {
        long seed =
                level.getRandom().nextLong();

        for (ServerPlayer viewer :
                viewers) {

            if (viewer.connection == null) {
                continue;
            }

            viewer.connection.send(
                    new ClientboundSoundPacket(
                            BuiltInRegistries.SOUND_EVENT
                                    .wrapAsHolder(sound),
                            SoundSource.HOSTILE,
                            position.x,
                            position.y,
                            position.z,
                            volume,
                            pitch,
                            seed
                    )
            );
        }
    }

    private static void cameraShake(
            Iterable<ServerPlayer> participants,
            int ticks,
            float intensity
    ) {
        for (ServerPlayer participant :
                participants) {

            NetworkManager.sendToPlayer(
                    participant,
                    new ParasiteCameraShakePacket(
                            ticks,
                            intensity
                    )
            );
        }
    }

    private static double clamp01(
            double value
    ) {
        return Math.max(
                0.0D,
                Math.min(1.0D, value)
        );
    }
}

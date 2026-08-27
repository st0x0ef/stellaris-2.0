package org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss;

import dev.architectury.hooks.level.entity.PlayerHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Helper class !!
public final class StarCrawlerBossVfx {
    private static final double TAU = Math.PI * 2.0D;

    private StarCrawlerBossVfx() {
    }

    public static void introTick(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            int tick,
            Iterable<ServerPlayer> participants
    ) {
        if (tick < 16
                || tick > 122
                || (tick & 1) != 0) {

            return;
        }

        List<ServerPlayer> viewers =
                collectIntroViewers(
                        level,
                        boss,
                        participants,
                        tick >= 88 ? 72.0D : 36.0D
                );

        Vec3 forward =
                Vec3.directionFromRotation(
                                0.0F,
                                boss.getYRot()
                        )
                        .horizontal()
                        .normalize();

        Vec3 crystal =
                boss.position()
                        .add(
                                forward.scale(-0.62D)
                        )
                        .add(
                                0.0D,
                                boss.getBbHeight() * 0.80D,
                                0.0D
                        );

        Vec3 body =
                boss.position()
                        .add(
                                0.0D,
                                boss.getBbHeight() * 0.50D,
                                0.0D
                        );

        Vec3 floor =
                boss.position()
                        .add(0.0D, 0.14D, 0.0D);


        if (tick >= 72
                && tick < 88) {

            if (tick == 72) {
                introRing(
                        level,
                        viewers,
                        ParticleTypes.REVERSE_PORTAL,
                        crystal,
                        0.62D,
                        10,
                        0.0D,
                        0.03D
                );
            }

            if (tick == 80) {
                sendIntroParticles(
                        level,
                        viewers,
                        ParticleTypes.ELECTRIC_SPARK,
                        crystal.x,
                        crystal.y,
                        crystal.z,
                        2,
                        0.04D,
                        0.04D,
                        0.04D,
                        0.0D
                );
            }

            return;
        }

        double progress =
                clamp01(
                        (tick - 16.0D)
                                / 56.0D
                );

        double radius =
                lerp(
                        3.15D,
                        0.46D,
                        progress
                );

        if (tick < 72
                && tick % 6 == 0) {
            introRing(
                    level,
                    viewers,
                    ParticleTypes.REVERSE_PORTAL,
                    crystal,
                    radius,
                    10,
                    -tick * 0.16D,
                    0.18D
            );
        }

        if (tick < 72
                && tick % 6 == 0) {
            sendIntroParticles(
                    level,
                    viewers,
                    ParticleTypes.END_ROD,
                    crystal.x,
                    crystal.y,
                    crystal.z,
                    5,
                    0.22D + radius * 0.12D,
                    0.20D,
                    0.22D + radius * 0.12D,
                    0.015D
            );
        }

        if (tick < 72) {
            if (tick % 4 == 0) {
                int attractedMotes =
                    progress < 0.45D
                            ? 2
                            : 3;

                for (int i = 0; i < attractedMotes; i++) {
                    double angle =
                        tick * 0.31D
                                + i * TAU / attractedMotes;

                    double sourceRadius =
                        radius
                                + 0.45D
                                + i * 0.18D;

                    Vec3 source =
                        crystal.add(
                                Math.cos(angle) * sourceRadius,
                                Math.sin(angle * 1.7D) * 0.65D,
                                Math.sin(angle) * sourceRadius
                        );

                    Vec3 velocity =
                        crystal.subtract(source)
                                .normalize()
                                .scale(
                                        0.055D
                                                + progress * 0.075D
                                );

                    sendIntroParticles(
                            level,
                            viewers,
                            i == 0
                                    ? ParticleTypes.END_ROD
                                    : ParticleTypes.REVERSE_PORTAL,
                            source.x,
                            source.y,
                            source.z,
                            0,
                            velocity.x,
                            velocity.y,
                            velocity.z,
                            1.0D
                    );
                }
            }

            if (tick % 8 == 0) {
                sendIntroParticles(
                        level,
                        viewers,
                        ParticleTypes.WHITE_ASH,
                        body.x,
                        body.y,
                        body.z,
                        5,
                        boss.getBbWidth() * 0.52D,
                        boss.getBbHeight() * 0.34D,
                        boss.getBbWidth() * 0.52D,
                        0.012D
                );
            }

            if (tick == 42
                    || tick == 62) {
                introRing(
                        level,
                        viewers,
                        ParticleTypes.DUST_PLUME,
                        floor,
                        tick == 42
                                ? 1.35D
                                : 1.90D,
                        tick == 42
                                ? 10
                                : 14,
                        tick * 0.08D,
                        0.02D
                );

                introGroundDebris(
                        level,
                        viewers,
                        boss,
                        tick == 42
                                ? 6
                                : 8,
                        tick * 0.11D,
                        0.10D
                );
            }

            return;
        }

        if (tick == 88) {
            sendIntroParticles(
                    level,
                    viewers,
                    ParticleTypes.END_ROD,
                    crystal.x,
                    crystal.y,
                    crystal.z,
                    12,
                    0.28D,
                    0.22D,
                    0.28D,
                    0.035D
            );

            introRing(
                    level,
                    viewers,
                    ParticleTypes.ELECTRIC_SPARK,
                    crystal,
                    0.86D,
                    18,
                    0.15D,
                    0.10D
            );
        }

        if (tick == 94
                || tick == 100) {
            double aftershockAge =
                    tick - 90.0D;

            introRing(
                    level,
                    viewers,
                    tick <= 96
                            ? ParticleTypes.DUST_PLUME
                            : ParticleTypes.REVERSE_PORTAL,
                    floor,
                    1.15D + aftershockAge * 0.24D,
                    tick <= 96
                            ? 18
                            : 14,
                    tick * 0.07D,
                    0.035D
            );

            sendIntroParticles(
                    level,
                    viewers,
                    ParticleTypes.WHITE_ASH,
                    body.x,
                    body.y,
                    body.z,
                    7,
                    1.05D,
                    0.62D,
                    1.05D,
                    0.025D
            );
        }

        if (tick == 106) {
            sendIntroParticles(
                    level,
                    viewers,
                    ParticleTypes.SONIC_BOOM,
                    body.x,
                    body.y,
                    body.z,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );

            introRing(
                    level,
                    viewers,
                    ParticleTypes.REVERSE_PORTAL,
                    body,
                    4.25D,
                    24,
                    0.12D,
                    0.15D
            );

            introRing(
                    level,
                    viewers,
                    ParticleTypes.END_ROD,
                    crystal,
                    1.72D,
                    16,
                    -0.18D,
                    0.24D
            );

            radialIntroBurst(
                    level,
                    viewers,
                    ParticleTypes.ELECTRIC_SPARK,
                    crystal,
                    18,
                    0.31D,
                    0.12D,
                    0.28D
            );
        }

        if (tick == 114) {
            introRing(
                    level,
                    viewers,
                    ParticleTypes.END_ROD,
                    crystal,
                    1.15D,
                    12,
                    0.0D,
                    0.20D
            );
        }
    }

    public static void introRoar(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            Iterable<ServerPlayer> participants
    ) {
        List<ServerPlayer> viewers =
                collectIntroViewers(
                        level,
                        boss,
                        participants,
                        72.0D
                );

        Vec3 center =
                boss.position()
                        .add(
                                0.0D,
                                boss.getBbHeight() * 0.72D,
                                0.0D
                        );

        Vec3 floor =
                boss.position()
                        .add(0.0D, 0.12D, 0.0D);

        Vec3 forward =
                Vec3.directionFromRotation(
                                0.0F,
                                boss.getYRot()
                        )
                        .horizontal()
                        .normalize();

        Vec3 crystal =
                boss.position()
                        .add(forward.scale(-0.62D))
                        .add(
                                0.0D,
                                boss.getBbHeight() * 0.80D,
                                0.0D
                        );

        sendIntroParticles(
                level,
                viewers,
                ParticleTypes.SONIC_BOOM,
                center.x,
                center.y,
                center.z,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        sendIntroParticles(
                level,
                viewers,
                ParticleTypes.DUST_PLUME,
                floor.x,
                floor.y,
                floor.z,
                46,
                1.55D,
                0.20D,
                1.55D,
                0.24D
        );

        sendIntroParticles(
                level,
                viewers,
                ParticleTypes.POOF,
                center.x,
                center.y,
                center.z,
                30,
                1.18D,
                0.72D,
                1.18D,
                0.16D
        );

        introRing(
                level,
                viewers,
                ParticleTypes.END_ROD,
                center,
                1.15D,
                18,
                0.0D,
                0.18D
        );

        introRing(
                level,
                viewers,
                ParticleTypes.REVERSE_PORTAL,
                center,
                2.25D,
                24,
                0.20D,
                0.22D
        );

        sendIntroParticles(
                level,
                viewers,
                ParticleTypes.ELECTRIC_SPARK,
                center.x,
                center.y,
                center.z,
                34,
                0.85D,
                0.65D,
                0.85D,
                0.10D
        );

        sendIntroParticles(
                level,
                viewers,
                ParticleTypes.END_ROD,
                crystal.x,
                crystal.y,
                crystal.z,
                28,
                0.42D,
                0.36D,
                0.42D,
                0.10D
        );

        sendIntroParticles(
                level,
                viewers,
                ParticleTypes.SCULK_SOUL,
                center.x,
                center.y,
                center.z,
                18,
                0.84D,
                0.52D,
                0.84D,
                0.075D
        );

        radialIntroBurst(
                level,
                viewers,
                ParticleTypes.DUST_PLUME,
                floor,
                24,
                0.34D,
                0.16D,
                0.0D
        );

        radialIntroBurst(
                level,
                viewers,
                ParticleTypes.ELECTRIC_SPARK,
                crystal,
                20,
                0.46D,
                0.20D,
                0.31D
        );

        introGroundDebris(
                level,
                viewers,
                boss,
                14,
                0.0D,
                0.24D
        );
    }

    public static void introComplete(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            Iterable<ServerPlayer> participants
    ) {
        List<ServerPlayer> viewers =
                collectIntroViewers(
                        level,
                        boss,
                        participants,
                        72.0D
                );

        Vec3 center =
                boss.position()
                        .add(
                                0.0D,
                                boss.getBbHeight() * 0.72D,
                                0.0D
                        );

        Vec3 floor =
                boss.position()
                        .add(0.0D, 0.14D, 0.0D);

        introRing(
                level,
                viewers,
                ParticleTypes.END_ROD,
                center,
                1.45D,
                20,
                0.0D,
                0.12D
        );

        sendIntroParticles(
                level,
                viewers,
                ParticleTypes.REVERSE_PORTAL,
                center.x,
                center.y,
                center.z,
                30,
                0.80D,
                0.55D,
                0.80D,
                0.08D
        );

        introRing(
                level,
                viewers,
                ParticleTypes.DUST_PLUME,
                floor,
                3.10D,
                20,
                0.20D,
                0.035D
        );

        radialIntroBurst(
                level,
                viewers,
                ParticleTypes.END_ROD,
                center,
                16,
                0.22D,
                0.16D,
                0.0D
        );
    }

    public static void chargeWindup(ServerLevel level, StarCrawlerBossEntity boss, int tick) {
        if ((tick & 1) != 0) {
            return;
        }

        double progress = clamp01(tick / 12.0D);
        double radius = lerp(3.15D, 0.95D, progress);
        Vec3 floor = boss.position().add(0.0D, 0.18D, 0.0D);

        ring(level, ParticleTypes.REVERSE_PORTAL, floor, radius, 22, tick * 0.20D, 0.025D);

        if ((tick & 3) == 0) {
            ring(level, ParticleTypes.END_ROD,
                    boss.position().add(0.0D, 0.75D + progress * 0.95D, 0.0D),
                    Math.max(0.65D, radius * 0.60D),
                    16, -tick * 0.28D, 0.15D);
        }
    }

    public static void chargeLaunch(ServerLevel level, StarCrawlerBossEntity boss) {
        Vec3 floor = boss.position().add(0.0D, 0.18D, 0.0D);

        ring(level, ParticleTypes.DUST_PLUME, floor, 1.15D, 18, 0.0D, 0.02D);
        ring(level, ParticleTypes.DUST_PLUME, floor, 2.30D, 28, 0.15D, 0.025D);
        ring(level, ParticleTypes.END_ROD, floor.add(0.0D, 0.75D, 0.0D),
                1.45D, 18, 0.0D, 0.16D);

        level.sendParticles(ParticleTypes.POOF,
                boss.getX(), boss.getY() + 0.35D, boss.getZ(),
                24, 1.10D, 0.24D, 1.10D, 0.10D);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                boss.getX(), boss.getY() + 1.0D, boss.getZ(),
                18, 0.90D, 0.65D, 0.90D, 0.13D);
    }

    public static void chargeTrail(ServerLevel level, StarCrawlerBossEntity boss, int tick) {
        Vec3 horizontal = boss.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D);
        Vec3 back = horizontal.horizontalDistanceSqr() > 1.0E-5D
                ? horizontal.normalize().scale(-1.45D)
                : Vec3.ZERO;
        Vec3 trail = boss.position().add(back).add(0.0D, 0.72D, 0.0D);

        level.sendParticles(ParticleTypes.END_ROD,
                trail.x, trail.y, trail.z,
                4, 0.38D, 0.34D, 0.38D, 0.025D);

        if ((tick & 1) == 0) {
            ring(level, ParticleTypes.ELECTRIC_SPARK, trail,
                    0.72D, 12, tick * 0.40D, 0.22D);
            level.sendParticles(ParticleTypes.DUST_PLUME,
                    boss.getX(), boss.getY() + 0.12D, boss.getZ(),
                    5, 0.65D, 0.07D, 0.65D, 0.035D);
        }
    }

    public static void chargeHit(ServerLevel level, LivingEntity target) {
        Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.45D, 0.0D);

        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                center.x, center.y, center.z,
                16, 0.48D, 0.48D, 0.48D, 0.18D);
        level.sendParticles(ParticleTypes.POOF,
                center.x, center.y, center.z,
                10, 0.38D, 0.30D, 0.38D, 0.08D);
    }

    public static void chargeCrash(ServerLevel level, StarCrawlerBossEntity boss) {
        Vec3 center = boss.position().add(0.0D, 0.55D, 0.0D);

        ring(level, ParticleTypes.DUST_PLUME,
                boss.position().add(0.0D, 0.18D, 0.0D),
                1.65D, 22, 0.0D, 0.02D);

        level.sendParticles(ParticleTypes.POOF,
                center.x, center.y, center.z,
                20, 0.90D, 0.60D, 0.90D, 0.12D);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                center.x, center.y, center.z,
                12, 0.75D, 0.55D, 0.75D, 0.12D);
    }

    public static void jumpWindup(ServerLevel level, StarCrawlerBossEntity boss, int tick) {
        if ((tick & 1) != 0) {
            return;
        }

        double progress = clamp01(tick / 7.0D);
        double radius = lerp(3.40D, 0.95D, progress);
        Vec3 floor = boss.position().add(0.0D, 0.16D, 0.0D);

        ring(level, ParticleTypes.REVERSE_PORTAL, floor, radius,
                22, tick * 0.22D, 0.025D);

        if (tick >= 3) {
            ring(level, ParticleTypes.END_ROD,
                    boss.position().add(0.0D, 0.65D + progress, 0.0D),
                    Math.max(0.70D, radius * 0.55D),
                    14, -tick * 0.30D, 0.12D);
        }
    }

    public static void jumpTakeoff(ServerLevel level, StarCrawlerBossEntity boss) {
        Vec3 floor = boss.position().add(0.0D, 0.18D, 0.0D);

        ring(level, ParticleTypes.DUST_PLUME, floor, 1.10D, 18, 0.0D, 0.02D);
        ring(level, ParticleTypes.DUST_PLUME, floor, 2.25D, 26, 0.18D, 0.02D);
        ring(level, ParticleTypes.END_ROD,
                boss.position().add(0.0D, 0.95D, 0.0D),
                1.45D, 18, 0.0D, 0.20D);
        ring(level, ParticleTypes.REVERSE_PORTAL,
                boss.position().add(0.0D, 1.65D, 0.0D),
                1.05D, 16, 0.35D, 0.26D);

        level.sendParticles(ParticleTypes.POOF,
                boss.getX(), boss.getY() + 0.25D, boss.getZ(),
                28, 1.10D, 0.18D, 1.10D, 0.11D);
        level.sendParticles(ParticleTypes.END_ROD,
                boss.getX(), boss.getY() + 1.0D, boss.getZ(),
                18, 0.65D, 0.85D, 0.65D, 0.08D);
    }

    public static void jumpFlightTrail(ServerLevel level, StarCrawlerBossEntity boss, int tick) {
        Vec3 horizontal = boss.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D);
        Vec3 back = horizontal.horizontalDistanceSqr() > 1.0E-5D
                ? horizontal.normalize().scale(-1.25D)
                : Vec3.ZERO;
        Vec3 center = boss.position().add(back).add(0.0D, 0.85D, 0.0D);

        level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                center.x, center.y, center.z,
                5, 0.38D, 0.38D, 0.38D, 0.035D);

        if ((tick & 1) == 0) {
            ring(level, ParticleTypes.END_ROD, center,
                    0.70D, 12, tick * 0.48D, 0.25D);
        }

        if (tick % 3 == 0) {
            ring(level, ParticleTypes.ELECTRIC_SPARK,
                    boss.position().add(0.0D, 0.55D, 0.0D),
                    0.92D, 12, -tick * 0.38D, 0.12D);
        }
    }

    public static void jumpImpact(ServerLevel level, StarCrawlerBossEntity boss) {
        Vec3 floor = boss.position().add(0.0D, 0.20D, 0.0D);

        ring(level, ParticleTypes.DUST_PLUME, floor, 1.25D, 20, 0.0D, 0.02D);
        ring(level, ParticleTypes.END_ROD, floor.add(0.0D, 0.18D, 0.0D),
                1.75D, 20, 0.18D, 0.07D);

        level.sendParticles(ParticleTypes.POOF,
                boss.getX(), boss.getY() + 0.25D, boss.getZ(),
                30, 1.35D, 0.25D, 1.35D, 0.12D);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                boss.getX(), boss.getY() + 0.45D, boss.getZ(),
                22, 1.15D, 0.40D, 1.15D, 0.15D);
    }

    public static void jumpShockwave(ServerLevel level, StarCrawlerBossEntity boss, int age) {
        if (age < 0 || age > 9) {
            return;
        }

        double radius = 0.90D + age * 0.78D;
        Vec3 floor = boss.position().add(0.0D, 0.15D, 0.0D);

        ring(level, ParticleTypes.DUST_PLUME, floor, radius,
                22, age * 0.12D, 0.018D);

        if ((age & 1) == 0) {
            ring(level, ParticleTypes.END_ROD,
                    floor.add(0.0D, 0.09D, 0.0D),
                    radius + 0.25D,
                    16, -age * 0.20D, 0.05D);
        }
    }

    public static void groundSmashWindup(ServerLevel level, StarCrawlerBossEntity boss, int tick) {
        if (tick < 2 || tick > 12 || (tick & 1) != 0) {
            return;
        }

        double progress = clamp01(tick / 13.0D);
        double radius = lerp(4.20D, 1.15D, progress);
        Vec3 floor = boss.position().add(0.0D, 0.16D, 0.0D);

        ring(level, ParticleTypes.REVERSE_PORTAL, floor, radius,
                24, tick * 0.18D, 0.025D);
        ring(level, ParticleTypes.SCULK_SOUL,
                boss.position().add(0.0D, 0.55D + progress * 0.90D, 0.0D),
                Math.max(0.75D, radius * 0.52D),
                14, -tick * 0.25D, 0.16D);
    }

    public static void groundSmashImpact(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            boolean finalSmash
    ) {
        Vec3 floor = boss.position().add(0.0D, 0.18D, 0.0D);

        ring(level, ParticleTypes.DUST_PLUME, floor, 1.35D, 22, 0.0D, 0.02D);
        ring(level, ParticleTypes.SCULK_SOUL,
                floor.add(0.0D, 0.18D, 0.0D),
                2.0D, 22, 0.16D, 0.08D);
        ring(level, ParticleTypes.END_ROD,
                floor.add(0.0D, 0.32D, 0.0D),
                2.55D, 24, -0.12D, 0.10D);

        if (finalSmash) {
            ring(level, ParticleTypes.REVERSE_PORTAL,
                    floor.add(0.0D, 0.42D, 0.0D),
                    3.25D, 30, 0.25D, 0.12D);
            level.sendParticles(ParticleTypes.END_ROD,
                    boss.getX(), boss.getY() + 1.0D, boss.getZ(),
                    42, 1.70D, 1.15D, 1.70D, 0.12D);
        }

        level.sendParticles(ParticleTypes.POOF,
                boss.getX(), boss.getY() + 0.25D, boss.getZ(),
                finalSmash ? 42 : 30,
                finalSmash ? 1.70D : 1.30D,
                0.30D,
                finalSmash ? 1.70D : 1.30D,
                0.13D);
    }

    public static void groundSmashShockwave(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            int age,
            boolean finalSmash
    ) {
        int maxAge = finalSmash ? 12 : 10;
        if (age < 0 || age > maxAge) {
            return;
        }

        double step = finalSmash ? 0.92D : 0.82D;
        double radius = 1.0D + age * step;
        Vec3 floor = boss.position().add(0.0D, 0.15D, 0.0D);

        ring(level, ParticleTypes.DUST_PLUME, floor, radius,
                finalSmash ? 26 : 22,
                age * 0.10D, 0.018D);

        if ((age & 1) == 0) {
            ring(level,
                    finalSmash ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.SCULK_SOUL,
                    floor.add(0.0D, 0.10D, 0.0D),
                    radius + 0.28D,
                    finalSmash ? 22 : 16,
                    -age * 0.17D,
                    0.06D);
        }
    }

    public static void healingStart(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            boolean secondHeal
    ) {
        Vec3 floor = boss.position().add(0.0D, 0.17D, 0.0D);

        ring(level, ParticleTypes.REVERSE_PORTAL, floor, 3.60D,
                secondHeal ? 30 : 24, 0.0D, 0.03D);
        ring(level, ParticleTypes.END_ROD,
                boss.position().add(0.0D, 1.0D, 0.0D),
                2.0D, secondHeal ? 24 : 18, 0.35D, 0.20D);

        level.sendParticles(
                secondHeal ? ParticleTypes.SCULK_SOUL : ParticleTypes.END_ROD,
                boss.getX(), boss.getY() + 1.15D, boss.getZ(),
                secondHeal ? 34 : 22,
                1.25D, 0.85D, 1.25D, 0.07D
        );
    }

    public static void healingTick(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            int tick,
            boolean secondHeal
    ) {
        double progress = clamp01(tick / 80.0D);
        double orbitRadius = secondHeal ? 2.15D : 1.75D;
        int helixCount = secondHeal ? 6 : 4;

        for (int i = 0; i < helixCount; i++) {
            double angle = tick * (secondHeal ? 0.34D : 0.27D)
                    + TAU * i / helixCount;
            double radius = orbitRadius
                    * (0.80D + 0.20D * Math.sin(tick * 0.15D + i));
            double y = boss.getY()
                    + 0.25D
                    + ((tick * 0.11D + i * 0.55D) % 2.8D);
            double x = boss.getX() + Math.cos(angle) * radius;
            double z = boss.getZ() + Math.sin(angle) * radius;

            level.sendParticles(
                    i % 2 == 0 ? ParticleTypes.END_ROD : ParticleTypes.REVERSE_PORTAL,
                    x, y, z,
                    1, 0.03D, 0.04D, 0.03D, 0.01D
            );
        }

        if (tick % 5 == 0) {
            double groundRadius = lerp(
                    secondHeal ? 4.40D : 3.50D,
                    secondHeal ? 1.50D : 1.20D,
                    progress
            );
            ring(level, ParticleTypes.REVERSE_PORTAL,
                    boss.position().add(0.0D, 0.16D, 0.0D),
                    groundRadius,
                    secondHeal ? 24 : 18,
                    tick * 0.16D,
                    0.025D);
        }

        if (tick % 8 == 0) {
            double y = boss.getY() + 0.75D + progress * 1.60D;
            ring(level,
                    secondHeal ? ParticleTypes.SCULK_SOUL : ParticleTypes.END_ROD,
                    new Vec3(boss.getX(), y, boss.getZ()),
                    lerp(2.30D, 0.75D, progress),
                    secondHeal ? 20 : 16,
                    -tick * 0.20D,
                    0.12D);
        }

        if (secondHeal && (tick & 3) == 0) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    boss.getX(), boss.getY() + 1.5D, boss.getZ(),
                    5, 1.0D, 0.90D, 1.0D, 0.05D);
        }
    }

    public static void healingFinish(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            boolean phaseThree
    ) {
        Vec3 floor = boss.position().add(0.0D, 0.18D, 0.0D);

        ring(level, ParticleTypes.END_ROD,
                floor.add(0.0D, 0.25D, 0.0D),
                1.35D, 20, 0.0D, 0.09D);
        ring(level, ParticleTypes.REVERSE_PORTAL,
                floor.add(0.0D, 0.45D, 0.0D),
                2.55D, 28, 0.18D, 0.12D);
        ring(level,
                phaseThree ? ParticleTypes.SCULK_SOUL : ParticleTypes.END_ROD,
                floor.add(0.0D, 0.70D, 0.0D),
                4.10D,
                phaseThree ? 34 : 28,
                -0.15D,
                0.14D);

        level.sendParticles(ParticleTypes.END_ROD,
                boss.getX(), boss.getY() + 1.5D, boss.getZ(),
                phaseThree ? 58 : 42,
                phaseThree ? 2.0D : 1.60D,
                phaseThree ? 1.45D : 1.15D,
                phaseThree ? 2.0D : 1.60D,
                0.12D);

        if (phaseThree) {
            level.sendParticles(ParticleTypes.SCULK_SOUL,
                    boss.getX(), boss.getY() + 1.0D, boss.getZ(),
                    36, 1.75D, 0.90D, 1.75D, 0.08D);
        }
    }

    private static void ring(
            ServerLevel level,
            ParticleOptions particle,
            Vec3 center,
            double radius,
            int points,
            double phase,
            double verticalWave
    ) {
        int sampleCount = Math.max(6, points);
        for (int i = 0; i < sampleCount; i++) {
            double angle = TAU * i / sampleCount + phase;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            double y = center.y + Math.sin(angle * 2.0D + phase) * verticalWave;

            level.sendParticles(
                    particle,
                    x, y, z,
                    1,
                    0.0D, 0.0D, 0.0D,
                    0.0D
            );
        }
    }

    private static void introRing(
            ServerLevel level,
            Iterable<ServerPlayer> participants,
            ParticleOptions particle,
            Vec3 center,
            double radius,
            int points,
            double phase,
            double verticalWave
    ) {
        int sampleCount = Math.max(6, points);

        for (int i = 0; i < sampleCount; i++) {
            double angle = TAU * i / sampleCount + phase;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            double y = center.y
                    + Math.sin(angle * 2.0D + phase) * verticalWave;

            sendIntroParticles(
                    level,
                    participants,
                    particle,
                    x,
                    y,
                    z,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }
    }

    private static void sendIntroParticles(
            ServerLevel level,
            Iterable<ServerPlayer> participants,
            ParticleOptions particle,
            double x,
            double y,
            double z,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed
    ) {
        for (ServerPlayer participant : participants) {
            level.sendParticles(
                    participant,
                    particle,
                    true,
                    true,
                    x,
                    y,
                    z,
                    count,
                    offsetX,
                    offsetY,
                    offsetZ,
                    speed
            );
        }
    }

    private static void radialIntroBurst(
            ServerLevel level,
            Iterable<ServerPlayer> participants,
            ParticleOptions particle,
            Vec3 origin,
            int points,
            double horizontalSpeed,
            double verticalSpeed,
            double phase
    ) {
        int sampleCount =
                Math.max(8, points);

        for (int i = 0; i < sampleCount; i++) {
            double angle =
                    TAU * i / sampleCount
                            + phase;

            double speedVariation =
                    0.72D
                            + (i % 5) * 0.095D;

            double verticalVariation =
                    verticalSpeed
                            * (0.42D + (i % 4) * 0.19D);

            sendIntroParticles(
                    level,
                    participants,
                    particle,
                    origin.x,
                    origin.y,
                    origin.z,
                    0,
                    Math.cos(angle)
                            * horizontalSpeed
                            * speedVariation,
                    verticalVariation,
                    Math.sin(angle)
                            * horizontalSpeed
                            * speedVariation,
                    1.0D
            );
        }
    }

    private static void introGroundDebris(
            ServerLevel level,
            Iterable<ServerPlayer> participants,
            StarCrawlerBossEntity boss,
            int samples,
            double phase,
            double strength
    ) {
        int sampleCount =
                Math.max(6, samples);

        for (int i = 0; i < sampleCount; i++) {
            double angle =
                    TAU * i / sampleCount
                            + phase;

            double sourceRadius =
                    0.45D
                            + (i % 4) * 0.26D;

            double x =
                    boss.getX()
                            + Math.cos(angle) * sourceRadius;

            double z =
                    boss.getZ()
                            + Math.sin(angle) * sourceRadius;

            BlockPos surface =
                    BlockPos.containing(
                            x,
                            boss.getY() - 0.20D,
                            z
                    );

            BlockState state =
                    level.getBlockState(surface);

            if (state.isAir()) {
                continue;
            }

            double speedVariation =
                    0.68D
                            + (i % 3) * 0.16D;

            sendIntroParticles(
                    level,
                    participants,
                    new BlockParticleOption(
                            ParticleTypes.BLOCK,
                            state
                    ),
                    x,
                    boss.getY() + 0.24D,
                    z,
                    0,
                    Math.cos(angle)
                            * strength
                            * speedVariation,
                    strength
                            * (0.55D + (i % 4) * 0.15D),
                    Math.sin(angle)
                            * strength
                            * speedVariation,
                    1.0D
            );
        }
    }

    private static List<ServerPlayer> collectIntroViewers(
            ServerLevel level,
            StarCrawlerBossEntity boss,
            Iterable<ServerPlayer> participants,
            double observerRadius
    ) {
        Map<UUID, ServerPlayer> viewers =
                new LinkedHashMap<>();

        for (ServerPlayer participant : participants) {
            if (participant.connection != null
                    && !participant.isRemoved()) {

                viewers.put(
                        participant.getUUID(),
                        participant
                );
            }
        }

        double observerRadiusSqr =
                observerRadius * observerRadius;

        for (ServerPlayer player : level.players()) {
            if (player.connection == null
                    || player.isRemoved()
                    || PlayerHooks.isFake(player)
                    || player.distanceToSqr(boss)
                    > observerRadiusSqr) {

                continue;
            }

            viewers.putIfAbsent(
                    player.getUUID(),
                    player
            );
        }

        return new ArrayList<>(viewers.values());
    }

    private static double clamp01(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static double lerp(double start, double end, double t) {
        return start + (end - start) * clamp01(t);
    }
}

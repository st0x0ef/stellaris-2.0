package org.exodusstudio.stellaris.common.entities.mobs.heartofluna;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.network.packets.ParasiteCameraShakePacket;

public final class HeartOfLunaVfx {
    private static final DustParticleOptions VIOLET = new DustParticleOptions(0xBA37EF, 0.8F);

    public static void sound(HeartOfLunaBossEntity boss, SoundEvent sound, float volume, float pitch) {
        boss.level().playSound(null, boss.getX(), boss.getY(), boss.getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    public static void shake(LivingEntity target, int ticks, float strength) {
        if (target instanceof ServerPlayer player) NetworkManager.sendToPlayer(player, new ParasiteCameraShakePacket(ticks, strength));
    }

    public static void sparks(ServerLevel level, Vec3 point) {
        particles(level, ParticleTypes.ELECTRIC_SPARK, point, 8, 0.2, 0.04);
    }

    public static void binding(ServerPlayer player) {
        ServerLevel level = player.level();
        for (int i = 0; i < 10; i++) {
            double angle = i * Math.PI / 5 + level.getGameTime() * 0.08;
            Vec3 p = player.position().add(Math.cos(angle) * 0.55, 0.12 + (i % 2) * 0.9, Math.sin(angle) * 0.55);
            particles(level, VIOLET, p, 1, 0, 0);
        }
    }

    public static void impact(HeartOfLunaBossEntity boss, Vec3 point, int kind) {
        if (!(boss.level() instanceof ServerLevel level)) return;
        sparks(level, point);
        Vec3 ground = new Vec3(point.x, boss.getY() + 0.1, point.z);
        if (kind != 3) {
            particles(level, new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(BlockPos.containing(ground).below())), ground, kind == 1 ? 14 : 8, 0.8, 0.08);
            particles(level, ParticleTypes.DUST_PLUME, ground, kind == 1 ? 5 : 3, 0.6, 0.02);
        }
        for (ServerPlayer player : level.players()) {
            double distance = player.position().distanceTo(point);
            if (distance < 48) shake(player, kind == 4 ? 16 : 9, (float) ((1 - distance / 48) * kind * 0.4));
        }
        sound(boss, kind == 3 ? SoundEvents.WARDEN_SONIC_BOOM : kind == 2 ? SoundEvents.WARDEN_ROAR : SoundEvents.WARDEN_ATTACK_IMPACT, 2.5F, kind == 4 ? 0.5F : 0.7F);
        sound(boss, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.3F, kind == 3 ? 1.5F : 0.5F);
    }

    private static void particles(ServerLevel level, ParticleOptions type, Vec3 p, int count, double spread, double speed) {
        for (ServerPlayer viewer : level.players()) {
            double distance = viewer.position().distanceToSqr(p);
            if (distance > 4096) continue;
            level.sendParticles(viewer, type, false, false, p.x, p.y, p.z, distance > 1024 ? Math.max(1, count / 3) : count, spread, spread * 0.5, spread, speed);
        }
    }

    private HeartOfLunaVfx() {}
}

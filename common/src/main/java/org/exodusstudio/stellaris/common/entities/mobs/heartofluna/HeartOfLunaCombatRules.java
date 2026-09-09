package org.exodusstudio.stellaris.common.entities.mobs.heartofluna;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;

public final class HeartOfLunaCombatRules {
    public static final int RAY_CHARGE_DELAY = 12;
    public static final int RAY_FIRE_TICK = 23 + RAY_CHARGE_DELAY;
    public static final int RAY_END_TICK = 32 + RAY_CHARGE_DELAY;

    public static float rayAnimationTicks(float ticks) {
        return ticks < 18 ? ticks : ticks < 18 + RAY_CHARGE_DELAY ? 18 : ticks - RAY_CHARGE_DELAY;
    }

    public static int phaseAfterRay(float health, int completedPhases) {
        return completedPhases == 0 ? 1 : completedPhases == 1 && health <= 20 ? 2 : completedPhases;
    }

    public static int repeatRayCooldown(int completedPhases) { return completedPhases >= 2 ? 80 : 120; }

    public static float gatedHealth(float requested, int completedRays) {
        return Math.max(completedRays == 0 ? 199.99F : completedRays == 1 ? 20 : 0, requested);
    }

    public static boolean thresholdPending(float health, int completedRays) {
        return completedRays == 0 && health < 200 || completedRays == 1 && health <= 20;
    }

    public static boolean faces(Vec3 forward, Vec3 towardSource) {
        return forward.horizontal().normalize().dot(towardSource.horizontal().normalize()) > 0.25;
    }

    public static float punchDamage(boolean buffed) { return buffed ? 28 : 14; }
    public static Vec3 lead(Vec3 velocity, double ticks) {
        Vec3 offset = velocity.horizontal().scale(ticks);
        return offset.lengthSqr() > 2.25 ? offset.normalize().scale(1.5) : offset;
    }

    public static boolean projectileThreat(AABB body, Vec3 origin, Vec3 velocity) {
        if (velocity.lengthSqr() < 0.01) return false;
        double time = body.getCenter().subtract(origin).dot(velocity) / velocity.lengthSqr();
        if (time <= 0 || time > 8) return false;
        Vec3 end = origin.add(velocity.scale(time));
        AABB guard = body.inflate(0.35);
        return guard.contains(origin) || guard.clip(origin, end).isPresent();
    }
    public static float rayDamage(boolean buffed, boolean shielded) { return (buffed ? 80 : 40) * (shielded ? 0.75F : 1); }
    private HeartOfLunaCombatRules() {}
}

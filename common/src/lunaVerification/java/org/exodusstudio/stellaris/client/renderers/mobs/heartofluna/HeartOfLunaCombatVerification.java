package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaCombatRules;

public final class HeartOfLunaCombatVerification {
    public static void main(String[] args) {
        int phases = HeartOfLunaCombatRules.phaseAfterRay(199.99F, 0);
        for (int cast = 0; cast < 20; cast++) {
            phases = HeartOfLunaCombatRules.phaseAfterRay(100, phases);
            require(phases == 1, "Repeat laser must not consume the desperation phase");
            require(HeartOfLunaCombatRules.gatedHealth(0, phases) == 20, "Repeat laser must preserve the final health gate");
        }
        phases = HeartOfLunaCombatRules.phaseAfterRay(20, phases);
        require(phases == 2, "Final phase laser must complete the second gate");
        for (int cast = 0; cast < 20; cast++) require(HeartOfLunaCombatRules.phaseAfterRay(10, phases) == 2, "Repeat laser must not invent additional phases");
        require(HeartOfLunaCombatRules.repeatRayCooldown(2) < HeartOfLunaCombatRules.repeatRayCooldown(1), "Final phase should cast more frequently");
        float previous = 0;
        for (int frame = 0; frame <= 52 * 4; frame++) {
            float tick = frame / 4F;
            float animation = HeartOfLunaCombatRules.rayAnimationTicks(tick);
            require(animation >= previous && animation - previous <= 0.25001F, "Charge hold must preserve continuous animation time");
            if (tick >= 18 && tick <= 30) require(animation == 18, "Laser must visibly hold its charging pose");
            previous = animation;
        }
        require(HeartOfLunaCombatRules.rayAnimationTicks(HeartOfLunaCombatRules.RAY_FIRE_TICK) == 23, "Laser damage and visual discharge are out of sync");
        require(HeartOfLunaCombatRules.RAY_FIRE_TICK - 22 >= 12, "Locked aim must leave a dodge window");
        AABB body = new AABB(-1.3, 0, -1.3, 1.3, 5.65, 1.3);
        require(HeartOfLunaCombatRules.projectileThreat(body, new Vec3(0, 2, 8), new Vec3(0, 0, -2)), "Incoming arrow must trigger guard");
        require(!HeartOfLunaCombatRules.projectileThreat(body, new Vec3(0, 2, 8), new Vec3(0, 0, 2)), "Receding projectile must not waste guard");
        require(!HeartOfLunaCombatRules.projectileThreat(body, new Vec3(5, 2, 8), new Vec3(0, 0, -2)), "Wide miss must not trigger guard");
        require(!HeartOfLunaCombatRules.projectileThreat(body, new Vec3(0, 9, 8), new Vec3(0, 0, -2)), "Overhead projectile must not trigger guard");
        require(!HeartOfLunaCombatRules.projectileThreat(body, new Vec3(0, 2, 8), Vec3.ZERO), "Stationary arrow must not trigger guard");
        require(HeartOfLunaCombatRules.lead(new Vec3(20, 10, 0), 8).length() <= 1.50001, "Prediction must remain bounded");
        require(HeartOfLunaCombatRules.lead(new Vec3(0.1, 5, 0), 4).distanceTo(new Vec3(0.4, 0, 0)) < 0.00001, "Horizontal movement prediction is incorrect");
        for (int yaw = -180; yaw <= 180; yaw += 15) {
            Vec3 facing = Vec3.directionFromRotation(0, yaw);
            require(HeartOfLunaCombatRules.faces(facing, facing.scale(4).add(0, 3, 0)), "Frontal attacker must be blocked at yaw " + yaw);
            require(!HeartOfLunaCombatRules.faces(facing, facing.scale(-4).add(0, 3, 0)), "Rear attacker must bypass guard at yaw " + yaw);
            require(!HeartOfLunaCombatRules.faces(facing, facing.yRot((float)Math.PI / 2)), "Flank must bypass guard at yaw " + yaw);
        }
        for (float burst : new float[]{351, 550, 10000, Float.MAX_VALUE}) {
            float first = HeartOfLunaCombatRules.gatedHealth(550 - burst, 0);
            require(first > 20 && first < 200, "Burst skipped first gate");
            require(HeartOfLunaCombatRules.thresholdPending(first, 0), "First ray missing");
            require(!HeartOfLunaCombatRules.thresholdPending(first, 1), "First ray repeated");
            float second = HeartOfLunaCombatRules.gatedHealth(first - burst, 1);
            require(second == 20, "Burst skipped desperation gate");
            require(HeartOfLunaCombatRules.thresholdPending(second, 1), "Desperation ray missing");
            require(!HeartOfLunaCombatRules.thresholdPending(second, 2), "Desperation ray repeated");
            require(HeartOfLunaCombatRules.gatedHealth(second - burst, 2) == 0, "Boss cannot die after both rays");
        }
        require(!HeartOfLunaCombatRules.thresholdPending(200, 0), "Phase two must begin below 200 HP");
        require(!HeartOfLunaCombatRules.thresholdPending(20.01F, 1), "Desperation ray fired early");
        require(HeartOfLunaCombatRules.gatedHealth(500, 0) == 500, "Ordinary damage changed");
        require(HeartOfLunaCombatRules.punchDamage(false) == 14 && HeartOfLunaCombatRules.punchDamage(true) == 28, "Punch damage changed");
        require(HeartOfLunaCombatRules.rayDamage(false, false) == 40, "Unshielded ray damage changed");
        require(HeartOfLunaCombatRules.rayDamage(false, true) == 30, "Shielded ray damage changed");
        require(HeartOfLunaCombatRules.rayDamage(true, false) == 80, "Buffed ray damage changed");
        require(HeartOfLunaCombatRules.rayDamage(true, true) == 60, "Buffed shielded ray damage changed");
        System.out.println("Verified directional guard across 25 headings, burst gates, threshold boundaries, and all damage variants");
    }

    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}

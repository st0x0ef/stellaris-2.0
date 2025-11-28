package org.exodusstudio.stellaris.common.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;

public class GravityUtils {
    public static void setGravity(LivingEntity entity) {
        Planet planet = PlanetsData.getPlanet(entity.level().dimension());

        if (planet == null) {
            return;
        }

        AttributeInstance gravityAttribute = entity.getAttribute(Attributes.GRAVITY);
        AttributeInstance fallDistanceAttribute = entity.getAttribute(Attributes.SAFE_FALL_DISTANCE);

        double gravity = MPS2ToMCG(planet.gravity());

        if (gravityAttribute != null && fallDistanceAttribute != null) {
            Stellaris.LOG.error(String.valueOf(gravity));
            gravityAttribute.setBaseValue(gravity);
            fallDistanceAttribute.setBaseValue(0.08 / gravity * 3);
        }
    }

    /**
     * @param MPS2 m/s²
     * @return Minecraft Gravity Unit (blocks/t²)
     */
    public static double MPS2ToMCG(double MPS2) {
        if (MPS2>0) return Math.floor(0.00816d * MPS2 * 100000) / 100000;
        else if (MPS2<0) return Math.ceil(0.00816d * MPS2 * 100000) / 100000;
        else return 0;
    }
}

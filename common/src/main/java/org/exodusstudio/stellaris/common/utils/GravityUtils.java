package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class GravityUtils {

    public static final BigDecimal GRAVITY_CONVERSION_RATE = new BigDecimal("0.08").divide(new BigDecimal("9.81"), 20, RoundingMode.HALF_UP);
    private static final Map<Planet, Double> GRAVITY_CACHE = new HashMap<>();

    public static void setGravity(LivingEntity entity) {

        Planet planet = PlanetsData.getPlanet(entity.level().dimension());

        if (planet == null) {
            resetEntityGravity(entity); // Resets gravity on dimensions like The Nether
            return;
        }

        double gravity = getGravity(planet);
        setEntityGravity(entity, gravity);

        Stellaris.LOG.debug(String.valueOf(gravity));
    }

    public static void trySetBaseAttribute(LivingEntity entity, Holder<Attribute> attribute, double value) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);

        if (attributeInstance != null)
            attributeInstance.setBaseValue(value);

    }

    public static void setEntityGravity(LivingEntity entity, double gravity) {
        trySetBaseAttribute(entity, Attributes.GRAVITY, gravity);
        trySetBaseAttribute(entity, Attributes.SAFE_FALL_DISTANCE, 0.08 / gravity * 3);
    }

    public static void resetEntityGravity(LivingEntity entity) {
        trySetBaseAttribute(entity, Attributes.GRAVITY, Attributes.GRAVITY.value().getDefaultValue());
        trySetBaseAttribute(entity, Attributes.SAFE_FALL_DISTANCE, Attributes.SAFE_FALL_DISTANCE.value().getDefaultValue());
    }

    public static double getGravity(@NotNull Planet planet) {
        return GRAVITY_CACHE.computeIfAbsent(planet, p -> MPS2ToMCG(p.gravity()));
    }

    /**
     * @param MPS2 m/s²
     * @return Minecraft Gravity Unit (blocks/t²)
     */
    //TODO replace double with string
    public static double MPS2ToMCG(double MPS2) {
        // replace with GRAVITY_CONVERSION_RATE.multiply(new BigDecimal(MPS2)).setScale(5, RoundingMode.HALF_UP).doubleValue();
        return GRAVITY_CONVERSION_RATE.multiply(BigDecimal.valueOf(MPS2)).setScale(5, RoundingMode.HALF_UP).doubleValue();
    }
}

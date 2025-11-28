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

    private static final BigDecimal EARTH_GRAVITY = new BigDecimal("9.81");
    public static final BigDecimal GRAVITY_CONVERSION_RATE = new BigDecimal("0.08").divide(EARTH_GRAVITY, 20, RoundingMode.HALF_UP);
    public static final BigDecimal SAFE_FALL_DISTANCE_CONVERSION_RATE = new BigDecimal("3").multiply(EARTH_GRAVITY);

    private static final Map<Planet, Double> GRAVITY_CACHE = new HashMap<>();
    private static final Map<Planet, Double> SAFE_FALL_DISTANCE_CACHE = new HashMap<>();
    private static final Map<Planet, Double> FALL_DAMAGE_MULT_CACHE = new HashMap<>();

    public static void setGravity(LivingEntity entity) {

        Planet planet = PlanetsData.getPlanet(entity.level().dimension());

        if (planet == null) {
            resetEntityGravity(entity); // Resets gravity on dimensions like The Nether
            return;
        }

        setEntityGravity(entity, planet);
    }

    public static void trySetBaseAttribute(LivingEntity entity, Holder<Attribute> attribute, double value) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);

        if (attributeInstance != null) {
            Stellaris.LOG.error("changed attribute");
            attributeInstance.setBaseValue(value);
        }

    }

    public static void setEntityGravity(LivingEntity entity, Planet planet) {
        trySetBaseAttribute(entity, Attributes.GRAVITY, getGravity(planet));
        trySetBaseAttribute(entity, Attributes.SAFE_FALL_DISTANCE, getSafeFallDistance(planet));
        trySetBaseAttribute(entity, Attributes.FALL_DAMAGE_MULTIPLIER, getFallDamageMult(planet));
    }

    public static void resetEntityGravity(LivingEntity entity) {
        trySetBaseAttribute(entity, Attributes.GRAVITY, Attributes.GRAVITY.value().getDefaultValue());
        trySetBaseAttribute(entity, Attributes.SAFE_FALL_DISTANCE, Attributes.SAFE_FALL_DISTANCE.value().getDefaultValue());
        trySetBaseAttribute(entity, Attributes.FALL_DAMAGE_MULTIPLIER, Attributes.FALL_DAMAGE_MULTIPLIER.value().getDefaultValue());
    }

    public static double getGravity(@NotNull Planet planet) {
        return GRAVITY_CACHE.computeIfAbsent(planet, p -> MPS2ToMCG(p.gravity()));
    }

    public static double getSafeFallDistance(Planet planet) {
        return SAFE_FALL_DISTANCE_CACHE.computeIfAbsent(planet, p -> computeSafeFallDistance(p.gravity()));
    }

    /// @param newGravity in m/s²
    public static double computeSafeFallDistance(String newGravity) {
        return SAFE_FALL_DISTANCE_CONVERSION_RATE.divide(new BigDecimal(newGravity), 5, RoundingMode.HALF_UP).doubleValue();
    }

    public static double getFallDamageMult(Planet planet) {
        return FALL_DAMAGE_MULT_CACHE.computeIfAbsent(planet, p -> computeFallDamageMult(p.gravity()));
    }

    /// @param newGravity in m/s²
    public static double computeFallDamageMult(String newGravity) {
        return new BigDecimal(newGravity).divide(EARTH_GRAVITY, 5, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * @param MPS2 m/s²
     * @return Minecraft Gravity Unit (blocks/t²)
     */
    public static double MPS2ToMCG(String MPS2) {
        return GRAVITY_CONVERSION_RATE.multiply(new BigDecimal(MPS2)).setScale(5, RoundingMode.HALF_UP).doubleValue();
    }
}

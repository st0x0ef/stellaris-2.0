package org.exodusstudio.stellaris.common.utils;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GravityUtils {

    public static final BigDecimal EARTH_GRAVITY = new BigDecimal("9.81");
    public static final BigDecimal SAFE_FALL_DISTANCE_CONVERSION_RATE = new BigDecimal("3").multiply(EARTH_GRAVITY);

    public static final BigDecimal GRAVITY_LIVING_CONVERSION_RATE = new BigDecimal("0.08").divide(EARTH_GRAVITY, 10, RoundingMode.HALF_UP);
    public static final BigDecimal GRAVITY_PROJECTILE_CONVERSION_RATE = new BigDecimal("0.05").divide(EARTH_GRAVITY, 10, RoundingMode.HALF_UP);
    public static final BigDecimal GRAVITY_FALLING_CONVERSION_RATE = new BigDecimal("0.04").divide(EARTH_GRAVITY, 10, RoundingMode.HALF_UP);
    public static final BigDecimal GRAVITY_XP_CONVERSION_RATE = new BigDecimal("0.03").divide(EARTH_GRAVITY, 10, RoundingMode.HALF_UP);
    public static final BigDecimal GRAVITY_LLAMA_SPIT_CONVERSION_RATE = new BigDecimal("0.06").divide(EARTH_GRAVITY, 10, RoundingMode.HALF_UP);
    public static final BigDecimal GRAVITY_XP_BOTTLE_CONVERSION_RATE = new BigDecimal("0.07").divide(EARTH_GRAVITY, 10, RoundingMode.HALF_UP);
    public static final BigDecimal GRAVITY_WATER_MINECART_CONVERSION_RATE = new BigDecimal("0.005").divide(EARTH_GRAVITY, 10, RoundingMode.HALF_UP);


    private static final Map<Planet, Map<BigDecimal, Double>> GRAVITY_CACHE = new ConcurrentHashMap<>();
    private static final Map<Planet, Double> SAFE_FALL_DISTANCE_CACHE = new ConcurrentHashMap<>();
    private static final Map<Planet, Double> FALL_DAMAGE_MULT_CACHE = new ConcurrentHashMap<>();

    public static void setLivingEntityGravity(LivingEntity entity) {
        Planet planet = PlanetsData.getPlanet(entity.level().dimension());

        if (planet == null || !Stellaris.CONFIG.gravityConfig.enableGravityEffects) {
            resetLivingEntityGravity(entity);
        } else {
            setLivingEntityGravity(entity, planet);
        }
    }

    public static double getEntityGravity(BigDecimal conversionRate, Entity entity) {
        Planet planet = PlanetsData.getPlanet(entity.level().dimension());

        if (planet == null || !Stellaris.CONFIG.gravityConfig.enableGravityEffects) {
            planet = PlanetsData.getPlanet(Level.OVERWORLD);
        }
        return getGravity(conversionRate, planet) + normalizeGravity(planet, entity.level(), conversionRate, entity.blockPosition());

    }

    public static double normalizeGravity(Planet planet, Level level, BigDecimal conversionRate, BlockPos entityPos) {
        // Check for Gravity Normalizer in the chunk
        AtomicDouble normalize = new AtomicDouble(0.0);
        AtomicInteger manipulatorCount = new AtomicInteger(0);
        level.getChunkAt(entityPos).getBlockEntities().forEach((pos, blockEntity) -> {
            if (blockEntity instanceof GravityManipulatorBlockEntity gravityManipulator) {
                if (gravityManipulator.getEnergy(null).getEnergy() > 0) {
                    normalize.addAndGet(gravityManipulator.getDifferenceGravity(planet.gravity()));
                    manipulatorCount.incrementAndGet();
                }
            }
        });

        if (normalize.get() == 0.0) {
            return 0.0;
        }

        return MPS2ToMCG(conversionRate, normalize.get() / manipulatorCount.get());
    }

    public static void trySetBaseAttribute(LivingEntity entity, Holder<Attribute> attribute, double value) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);

        if (attributeInstance != null) {
            attributeInstance.setBaseValue(value);
        }
    }

    public static void setLivingEntityGravity(LivingEntity entity, Planet planet) {
        trySetBaseAttribute(entity, Attributes.GRAVITY, getGravity(GRAVITY_LIVING_CONVERSION_RATE, planet) + normalizeGravity(planet, entity.level(), GRAVITY_LIVING_CONVERSION_RATE, entity.blockPosition()));
        trySetBaseAttribute(entity, Attributes.SAFE_FALL_DISTANCE, getSafeFallDistance(planet));
        trySetBaseAttribute(entity, Attributes.FALL_DAMAGE_MULTIPLIER, getFallDamageMult(planet));
    }

    public static void resetLivingEntityGravity(LivingEntity entity) {
        trySetBaseAttribute(entity, Attributes.GRAVITY, Attributes.GRAVITY.value().getDefaultValue());
        trySetBaseAttribute(entity, Attributes.SAFE_FALL_DISTANCE, Attributes.SAFE_FALL_DISTANCE.value().getDefaultValue());
        trySetBaseAttribute(entity, Attributes.FALL_DAMAGE_MULTIPLIER, Attributes.FALL_DAMAGE_MULTIPLIER.value().getDefaultValue());
    }

    private static double getGravity(BigDecimal conversionRate, Planet planet) {
        Map<BigDecimal, Double> planetGravityMap = GRAVITY_CACHE.computeIfAbsent(planet, p -> new ConcurrentHashMap<>());
        return planetGravityMap.computeIfAbsent(conversionRate, c -> MPS2ToMCG(c, planet.gravity()));
    }

    private static double getSafeFallDistance(Planet planet) {
        return SAFE_FALL_DISTANCE_CACHE.computeIfAbsent(planet, p -> computeSafeFallDistance(p.gravity()));
    }

    /// @param newGravity in m/s²
    private static double computeSafeFallDistance(double newGravity) {
        return SAFE_FALL_DISTANCE_CONVERSION_RATE.divide(new BigDecimal(newGravity), 5, RoundingMode.HALF_UP).doubleValue();
    }

    private static double getFallDamageMult(Planet planet) {
        return FALL_DAMAGE_MULT_CACHE.computeIfAbsent(planet, p -> computeFallDamageMult(p.gravity()));
    }

    /// @param newGravity in m/s²
    private static double computeFallDamageMult(double newGravity) {
        return new BigDecimal(newGravity).divide(EARTH_GRAVITY, 5, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * @param MPS2 gravity in m/s²
     * @return Minecraft Gravity Unit (blocks/t²)
     */
    public static double MPS2ToMCG(BigDecimal conversionRate, String MPS2) {
        return MPS2ToMCG(conversionRate, new BigDecimal(MPS2));
    }

    /**
     * @param MPS2 gravity in m/s²
     * @return Minecraft Gravity Unit (blocks/t²)
     */
    public static double MPS2ToMCG(BigDecimal conversionRate, double MPS2) {
        return MPS2ToMCG(conversionRate, new BigDecimal(MPS2));
    }

    /**
     * @param MPS2 gravity in m/s²
     * @return Minecraft Gravity Unit (blocks/t²)
     */
    public static double MPS2ToMCG(BigDecimal conversionRate, BigDecimal MPS2) {
        return conversionRate.multiply(MPS2).setScale(5, RoundingMode.HALF_UP).doubleValue();
    }
}

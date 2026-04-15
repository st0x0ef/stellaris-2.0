package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.exodusstudio.stellaris.common.world.features.AshFloorFeature;
import org.exodusstudio.stellaris.common.world.features.BurntForestTreeFeature;
import org.exodusstudio.stellaris.common.world.features.CraterFeature;


public class FeaturesRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create("stellaris", Registries.FEATURE);

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> CRATER_SMALL =
            FEATURES.register("crater_small", () -> new CraterFeature(NoneFeatureConfiguration.CODEC, 8, 10, Blocks.AIR::defaultBlockState, () -> BlocksRegistry.MOON_SAND.block().get().defaultBlockState(), true));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> CRATER_MEDIUM =
            FEATURES.register("crater_medium", () -> new CraterFeature(NoneFeatureConfiguration.CODEC, 12, 16, Blocks.AIR::defaultBlockState, () -> BlocksRegistry.MOON_SAND.block().get().defaultBlockState(), true));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> CRATER_LARGE =
            FEATURES.register("crater_large", () -> new CraterFeature(NoneFeatureConfiguration.CODEC, 18, 20, Blocks.AIR::defaultBlockState, () -> BlocksRegistry.MOON_SAND.block().get().defaultBlockState(), true));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> CRATER_LAKE_BLUE_LIQUID =
            FEATURES.register("crater_lake_blue_liquid", () -> new CraterFeature(NoneFeatureConfiguration.CODEC, 6, 18, () -> BlocksRegistry.BLUE_LIQUID.get().defaultBlockState(), () -> BlocksRegistry.MOON_STONE.block().get().defaultBlockState(), false));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> BURNT_FOREST_TREE =
            FEATURES.register("burnt_forest_tree", () -> new BurntForestTreeFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> ASH_FLOOR =
            FEATURES.register("ash_floor", () -> new AshFloorFeature(NoneFeatureConfiguration.CODEC));

    public static void register() {
        FEATURES.register();
    }
}
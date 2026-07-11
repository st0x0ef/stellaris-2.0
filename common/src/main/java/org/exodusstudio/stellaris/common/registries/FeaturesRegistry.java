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
import org.exodusstudio.stellaris.common.world.features.LunarTreeFeature;


public class FeaturesRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create("stellaris", Registries.FEATURE);

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> CRATER =
            FEATURES.register("crater", () -> new CraterFeature(NoneFeatureConfiguration.CODEC, 10, 20, Blocks.AIR::defaultBlockState, () -> BlocksRegistry.MOON_SAND.block().get().defaultBlockState(), true));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> CRATER_LAKE_BLUE_LIQUID =
            FEATURES.register("crater_lake_blue_liquid", () -> new CraterFeature(NoneFeatureConfiguration.CODEC, 6, 18, () -> BlocksRegistry.BLUE_LIQUID.get().defaultBlockState(), () -> BlocksRegistry.MOON_STONE.block().get().defaultBlockState(), false));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> BURNT_FOREST_TREE =
            FEATURES.register("burnt_forest_tree", () -> new BurntForestTreeFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> LUNAR_TREE =
            FEATURES.register("lunar_tree", () -> new LunarTreeFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> ASH_FLOOR =
            FEATURES.register("ash_floor", () -> new AshFloorFeature(NoneFeatureConfiguration.CODEC));

    public static void register() {
        FEATURES.register();
    }
}

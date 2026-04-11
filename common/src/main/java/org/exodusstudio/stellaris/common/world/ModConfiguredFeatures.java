package org.exodusstudio.stellaris.common.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.exodusstudio.stellaris.common.registries.FeaturesRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> LAKE_OIL = registerKey("lake_oil");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LUNAR_TREE = registerKey("lunar_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRATER_SMALL = registerKey("crater_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRATER_MEDIUM = registerKey("crater_medium");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRATER_LARGE = registerKey("crater_large");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BURNT_FOREST_TREE = registerKey("burnt_forest_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASH_FLOOR = registerKey("ash_floor");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TITANIUM = registerKey("ore_titanium");

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, IdentifierUtils.id(name));
    }
}
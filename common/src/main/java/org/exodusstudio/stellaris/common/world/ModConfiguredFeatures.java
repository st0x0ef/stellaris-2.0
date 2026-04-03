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

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(CRATER_SMALL, new ConfiguredFeature<>(FeaturesRegistry.CRATER_SMALL.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(CRATER_MEDIUM, new ConfiguredFeature<>(FeaturesRegistry.CRATER_MEDIUM.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(CRATER_LARGE, new ConfiguredFeature<>(FeaturesRegistry.CRATER_LARGE.get(), NoneFeatureConfiguration.INSTANCE));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, IdentifierUtils.id(name));
    }
}

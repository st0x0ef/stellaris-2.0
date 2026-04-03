package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.exodusstudio.stellaris.common.world.feature.CraterFeature;

public class FeaturesRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create("stellaris", Registries.FEATURE);

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> CRATER_SMALL =
            FEATURES.register("crater_small", () -> new CraterFeature(NoneFeatureConfiguration.CODEC, 8));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> CRATER_MEDIUM =
            FEATURES.register("crater_medium", () -> new CraterFeature(NoneFeatureConfiguration.CODEC, 16));

    public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> CRATER_LARGE =
            FEATURES.register("crater_large", () -> new CraterFeature(NoneFeatureConfiguration.CODEC, 28));

    public static void register() {
        FEATURES.register();
    }
}
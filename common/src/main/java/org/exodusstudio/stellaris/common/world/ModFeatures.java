package org.exodusstudio.stellaris.common.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.world.configuration.*;
import org.exodusstudio.stellaris.common.world.features.*;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Stellaris.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<Feature<LargeSpeleothemConfiguration>> LARGE_ICICLE = FEATURES.register("large_icicle", () -> new LargeSpeleothemFeature(LargeSpeleothemConfiguration.CODEC));
    public static final RegistrySupplier<Feature<SequenceConfiguration>> SEQUENCE = FEATURES.register(
            "sequence", () -> new SequenceFeature(SequenceConfiguration.CODEC));
    public static final RegistrySupplier<Feature<SpikeFeatureConfiguration>> SPIKE = FEATURES.register(
            "spike", () -> new ComplexSpikeFeature(SpikeFeatureConfiguration.CODEC)
    );
    public static final RegistrySupplier<Feature<PointySpikeFeatureConfiguration>> POINTY_SPIKE = FEATURES.register(
            "pointy_spike", () -> new PointySpikeFeature(PointySpikeFeatureConfiguration.CODEC)
    );
    public static final RegistrySupplier<Feature<CrevasseFeatureConfiguration>> CREVASSE_FEATURE = FEATURES.register("crevasse_feature", CrevasseFeature::new);

    public static final RegistrySupplier<Feature<SpeleothemClusterConfiguration>> SPELEOTHEM_CLUSTER = FEATURES.register("speleothem_cluster", SpeleothemClusterFeature::new);
}

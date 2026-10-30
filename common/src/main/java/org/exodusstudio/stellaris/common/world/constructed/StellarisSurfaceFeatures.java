package org.exodusstudio.stellaris.common.world.constructed;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.exodusstudio.stellaris.common.world.FeaturesRecord;
import org.exodusstudio.stellaris.common.world.ModFeatures;
import org.exodusstudio.stellaris.common.world.configuration.PointySpikeFeatureConfiguration;
import org.exodusstudio.stellaris.common.world.configuration.SpikeFeatureConfiguration;

public class StellarisSurfaceFeatures {
    public static final FeaturesRecord CIRCULAR_SPIKES = FeaturesRecord.create("circular_spikes");
    public static final FeaturesRecord POINTY_SPIKES = FeaturesRecord.create("pointy_spikes");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?,?>> context) {
        FeatureUtils.register(context, CIRCULAR_SPIKES.configuredFeature(), ModFeatures.SPIKE.get(), new SpikeFeatureConfiguration(Blocks.BLUE_ICE.defaultBlockState(), UniformInt.of(14,20),UniformInt.of(10,17), ConstantInt.of(3), ConstantInt.of(360), ConstantInt.of(6)));
        FeatureUtils.register(context, POINTY_SPIKES.configuredFeature(), ModFeatures.POINTY_SPIKE.get(), new PointySpikeFeatureConfiguration(Blocks.PACKED_ICE.defaultBlockState(), UniformInt.of(14,20),UniformInt.of(10,17), ConstantInt.of(3), ConstantInt.of(6)));

    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configureFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacementUtils.register(context,
                CIRCULAR_SPIKES.placedFeature(),
                configureFeatures.getOrThrow(CIRCULAR_SPIKES.configuredFeature()),
                CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register(context,
                POINTY_SPIKES.placedFeature(),
                configureFeatures.getOrThrow(POINTY_SPIKES.configuredFeature()),
                CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }
}

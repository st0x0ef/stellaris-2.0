package org.exodusstudio.stellaris.common.world.constructed;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.exodusstudio.stellaris.common.world.FeaturesRecord;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.common.world.ModFeatures;
import org.exodusstudio.stellaris.common.world.configuration.CrevasseFeatureConfiguration;
import org.exodusstudio.stellaris.common.world.configuration.LargeSpeleothemConfiguration;
import org.exodusstudio.stellaris.common.world.configuration.SpeleothemClusterConfiguration;
import org.exodusstudio.stellaris.common.world.features.utils.SpeleothemUtils;

public class StellarisCaveFeatures {
    public static final FeaturesRecord LARGE_ICICLE = FeaturesRecord.create("large_icicle");
    public static final FeaturesRecord ICE_CRYSTAL = FeaturesRecord.create("ice_crystal");
    public static final FeaturesRecord ICE_CREVASSE = FeaturesRecord.create("ice_crevasse");
    public static final FeaturesRecord ICICLE_CLUSTER = FeaturesRecord.create("icicle_cluster");


    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?,?>> context) {
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
        FeatureUtils.register(
                context,
                LARGE_ICICLE.configuredFeature(),
                ModFeatures.LARGE_ICICLE.get(),
                new LargeSpeleothemConfiguration(
                        Blocks.PACKED_ICE.defaultBlockState(),
                        blocks.getOrThrow(TagsRegistry.BlockTags.LARGE_ICICLE_REPLACEABLE),
                        30,
                        ClampedInt.of(UniformInt.of(3, 19), 3, 16),
                        UniformFloat.of(0.4f, 2f),
                        0.33f,
                        UniformFloat.of(0.3F, 0.9F),
                        UniformFloat.of(0.4F, 1.0F),
                        UniformFloat.of(0.0F, 0.3F),
                        4,
                        0.6f
                )
        );
        FeatureUtils.register(context, ICE_CRYSTAL.configuredFeature(), Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(BlocksRegistry.ICE_CRYSTAL.block().get())));
        FeatureUtils.register(context,
                ICE_CREVASSE.configuredFeature(),
                ModFeatures.CREVASSE_FEATURE.get(),
                new CrevasseFeatureConfiguration(
                        4, 16, 10, 8, 8, 4, 0.6f, 0.05f,
                        UniformInt.of(6, 10),
                        UniformInt.of(4, 6),
                        BlockStateProvider.simple(BlocksRegistry.BRITTLE_ICE.block().get()),
                        BlockStateProvider.simple(Blocks.ICE),
                        BlockPredicate.matchesTag(TagsRegistry.BlockTags.ICE_CREVASSE_REPLACEABLE)
//                        BlockPredicate.matchesTag(TagsRegistry.BlockTags.BLOCKS_FLUID_FLOW)
                )

        );
        FeatureUtils.register(
                context,
                ICICLE_CLUSTER.configuredFeature(),
                ModFeatures.SPELEOTHEM_CLUSTER.get(),
                new SpeleothemClusterConfiguration(
                        Blocks.PACKED_ICE.defaultBlockState(),
                        BlocksRegistry.ICICLE.block().get().defaultBlockState(),
                        HolderSet.empty(),
                        32,
                        UniformInt.of(3, 5),
                        ConstantInt.of(6),
                        1,
                        3,
                        UniformInt.of(2, 3),
                        UniformFloat.of(0.3f, 0.7f),
                        ConstantFloat.ZERO,
                        0.1f,
                        3,
                        8,
                        new SpeleothemClusterConfiguration.PlacementOptions(SpeleothemClusterConfiguration.PlacementMode.CEILING_ONLY, SpeleothemUtils.BaseBlockTransformer.SET_ATTACHED, false)
                )
        );
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configureFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(
                context,
                LARGE_ICICLE.placedFeature(),
                configureFeatures.getOrThrow(LARGE_ICICLE.configuredFeature()),
                CountPlacement.of(UniformInt.of(48,64)),
                InSquarePlacement.spread(),
                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                BiomeFilter.biome()
        );
        PlacementUtils.register(
                context,
                ICE_CRYSTAL.placedFeature(),
                configureFeatures.getOrThrow(ICE_CRYSTAL.configuredFeature()),
                CountPlacement.of(UniformInt.of(48, 64)),
                InSquarePlacement.spread(),
                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                CountPlacement.of(2),
                RandomOffsetPlacement.ofTriangle(2, 0),
                EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.matchesBlocks(Blocks.PACKED_ICE), BlockPredicate.ONLY_IN_AIR_PREDICATE, 32),
                RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                BiomeFilter.biome()
        );
        PlacementUtils.register(
                context,
                ICE_CREVASSE.placedFeature(),
                configureFeatures.getOrThrow(ICE_CREVASSE.configuredFeature()),
                CountPlacement.of(ConstantInt.of(16)),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(24), VerticalAnchor.belowTop(64)),
                EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.matchesTag(TagsRegistry.BlockTags.ICE_CREVASSE_REPLACEABLE), BlockPredicate.ONLY_IN_AIR_PREDICATE, 8),
                BiomeFilter.biome()
        );
        PlacementUtils.register(
                context,
                ICICLE_CLUSTER.placedFeature(),
                configureFeatures.getOrThrow(ICICLE_CLUSTER.configuredFeature()),
                CountPlacement.of(UniformInt.of(24, 64)),
                InSquarePlacement.spread(),
                PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                BiomeFilter.biome()
        );
    }
}

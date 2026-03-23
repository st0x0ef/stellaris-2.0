package org.exodusstudio.stellaris.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

import java.util.List;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> LAKE_OIL_UNDERGROUND = createKey("lake_oil_underground");
    public static final ResourceKey<PlacedFeature> LAKE_OIL_SURFACE = createKey("lake_oil_surface");
    public static final ResourceKey<PlacedFeature> LUNAR_TREE = createKey("lunar_tree");

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, IdentifierUtils.id(name));
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, LAKE_OIL_UNDERGROUND, configuredFeatures.getOrThrow(ModConfiguredFeatures.LAKE_OIL), List.of(RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.top())), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5), BiomeFilter.biome()));
        register(context, LAKE_OIL_SURFACE, configuredFeatures.getOrThrow(ModConfiguredFeatures.LAKE_OIL), List.of(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, LUNAR_TREE, configuredFeatures.getOrThrow(ModConfiguredFeatures.LUNAR_TREE),
                List.of(PlacementUtils.filteredByBlockSurvival(BlocksRegistry.LUNAR_SAPLING.block().get())));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

}

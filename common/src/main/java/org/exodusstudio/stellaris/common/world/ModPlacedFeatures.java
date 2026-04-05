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
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> LAKE_OIL_UNDERGROUND = createKey("lake_oil_underground");
    public static final ResourceKey<PlacedFeature> LAKE_OIL_SURFACE = createKey("lake_oil_surface");
    public static final ResourceKey<PlacedFeature> LUNAR_TREE = createKey("lunar_tree");
    public static final ResourceKey<PlacedFeature> CRATER_SMALL = createKey("crater_small");
    public static final ResourceKey<PlacedFeature> CRATER_MEDIUM = createKey("crater_medium");
    public static final ResourceKey<PlacedFeature> CRATER_LARGE = createKey("crater_large");
    public static final ResourceKey<PlacedFeature> BURNT_FOREST_TREE = createKey("burnt_forest_tree");
    public static final ResourceKey<PlacedFeature> ASH_FLOOR = createKey("ash_floor");

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, IdentifierUtils.id(name));
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, LAKE_OIL_UNDERGROUND, configuredFeatures.getOrThrow(ModConfiguredFeatures.LAKE_OIL), List.of(RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.top())), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5), BiomeFilter.biome()));
        register(context, LAKE_OIL_SURFACE, configuredFeatures.getOrThrow(ModConfiguredFeatures.LAKE_OIL), List.of(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, LUNAR_TREE, configuredFeatures.getOrThrow(ModConfiguredFeatures.LUNAR_TREE),
                List.of(PlacementUtils.filteredByBlockSurvival(BlocksRegistry.LUNAR_SAPLING.block().get())));

        context.register(CRATER_SMALL, new PlacedFeature(
                configuredFeatures.getOrThrow(ModConfiguredFeatures.CRATER_SMALL),
                List.of(
                        RarityFilter.onAverageOnceEvery(3),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));

        context.register(CRATER_MEDIUM, new PlacedFeature(
                configuredFeatures.getOrThrow(ModConfiguredFeatures.CRATER_MEDIUM),
                List.of(
                        RarityFilter.onAverageOnceEvery(6),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));

        context.register(CRATER_LARGE, new PlacedFeature(
                configuredFeatures.getOrThrow(ModConfiguredFeatures.CRATER_LARGE),
                List.of(
                        RarityFilter.onAverageOnceEvery(12),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));

        context.register(BURNT_FOREST_TREE, new PlacedFeature(
                configuredFeatures.getOrThrow(ModConfiguredFeatures.BURNT_FOREST_TREE),
                List.of(
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));

        context.register(ASH_FLOOR, new PlacedFeature(
                configuredFeatures.getOrThrow(ModConfiguredFeatures.ASH_FLOOR),
                List.of(
                        CountPlacement.of(4),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BiomeFilter.biome()
                )
        ));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
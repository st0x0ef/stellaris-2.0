package org.exodusstudio.stellaris.worldgen.placement;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.exodusstudio.stellaris.util.worldgen.StellarisPlacementUtil;
import org.exodusstudio.stellaris.worldgen.feature.StellarisTreeFeatures;

import java.util.List;

public class StellarisTreePlacements {
    public static final ResourceKey<PlacedFeature> DEAD_TREE_CHECKED = StellarisPlacementUtil.createKey("dead_tree");
//    public static final ResourceKey<PlacedFeature> DEAD_TREE_MEDIUM_CHECKED = StellarisPlacementUtil.createKey("dead_tree_medium");
//    public static final ResourceKey<PlacedFeature> DEAD_TREE_LARGE_CHECKED = StellarisPlacementUtil.createKey("dead_tree_large");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatureGetter = context.lookup(Registries.CONFIGURED_FEATURE);

        final Holder<ConfiguredFeature<?, ?>> DEAD_TREE = configuredFeatureGetter.getOrThrow(StellarisTreeFeatures.DEAD_TREE);
//        final Holder<ConfiguredFeature<?, ?>> DEAD_TREE_MEDIUM = configuredFeatureGetter.getOrThrow(StellarisTreeFeatures.DEAD_TREE_MEDIUM);
//        final Holder<ConfiguredFeature<?, ?>> DEAD_TREE_LARGE = configuredFeatureGetter.getOrThrow(StellarisTreeFeatures.DEAD_TREE_LARGE);


        register(context, StellarisTreePlacements.DEAD_TREE_CHECKED, DEAD_TREE, List.of(PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING)));
//        register(context, StellarisTreePlacements.DEAD_TREE_MEDIUM_CHECKED, DEAD_TREE_MEDIUM, List.of(PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING)));
//        register(context, StellarisTreePlacements.DEAD_TREE_LARGE_CHECKED, DEAD_TREE_LARGE, List.of(PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING)));

    }

    protected static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers)
    {
        register(context, placedFeatureKey, configuredFeature, List.of(modifiers));
    }

    protected static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, List<PlacementModifier> modifiers)
    {
        context.register(placedFeatureKey, new PlacedFeature(configuredFeature, modifiers));
    }
}

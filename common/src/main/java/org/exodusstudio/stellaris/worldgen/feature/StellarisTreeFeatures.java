package org.exodusstudio.stellaris.worldgen.feature;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.exodusstudio.stellaris.util.worldgen.StellarisFeatureUtil;
import org.exodusstudio.stellaris.worldgen.feature.configurations.StellarisTreeConfiguration;
import org.jetbrains.annotations.NotNull;

public class StellarisTreeFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_TREE = StellarisFeatureUtil.createKey("dead_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_TREE_MEDIUM = StellarisFeatureUtil.createKey("dead_tree_medium");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_TREE_LARGE = StellarisFeatureUtil.createKey("dead_tree_large");


    public static void bootstrap(BootstrapContext<@NotNull ConfiguredFeature<?,?>> context) {
        register(context, StellarisTreeFeatures.DEAD_TREE, StellarisBaseFeatures.DEAD_TREE.get(), createTaigaLikeTree(Blocks.OAK_LOG).minHeight(10).maxHeight(30).build());
    }

    private static StellarisTreeConfiguration.Builder createTaigaLikeTree(Block logBlock, Block leafBlock) {
        return new StellarisTreeConfiguration.Builder().trunk(BlockStateProvider.simple(logBlock)).foliage(BlockStateProvider.simple(leafBlock));
    }
    private static StellarisTreeConfiguration.Builder createTaigaLikeTree(Block logBlock) {
        return new StellarisTreeConfiguration.Builder().trunk(BlockStateProvider.simple(logBlock));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey, F feature, FC configuration)
    {
        context.register(configuredFeatureKey, new ConfiguredFeature<>(feature, configuration));
    }
}

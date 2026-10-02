package org.exodusstudio.stellaris.common.world;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

import java.util.List;

public class StellarisOreFeatures {
    public static final FeaturesRecord MARS_IRON_ORE = FeaturesRecord.create("mars_iron_ore");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest marsStoneReplaceable = new TagMatchTest(TagsRegistry.BlockTags.MARS_STONE_ORE_REPLACEABLE);
        FeatureUtils.register(context, MARS_IRON_ORE.configuredFeature(), Feature.ORE, new OreConfiguration(
                marsStoneReplaceable, BlocksRegistry.MARS_STONE_IRON_ORE.block().get().defaultBlockState(), 9
        ));

        
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configureFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(context,
                MARS_IRON_ORE.placedFeature(),
                configureFeatures.getOrThrow(MARS_IRON_ORE.configuredFeature()),
                orePlacement(CountPlacement.of(12), HeightRangePlacement.triangle(VerticalAnchor.absolute(-64),  VerticalAnchor.absolute(80)))
        );
    }

    private static List<PlacementModifier> orePlacement(final PlacementModifier frequencyModifier, final PlacementModifier heightRange) {
        return List.of(frequencyModifier, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
    }
}

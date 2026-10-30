package org.exodusstudio.stellaris.common.world.constructed;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.exodusstudio.stellaris.common.world.FeaturesRecord;
import org.exodusstudio.stellaris.common.world.ModFeatures;
import org.exodusstudio.stellaris.common.world.configuration.SequenceConfiguration;

import java.util.List;

public class StellarisOreFeatures {
    /// [FeaturesRecord]
    public static final FeaturesRecord MARS_ORE_IRON_SMALL = FeaturesRecord.create("mars_ore_iron_small");
    public static final FeaturesRecord ICE_CAVE_ORE_IRON_SMALL = FeaturesRecord.create("ice_cave_ore_iron_small");

    /// [ConfiguredFeature]
    public static final ResourceKey<ConfiguredFeature<?, ?>> MARS_ORE_IRON = FeaturesRecord.createConfiguredFeature("mars_ore_iron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ICE_CAVE_ORE_IRON = FeaturesRecord.createConfiguredFeature("ice_cave_ore_iron");

    /// [PlacedFeature]

    public static final ResourceKey<PlacedFeature> ICE_CAVE_ORE_IRON_UPPER = FeaturesRecord.createPlacementFeature("ice_cave_ore_iron_upper");
    public static final ResourceKey<PlacedFeature> ICE_CAVE_ORE_IRON_MIDDLE =  FeaturesRecord.createPlacementFeature("ice_cave_ore_iron_middle");
    public static final ResourceKey<PlacedFeature> MARS_ORE_IRON_MIDDLE = FeaturesRecord.createPlacementFeature("mars_ore_iron_middle");
    public static final ResourceKey<PlacedFeature> MARS_ORE_IRON_UPPER = FeaturesRecord.createPlacementFeature("mars_ore_iron_upper");

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest marsStoneReplaceable = new TagMatchTest(TagsRegistry.BlockTags.MARS_STONE_ORE_REPLACEABLE);
        RuleTest iceCaveMarsStoneReplaceable = new TagMatchTest(TagsRegistry.BlockTags.ICE_CAVE_ORE_REPLACEABLE);

       Holder<ConfiguredFeature<?, ?>> oreIronConfiguredFeature = Holder.direct(oreFeatureBuilder(marsStoneReplaceable, BlocksRegistry.MARS_STONE_IRON_ORE.block().get().defaultBlockState(), 9));
        Holder<ConfiguredFeature<?, ?>> oreIronSmallConfiguredFeature = Holder.direct(oreFeatureBuilder(marsStoneReplaceable, BlocksRegistry.MARS_STONE_IRON_ORE.block().get().defaultBlockState(), 4));

        FeatureUtils.register(context, MARS_ORE_IRON_SMALL.configuredFeature(), Feature.ORE, new OreConfiguration(
                marsStoneReplaceable, BlocksRegistry.MARS_STONE_IRON_ORE.block().get().defaultBlockState(), 4
        ));
        FeatureUtils.register(context, MARS_ORE_IRON, Feature.ORE, new OreConfiguration(
                marsStoneReplaceable, BlocksRegistry.MARS_STONE_IRON_ORE.block().get().defaultBlockState(), 9
        ));
        FeatureUtils.register(context, ICE_CAVE_ORE_IRON_SMALL.configuredFeature(), ModFeatures.SEQUENCE.get(), hostedOre(
                iceCaveMarsStoneReplaceable, BlocksRegistry.MARS_STONE.block().get().defaultBlockState(), oreIronSmallConfiguredFeature
        ));
        FeatureUtils.register(context, ICE_CAVE_ORE_IRON, ModFeatures.SEQUENCE.get(), hostedOre(
                iceCaveMarsStoneReplaceable, BlocksRegistry.MARS_STONE.block().get().defaultBlockState(), oreIronConfiguredFeature
        ));
//        FeatureUtils.register(context, TEST.configuredFeature(), ModFeatures.SPIKE.get(), new SpikeFeatureConfiguration(Blocks.BLUE_ICE.defaultBlockState(), ConstantInt.of(6), ConstantInt.of(9), UniformInt.of(30,60), ConstantInt.of(4)));
        
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configureFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Common Placement Modifier List
        List<PlacementModifier> oreIronUpperModifiers = commonOrePlacement(90, HeightRangePlacement.triangle(VerticalAnchor.absolute(80),  VerticalAnchor.absolute(384)));
        List<PlacementModifier> oreIronMediumModifiers = commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56)));
        List<PlacementModifier> oreIronSmallModifiers = commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.bottom(),  VerticalAnchor.absolute(80)));



        PlacementUtils.register(context,
                MARS_ORE_IRON_SMALL.placedFeature(),
                configureFeatures.getOrThrow(MARS_ORE_IRON_SMALL.configuredFeature()),
                oreIronSmallModifiers
        );
        PlacementUtils.register(context,
                MARS_ORE_IRON_MIDDLE,
                configureFeatures.getOrThrow(MARS_ORE_IRON),
                oreIronMediumModifiers
        );
        PlacementUtils.register(context,
                MARS_ORE_IRON_UPPER,
                configureFeatures.getOrThrow(MARS_ORE_IRON),
                oreIronUpperModifiers
        );
        PlacementUtils.register(context,
                ICE_CAVE_ORE_IRON_UPPER,
                configureFeatures.getOrThrow(ICE_CAVE_ORE_IRON),
                oreIronUpperModifiers
                );
        PlacementUtils.register(context,
                ICE_CAVE_ORE_IRON_MIDDLE,
                configureFeatures.getOrThrow(ICE_CAVE_ORE_IRON),
                oreIronMediumModifiers
        );
        PlacementUtils.register(context,
                ICE_CAVE_ORE_IRON_SMALL.placedFeature(),
                configureFeatures.getOrThrow(ICE_CAVE_ORE_IRON_SMALL.configuredFeature()),
                oreIronSmallModifiers
        );
//        PlacementUtils.register(context,
//                TEST.placedFeature(),
//                configureFeatures.getOrThrow(TEST.configuredFeature()),
//                CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }

    private static List<PlacementModifier> orePlacement(final PlacementModifier frequencyModifier, final PlacementModifier heightRange) {
        return List.of(frequencyModifier, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(final int count, final PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }

    private static List<PlacementModifier> rareOrePlacement(final int rarity, final PlacementModifier heightRange) {
        return orePlacement(RarityFilter.onAverageOnceEvery(rarity), heightRange);
    }

    private static SequenceConfiguration hostedOre(final RuleTest target, BlockState state, final Holder<ConfiguredFeature<? , ?>> oreFeature) {
        return new SequenceConfiguration(HolderSet.direct(PlacementUtils.inlinePlaced(Holder.direct(oreFeatureBuilder(target, state, 20))), PlacementUtils.inlinePlaced(oreFeature)));
    }

    private static ConfiguredFeature<?, ?> oreFeatureBuilder(RuleTest target, BlockState state, int size) {
        return new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(target, state, size));
    }
}

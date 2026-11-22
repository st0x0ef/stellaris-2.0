package org.exodusstudio.stellaris.common.world.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.exodusstudio.stellaris.common.world.features.utils.SpeleothemUtils;

public record SpeleothemClusterConfiguration(
        BlockState baseBlock,
        BlockState pointedBlock,
        HolderSet<Block> replaceableBlocks,
        int floorToCeilingSearchRange,
        IntProvider height,
        IntProvider radius,
        int maxStalagmiteStalactiteHeightDiff,
        int heightDeviation,
        IntProvider speleothemBlockLayerThickness,
        FloatProvider density,
        FloatProvider wetness,
        float chanceOfSpeleothemAtMaxDistanceFromCenter,
        int maxDistanceFromEdgeAffectingChanceOfSpeleothem,
        int maxDistanceFromCenterAffectingHeightBias,
        SpeleothemClusterConfiguration.PlacementOptions placementOptions
) implements FeatureConfiguration {
    public static final Codec<SpeleothemClusterConfiguration> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                            BlockState.CODEC.fieldOf("base_block").forGetter(SpeleothemClusterConfiguration::baseBlock),
                            BlockState.CODEC.fieldOf("pointed_block").forGetter(SpeleothemClusterConfiguration::pointedBlock),
                            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable_blocks").forGetter(SpeleothemClusterConfiguration::replaceableBlocks),
                            Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").forGetter(SpeleothemClusterConfiguration::floorToCeilingSearchRange),
                            IntProviders.codec(1, 128).fieldOf("height").forGetter(SpeleothemClusterConfiguration::height),
                            IntProviders.codec(1, 128).fieldOf("radius").forGetter(SpeleothemClusterConfiguration::radius),
                            Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter(SpeleothemClusterConfiguration::maxStalagmiteStalactiteHeightDiff),
                            Codec.intRange(1, 64).fieldOf("height_deviation").forGetter(SpeleothemClusterConfiguration::heightDeviation),
                            IntProviders.codec(0, 128).fieldOf("speleothem_block_layer_thickness").forGetter(SpeleothemClusterConfiguration::speleothemBlockLayerThickness),
                            FloatProviders.codec(0.0F, 2.0F).fieldOf("density").forGetter(SpeleothemClusterConfiguration::density),
                            FloatProviders.codec(0.0F, 2.0F).fieldOf("wetness").forGetter(SpeleothemClusterConfiguration::wetness),
                            Codec.floatRange(0.0F, 1.0F)
                                    .fieldOf("chance_of_speleothem_at_max_distance_from_center")
                                    .forGetter(SpeleothemClusterConfiguration::chanceOfSpeleothemAtMaxDistanceFromCenter),
                            Codec.intRange(1, 64)
                                    .fieldOf("max_distance_from_edge_affecting_chance_of_speleothem")
                                    .forGetter(SpeleothemClusterConfiguration::maxDistanceFromEdgeAffectingChanceOfSpeleothem),
                            Codec.intRange(1, 64)
                                    .fieldOf("max_distance_from_center_affecting_height_bias")
                                    .forGetter(SpeleothemClusterConfiguration::maxDistanceFromCenterAffectingHeightBias),
                            SpeleothemClusterConfiguration.PlacementOptions.CODEC
                                    .optionalFieldOf("placement_options", SpeleothemClusterConfiguration.PlacementOptions.DEFAULT)
                                    .forGetter(SpeleothemClusterConfiguration::placementOptions)
                    )
                    .apply(i, SpeleothemClusterConfiguration::new));

    public SpeleothemClusterConfiguration(
            final BlockState baseBlock,
            final BlockState pointedBlock,
            final HolderSet<Block> replaceableBlocks,
            final int floorToCeilingSearchRange,
            final IntProvider height,
            final IntProvider radius,
            final int maxStalagmiteStalactiteHeightDiff,
            final int heightDeviation,
            final IntProvider speleothemBlockLayerThickness,
            final FloatProvider density,
            final FloatProvider wetness,
            final float chanceOfSpeleothemAtMaxDistanceFromCenter,
            final int maxDistanceFromEdgeAffectingChanceOfSpeleothem,
            final int maxDistanceFromCenterAffectingHeightBias
    ) {
        this(
                baseBlock,
                pointedBlock,
                replaceableBlocks,
                floorToCeilingSearchRange,
                height,
                radius,
                maxStalagmiteStalactiteHeightDiff,
                heightDeviation,
                speleothemBlockLayerThickness,
                density,
                wetness,
                chanceOfSpeleothemAtMaxDistanceFromCenter,
                maxDistanceFromEdgeAffectingChanceOfSpeleothem,
                maxDistanceFromCenterAffectingHeightBias,
                SpeleothemClusterConfiguration.PlacementOptions.DEFAULT
        );
    }

    public record PlacementOptions(SpeleothemClusterConfiguration.PlacementMode placementMode, SpeleothemUtils.BaseBlockTransformer baseBlockTransformer, boolean allowWaterPlacement) {
        public static final SpeleothemClusterConfiguration.PlacementOptions DEFAULT = new SpeleothemClusterConfiguration.PlacementOptions(
                SpeleothemClusterConfiguration.PlacementMode.FLOOR_AND_CEILING, SpeleothemUtils.BaseBlockTransformer.NONE, true
        );
        public static final Codec<SpeleothemClusterConfiguration.PlacementOptions> CODEC = RecordCodecBuilder.create(
                i -> i.group(
                                SpeleothemClusterConfiguration.PlacementMode.CODEC.fieldOf("placement_mode").forGetter(SpeleothemClusterConfiguration.PlacementOptions::placementMode),
                                SpeleothemUtils.BaseBlockTransformer.CODEC.fieldOf("base_block_transformer").forGetter(SpeleothemClusterConfiguration.PlacementOptions::baseBlockTransformer),
                                Codec.BOOL.fieldOf("allow_water_placement").forGetter(SpeleothemClusterConfiguration.PlacementOptions::allowWaterPlacement)
                        )
                        .apply(i, SpeleothemClusterConfiguration.PlacementOptions::new)
        );
    }

    public enum PlacementMode implements StringRepresentable {
        FLOOR_AND_CEILING("floor_and_ceiling"),
        FLOOR_ONLY("floor_only"),
        CEILING_ONLY("ceiling_only");

        public static final Codec<SpeleothemClusterConfiguration.PlacementMode> CODEC = StringRepresentable.fromEnum(SpeleothemClusterConfiguration.PlacementMode::values);
        private final String name;

        PlacementMode(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }


}

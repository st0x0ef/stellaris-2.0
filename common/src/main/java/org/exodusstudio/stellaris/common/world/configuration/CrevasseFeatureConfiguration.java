package org.exodusstudio.stellaris.common.world.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record CrevasseFeatureConfiguration(
        int maxWidth,
        int maxLength,
        int minFootprint,
        int cursorSurfaceSearchRange,
        int columnSurfaceSearchRange,
        float twistiness,
        float tapering,
        float turnFactor,
        IntProvider carveDepth,
        IntProvider fillDepth,
        BlockStateProvider surfaceBlock,
        BlockStateProvider fillBlock,
        BlockPredicate carvableBlocks
//        BlockPredicate acceptableBarrier
) implements FeatureConfiguration {


    public static final Codec<CrevasseFeatureConfiguration> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                            ExtraCodecs.intRange(2, 8).fieldOf("max_width").forGetter(CrevasseFeatureConfiguration::maxWidth),
                            ExtraCodecs.intRange(4, 16).fieldOf("max_length").forGetter(CrevasseFeatureConfiguration::maxLength),
                            ExtraCodecs.intRange(1, 32).fieldOf("min_footprint").forGetter(CrevasseFeatureConfiguration::minFootprint),
                            ExtraCodecs.POSITIVE_INT.fieldOf("cursor_surface_search_range").forGetter(CrevasseFeatureConfiguration::cursorSurfaceSearchRange),
                            ExtraCodecs.POSITIVE_INT.fieldOf("column_surface_search_range").forGetter(CrevasseFeatureConfiguration::columnSurfaceSearchRange),
                            ExtraCodecs.floatRange(0.0F, 16.0F).fieldOf("twistiness").forGetter(CrevasseFeatureConfiguration::twistiness),
                            ExtraCodecs.floatRange(0.0F, 1.0F).fieldOf("tapering").forGetter(CrevasseFeatureConfiguration::tapering),
                            ExtraCodecs.floatRange(0.0F, 1.0F).fieldOf("turn_factor").forGetter(CrevasseFeatureConfiguration::turnFactor),
                            IntProviders.POSITIVE_CODEC.fieldOf("carve_depth").forGetter(CrevasseFeatureConfiguration::carveDepth),
                            IntProviders.POSITIVE_CODEC.fieldOf("fill_depth").forGetter(CrevasseFeatureConfiguration::fillDepth),
                            BlockStateProvider.CODEC.fieldOf("surface_block").forGetter(CrevasseFeatureConfiguration::surfaceBlock),
                            BlockStateProvider.CODEC.fieldOf("fill_block").forGetter(CrevasseFeatureConfiguration::fillBlock),
                            BlockPredicate.CODEC.fieldOf("carvable_blocks").forGetter(CrevasseFeatureConfiguration::carvableBlocks)
//                            BlockPredicate.CODEC.fieldOf("acceptable_barrier").forGetter(CrevasseFeatureConfiguration::acceptableBarrier)
                    )
                    .apply(i, CrevasseFeatureConfiguration::new)
    );

}

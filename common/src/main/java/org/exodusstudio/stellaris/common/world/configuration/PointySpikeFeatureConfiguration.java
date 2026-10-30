package org.exodusstudio.stellaris.common.world.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record PointySpikeFeatureConfiguration(BlockState blockState, IntProvider spread, IntProvider height, IntProvider baseRadius, IntProvider branchCount) implements FeatureConfiguration {
    private static final int minNumberOfSpikes = 1;

    public static final Codec<PointySpikeFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("block_state").forGetter(PointySpikeFeatureConfiguration::blockState),
                    IntProviders.CODEC.fieldOf("spread").forGetter(PointySpikeFeatureConfiguration::spread),
                    IntProviders.CODEC.fieldOf("height").forGetter(PointySpikeFeatureConfiguration::height),
                    IntProviders.CODEC.fieldOf("radius").forGetter(PointySpikeFeatureConfiguration::baseRadius),
                    IntProviders.CODEC.fieldOf("branch_count").validate(u -> u.minInclusive() < minNumberOfSpikes ? DataResult.error(() -> "Max must be at least min, min_inclusive: " + minNumberOfSpikes) : DataResult.success(u)).forGetter(PointySpikeFeatureConfiguration::branchCount)
            ).apply(instance, PointySpikeFeatureConfiguration::new)
    );
}

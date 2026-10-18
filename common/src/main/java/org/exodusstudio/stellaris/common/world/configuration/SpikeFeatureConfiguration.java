package org.exodusstudio.stellaris.common.world.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record SpikeFeatureConfiguration(BlockState blockState, IntProvider spread, IntProvider height,  IntProvider baseRadius, IntProvider baseAngle, IntProvider branchCount) implements FeatureConfiguration {
    private static final int minNumberOfSpikes = 1;

    public static final Codec<SpikeFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("block_state").forGetter(SpikeFeatureConfiguration::blockState),
                    IntProviders.CODEC.fieldOf("spread").forGetter(SpikeFeatureConfiguration::spread),
                    IntProviders.CODEC.fieldOf("height").forGetter(SpikeFeatureConfiguration::height),
                    IntProviders.CODEC.fieldOf("radius").forGetter(SpikeFeatureConfiguration::baseRadius),
                    IntProviders.CODEC.fieldOf("angle").validate(u -> u.minInclusive() < 0 ? DataResult.error(() -> "Max must be at least min, min_inclusive: " + 0) : DataResult.success(u)).forGetter(SpikeFeatureConfiguration::baseAngle),
                    IntProviders.CODEC.fieldOf("branch_count").validate(u -> u.minInclusive() < minNumberOfSpikes ? DataResult.error(() -> "Max must be at least min, min_inclusive: " + minNumberOfSpikes) : DataResult.success(u)).forGetter(SpikeFeatureConfiguration::branchCount)
            ).apply(instance, SpikeFeatureConfiguration::new)
    );
}

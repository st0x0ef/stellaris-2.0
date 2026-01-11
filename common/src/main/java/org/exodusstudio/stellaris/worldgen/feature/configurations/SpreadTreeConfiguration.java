package org.exodusstudio.stellaris.worldgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

import java.util.List;

public class SpreadTreeConfiguration extends StellarisTreeConfiguration{

    public static final Codec<SpreadTreeConfiguration> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter((instance) -> instance.trunkProvider),
            BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter((instance) -> instance.foliageProvider),
            BlockStateProvider.CODEC.fieldOf("vine_provider").forGetter((instance) -> instance.vineProvider),
            BlockStateProvider.CODEC.fieldOf("hanging_provider").forGetter((instance) -> instance.hangingProvider),
            BlockStateProvider.CODEC.fieldOf("trunk_fruit_provider").forGetter((instance) -> instance.trunkFruitProvider),
            BlockStateProvider.CODEC.fieldOf("alt_foliage_provider").forGetter((instance) -> instance.altFoliageProvider),
            Codec.INT.fieldOf("min_height").forGetter((instance) -> instance.minHeight),
            Codec.INT.fieldOf("max_height").forGetter((instance) -> instance.maxHeight),
            TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter(instance -> instance.decorators),
            Codec.INT.fieldOf("trunk_width").forGetter(instance -> instance.trunkWidth)
    ).apply(inst, SpreadTreeConfiguration::new));

    public final int trunkWidth;

    protected SpreadTreeConfiguration(BlockStateProvider trunkProvider, BlockStateProvider foliageProvider, BlockStateProvider vineProvider, BlockStateProvider hangingProvider, BlockStateProvider trunkFruitProvider, BlockStateProvider altFoliageProvider, int minHeight, int maxHeight, List<TreeDecorator> decorators, int trunkWidth) {
        super(trunkProvider, foliageProvider, vineProvider, hangingProvider, trunkFruitProvider, altFoliageProvider, minHeight, maxHeight, decorators);
        this.trunkWidth = trunkWidth;
    }

    public static class Builder extends StellarisTreeConfiguration.Builder<Builder> {
        private int trunkWidth;

        public Builder() {
            this.minHeight = 6;
            this.maxHeight = 16;
            this.trunkProvider = BlockStateProvider.simple(Blocks.SPRUCE_LOG.defaultBlockState());
            this.foliageProvider = BlockStateProvider.simple(Blocks.SPRUCE_LEAVES.defaultBlockState());
            this.vineProvider = BlockStateProvider.simple(Blocks.VINE.defaultBlockState());
            this.trunkWidth = 3;
        }

        public Builder trunkWidth(int a)
        {
            this.trunkWidth = a;
            return this;
        }

        public SpreadTreeConfiguration build() {
            return new SpreadTreeConfiguration(this.trunkProvider, this.foliageProvider, this.vineProvider, this.hangingProvider, this.trunkFruitProvider, this.altFoliageProvider, this.minHeight, this.maxHeight, this.decorators,this.trunkWidth);
        }
    }
}

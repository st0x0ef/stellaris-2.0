package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

public class LunarVinesBlock extends GrowingPlantHeadBlock {
    public static final MapCodec<LunarVinesBlock> CODEC = simpleCodec(LunarVinesBlock::new);
    private static final VoxelShape SHAPE = Block.column((double)8.0F, (double)0.0F, (double)15.0F);

    public MapCodec<LunarVinesBlock> codec() {
        return CODEC;
    }

    public LunarVinesBlock(BlockBehaviour.Properties properties) {
        super(properties, Direction.UP, SHAPE, false, 0.1);
    }

    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    protected Block getBodyBlock() {
        return BlocksRegistry.LUNAR_VINES_PLANT.get();
    }

    protected boolean canGrowInto(BlockState state) {
        return NetherVines.isValidGrowthState(state);
    }
}

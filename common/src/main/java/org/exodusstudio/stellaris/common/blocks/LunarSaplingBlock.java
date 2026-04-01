package org.exodusstudio.stellaris.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

public class LunarSaplingBlock extends SaplingBlock {
    public LunarSaplingBlock(TreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlocksRegistry.MOON_SAND.block().get()) || state.is(BlocksRegistry.MOON_ROCK.block().get()) || super.mayPlaceOn(state, level, pos);
    }
}


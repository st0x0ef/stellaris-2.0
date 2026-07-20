package org.exodusstudio.stellaris.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public interface MultiblockProxyBlock {

    BlockPos getControllerPos(BlockPos proxyPos, BlockState proxyState);
}

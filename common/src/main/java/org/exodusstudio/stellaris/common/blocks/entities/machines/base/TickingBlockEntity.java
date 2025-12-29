package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface TickingBlockEntity {
    void tick(Level level, BlockState state);
}

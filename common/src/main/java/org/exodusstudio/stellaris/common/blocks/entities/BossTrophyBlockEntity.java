package org.exodusstudio.stellaris.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class BossTrophyBlockEntity extends BlockEntity {

    public BossTrophyBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.BOSS_TROPHY.get(), pos, state);
    }
}

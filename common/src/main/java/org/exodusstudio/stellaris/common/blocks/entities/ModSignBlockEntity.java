package org.exodusstudio.stellaris.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class ModSignBlockEntity extends SignBlockEntity {

    public ModSignBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.MOD_SIGN.get(), pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return BlockEntitiesRegistry.MOD_SIGN.get();
    }
}

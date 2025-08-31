package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.CableBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyUtil;

public class CableBlockEntity extends BaseEnergyBlockEntity {
    public CableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.CABLES.get(), blockPos, blockState, blockState.getBlock() instanceof CableBlock block ? block.transferRate : 0);
    }

    public CableBlockEntity(BlockPos blockPos, BlockState blockState, int transferRate) {
        super(BlockEntitiesRegistry.CABLES.get(), blockPos, blockState, transferRate);
    }

    public static CableBlockEntity create(BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CableBlock block) {
            return new CableBlockEntity(pos, state, block.transferRate);
        }
        return new CableBlockEntity(pos, state, 0);
    }

    @Override
    public void tick() {
        EnergyUtil.distributeEnergyNearby(level, worldPosition, energyContainer.getEnergy());
    }
}

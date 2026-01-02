package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.CableBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyUtil;

public class CableBlockEntity extends BaseEnergyBlockEntity {
    public CableBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(blockPos, blockState, blockState.getBlock() instanceof CableBlock block ? block.transferRate : 0);
    }

    public CableBlockEntity(BlockPos blockPos, BlockState blockState, int transferRate) {
        super(BlockEntitiesRegistry.CABLES.get(), blockPos, blockState, transferRate);
    }

    @Override
    public void tick(Level level, BlockState state) {
        EnergyUtil.distributeEnergyNearby(level, worldPosition, energyContainer.getEnergy());
    }
}

package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.base.BaseLitMachineBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class OxygenPropagatorBlockEntity extends BaseEnergyBlockEntity {
    public OxygenPropagatorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.OXYGEN_PROPAGATOR.get(), blockPos, blockState, 12800);
    }

    @Override
    public void tick(Level level, BlockState state) {
        if (energyContainer.getEnergy() > 0) {
            state.setValue(BaseLitMachineBlock.LIT, true);
        } else {
            state.setValue(BaseLitMachineBlock.LIT, false);
        }
    }
}

package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class GravityManipulatorBlockEntity extends BaseEnergyContainerBlockEntity {
    public boolean isActive = false;
    public double gravity = 9.81;

    public GravityManipulatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), pos, state, 3000);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("item.stellaris.gravity_manipulator");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            this.isActive = this.energyContainer.getEnergy() > 0;

            if  (this.isActive) {
                this.energyContainer.extract(1, false); // TODO : adjust energy consumption based on new gravity
            }
        }
    }

    public double getDifferenceGravity(double targetGravity) {
        return this.gravity - targetGravity;
    }
}

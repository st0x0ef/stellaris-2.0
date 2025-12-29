package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.PowerBankBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.menus.PowerBankMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyUtil;
import org.jetbrains.annotations.NotNull;

public class PowerBankBlockEntity extends BaseEnergyContainerBlockEntity {
    private int renderStage = -1; // -1 to force update on first tick

    public PowerBankBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((PowerBankBlock)state.getBlock()).tier);
    }

    public PowerBankBlockEntity(BlockPos pos, BlockState state, int tier) {
        super(BlockEntitiesRegistry.POWER_BANKS.get(), pos, state, (int) Math.pow(2,4*tier)*1000);
    }

    @Override
    public void tick(Level level, BlockState blockState) {
        int initialRenderStage = renderStage;

        //First - Insert slot
        if (!items.getFirst().isEmpty())
            EnergyUtil.moveEnergyFromItem(energyContainer, items.getFirst(), energyContainer.getMaxEnergy() / 40);
        //Last - Extract slot
        if (!items.getLast().isEmpty())
            EnergyUtil.moveEnergyToItem(energyContainer, items.getLast(), energyContainer.getMaxEnergy() / 40);

        EnergyUtil.distributeEnergyNearby(level, worldPosition, energyContainer.getMaxEnergy() / 20);

        //Update render stage
        renderStage = (energyContainer.getEnergy() * 9) / energyContainer.getMaxEnergy();

        if (initialRenderStage != renderStage && level != null) {
            BlockState state = getBlockState().setValue(PowerBankBlock.STAGE, renderStage);
            level.setBlock(getBlockPos(), state, 3);
            setChanged();
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("item.stellaris.power_bank_t" + ((PowerBankBlock)getBlockState().getBlock()).tier);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new PowerBankMenu(containerId, inventory, this, this);
    }
}
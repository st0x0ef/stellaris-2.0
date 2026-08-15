package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.SkyPanelBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.menus.SkyPanelMenu;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyUtil;

public class SkyPanelBlockEntity extends BaseGeneratorBlockEntity {

    public SkyPanelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(panelBlock(blockState).getBlockEntityType(), blockPos, blockState, 1, 12800);
    }

    private static SkyPanelBlock panelBlock(BlockState blockState) {
        return (SkyPanelBlock) blockState.getBlock();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new SkyPanelMenu(containerId, inventory, this, this);
    }

    @Override
    public boolean canGenerate() {
        if (level == null) {
            return false;
        }

        BlockPos blockPos = this.getBlockPos().offset(0, 1, 0);
        return panelBlock(getBlockState()).type.canGenerate(level) && level.canSeeSky(blockPos);
    }

    @Override
    public void tick(Level level, BlockState state) {
        super.tick(level, state);
        EnergyUtil.moveEnergyToItem(getEnergy(null), items.getFirst(), 10);
    }

    protected Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }
}

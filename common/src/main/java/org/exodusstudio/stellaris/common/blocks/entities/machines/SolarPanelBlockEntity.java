package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.menus.SolarPanelMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyUtil;

public class SolarPanelBlockEntity extends BaseGeneratorBlockEntity {

    public SolarPanelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.SOLAR_PANEL.get(), blockPos, blockState, 1, 12800);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new SolarPanelMenu(containerId, inventory, this, this);
    }

    @Override
    public boolean canGenerate() {
        if (level == null) {
            return false;
        }

        BlockPos blockPos = this.getBlockPos().offset(0, 1, 0);
        return level.isBrightOutside() && level.canSeeSky(blockPos);
    }

    @Override
    public void tick(Level level, BlockState state) {
        super.tick(level, state);
        EnergyUtil.moveEnergyToItem(getEnergy(null), items.getFirst(), 10);
    }

    protected Component getDefaultName() {
        return Component.translatable("item.stellaris.solar_panel");
    }

    @Override
    public int getContainerSize() {
        return 1;
    }
}

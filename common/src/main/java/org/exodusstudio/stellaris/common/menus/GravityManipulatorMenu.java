package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.EnergyUsageMenu;
import org.exodusstudio.stellaris.common.menus.base.EnergyUsageSync;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class GravityManipulatorMenu extends AbstractContainerMenu implements EnergyUsageMenu {

    private final GravityManipulatorBlockEntity entity;
    private final EnergyUsageSync energyUsage;


    public static GravityManipulatorMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        GravityManipulatorBlockEntity entity = (GravityManipulatorBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());

        return new GravityManipulatorMenu(syncId, inventory, entity);
    }

    public GravityManipulatorMenu(int syncId, Inventory inventory, GravityManipulatorBlockEntity entity) {
        super(MenuTypesRegistry.GRAVITY_MANIPULATOR.get(), syncId);

        this.entity = entity;
        this.energyUsage = EnergyUsageSync.create(this.entity, inventory);
    }

    public GravityManipulatorBlockEntity getBlockEntity() {
        return entity;
    }

    @Override
    public EnergyUsageSync getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        energyUsage.broadcastChanges(containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

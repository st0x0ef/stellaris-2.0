package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectricLightBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.EnergyUsageMenu;
import org.exodusstudio.stellaris.common.menus.base.EnergyUsageSync;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class ElectricLightMenu extends AbstractContainerMenu implements EnergyUsageMenu {

    private final ElectricLightBlockEntity entity;
    private final EnergyUsageSync energyUsage;

    public static ElectricLightMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        ElectricLightBlockEntity entity = (ElectricLightBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());

        return new ElectricLightMenu(syncId, inventory, entity);
    }

    public ElectricLightMenu(int syncId, Inventory inventory, ElectricLightBlockEntity entity) {
        super(MenuTypesRegistry.ELECTRIC_LIGHT.get(), syncId);

        this.entity = entity;
        this.energyUsage = EnergyUsageSync.create(this.entity, inventory);
    }

    public ElectricLightBlockEntity getBlockEntity() {
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

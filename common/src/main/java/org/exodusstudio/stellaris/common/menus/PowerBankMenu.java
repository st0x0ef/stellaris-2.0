package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.PowerBankBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.EnergySlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class PowerBankMenu extends BaseContainer {

    private final Container inventory;
    private final PowerBankBlockEntity blockEntity;

    public static PowerBankMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        return new PowerBankMenu(syncId, inventory, new SimpleContainer(2), (PowerBankBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public PowerBankMenu(int syncId, Inventory playerInventory, Container container, PowerBankBlockEntity entity) {
        super(MenuTypesRegistry.POWER_BANK.get(), syncId, 2, playerInventory, 10, 106);

        checkContainerSize(container, 2);
        this.inventory = container;
        this.blockEntity = entity;

        addSlot(new EnergySlot(inventory, 0, 64, 56)); // INSERT
        addSlot(new EnergySlot(inventory, 1, 100, 56)); // EXTRACT

    }

    public PowerBankBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }
}

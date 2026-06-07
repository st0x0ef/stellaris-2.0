package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class RocketMenu extends AbstractContainerMenu {

    private final Container inventory;
    private final RocketEntity rocket;
    private final int inventoryRows;

    public static RocketMenu create(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        RocketEntity entity = (RocketEntity) inventory.player.level().getEntity(buffer.readUUID());
        if (entity != null) {
            int rows = entity.getInventoryRows();
            return new RocketMenu(syncId, inventory, new SimpleContainer(2 + 9 * rows), entity, rows);
        }

        return new RocketMenu(syncId, inventory, new SimpleContainer(11), null, 1); // default rocket, need that to fix a crash
    }

    public RocketMenu(int syncId, Inventory playerInventory, Container container, RocketEntity rocket, int inventoryRows) {
        super(MenuTypesRegistry.ROCKET_MENU.get(), syncId);

        this.rocket = rocket;
        this.inventoryRows = inventoryRows;

        checkContainerSize(container, 2 + 9 * inventoryRows);

        this.inventory = container;
        addSlots(inventory, inventoryRows);

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        return MenuQuickMoveHelper.quickMoveMachineFirst(this, player, invSlot, 2 + 9 * inventoryRows);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    private void addSlots(Container inventory, float inventoryRows) {
        // fuel slots
        this.addSlot(new Slot(inventory, 0, 68, 18));
        this.addSlot(new Slot(inventory, 1, 68, 52));

        // inventory slots
        for (int i = 0; i < inventoryRows; i++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(inventory, l + i * 9 + 2, 10 + l * 18, 77 + i * 18));
            }
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 10 + l * 18, 142 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 10 + i * 18, 200));
        }
    }

    public RocketEntity getRocket() {
        return rocket;
    }

}

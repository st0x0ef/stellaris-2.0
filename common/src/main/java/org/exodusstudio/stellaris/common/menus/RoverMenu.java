package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.menus.slot.VehicleFuelSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class RoverMenu extends AbstractContainerMenu implements IVehicleMenu {

    private final Container inventory;
    private final RoverEntity rover;
    private final int inventoryRows;

    public static RoverMenu create(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        int entityId = buffer.readVarInt();
        RoverEntity entity = (RoverEntity) inventory.player.level().getEntity(entityId);
        int rows = entity != null ? entity.getInventoryRows() : 1;
        return new RoverMenu(syncId, inventory, new SimpleContainer(2 + 9 * rows), entityId, rows);
    }

    public RoverMenu(int syncId, Inventory playerInventory, Container container, int entityId, int inventoryRows) {
        super(MenuTypesRegistry.ROVER_MENU.get(), syncId);

        this.rover = (RoverEntity) playerInventory.player.level().getEntity(entityId);
        this.inventoryRows = inventoryRows;
        checkContainerSize(container, 2 + 9 * inventoryRows);
        this.inventory = container;

        addSlots(container, inventoryRows);

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

    private void addSlots(Container inventory, int inventoryRows) {
        // fuel slots
        this.addSlot(new VehicleFuelSlot(inventory, 0, 68, 18));
        this.addSlot(new ResultSlot(inventory, 1, 68, 52));

        // cargo slots
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

    public RoverEntity getRover() {
        return rover;
    }

    @Override
    public int getFuel() {
        return getRover().getFuel();
    }
}

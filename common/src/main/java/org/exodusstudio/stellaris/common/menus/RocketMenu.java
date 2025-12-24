package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.menus.slot.RocketModuleSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class RocketMenu extends AbstractContainerMenu {

    private final Container inventory;
    private final RocketEntity rocket;

    public static RocketMenu create(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        return new RocketMenu(syncId, inventory, new SimpleContainer(15), (RocketEntity) inventory.player.level().getEntity(buffer.readInt()));
    }

    public RocketMenu(int syncId, Inventory playerInventory, Container container, RocketEntity rocket) {
        super(MenuTypesRegistry.ROCKET_MENU.get(), syncId);

        this.rocket = rocket;
        checkContainerSize(container, 14);
        this.inventory = container;
        addSlots(inventory);

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }
        }

        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {

        return this.inventory.stillValid(player);
    }

    private void addSlots(Container inventory) {
        //FUEL SLOTS
        this.addSlot(new Slot(inventory, 0, 20, 28));
        this.addSlot(new Slot(inventory, 1, 20, 62));

        //UPGRADE SLOTS
        this.addSlot(new RocketModuleSlot(inventory, 2, 82, 74));
        this.addSlot(new RocketModuleSlot(inventory, 3, 100, 74));
        this.addSlot(new RocketModuleSlot(inventory, 4, 118, 74));
        this.addSlot(new RocketModuleSlot(inventory, 5, 136, 74));

        //INVENTORY SLOTS
        this.addSlot(new Slot(inventory, 6, 82, 28));
        this.addSlot(new Slot(inventory, 7, 82, 46));

        this.addSlot(new Slot(inventory, 8, 100, 28));
        this.addSlot(new Slot(inventory, 9, 100, 46));

        this.addSlot(new Slot(inventory, 10, 118, 28));
        this.addSlot(new Slot(inventory, 11, 118, 46));

        this.addSlot(new Slot(inventory, 12, 136, 28));
        this.addSlot(new Slot(inventory, 13, 136, 46));

    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 10 + l * 18, (95 + i * 18) + 11));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 10 + i * 18, 164));
        }
    }

    public RocketEntity getRocket() {
        return rocket;
    }

}
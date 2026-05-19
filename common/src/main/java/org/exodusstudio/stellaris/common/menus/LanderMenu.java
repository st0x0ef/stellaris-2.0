package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.exodusstudio.stellaris.common.menus.MenuQuickMoveHelper;

public class LanderMenu extends AbstractContainerMenu {

    private final Container inventory;

    public static LanderMenu create(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        return new LanderMenu(syncId, inventory, new SimpleContainer(11));
    }

    public LanderMenu(int syncId, Inventory playerInventory, Container container) {
        super(MenuTypesRegistry.LANDER_MENU.get(), syncId);

        checkContainerSize(container, 11);
        this.inventory = container;
        addSlots(inventory);

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        return MenuQuickMoveHelper.quickMoveMachineFirst(this, player, invSlot, this.inventory.getContainerSize());
    }

    @Override
    public boolean stillValid(Player player) {

        return this.inventory.stillValid(player);
    }

    private void addSlots(Container inventory) {
        //ROCKET SLOT
        this.addSlot(new ResultSlot(inventory, 0, 38, 37));

        //FUEL SLOTS
        this.addSlot(new ResultSlot(inventory, 1, 24, 68));
        this.addSlot(new ResultSlot(inventory, 2, 52, 68));

        //INVENTORY SLOTS
        this.addSlot(new ResultSlot(inventory, 3, 82, 28));
        this.addSlot(new ResultSlot(inventory, 4, 82, 46));

        this.addSlot(new ResultSlot(inventory, 5, 100, 28));
        this.addSlot(new ResultSlot(inventory, 6, 100, 46));

        this.addSlot(new ResultSlot(inventory, 7, 118, 28));
        this.addSlot(new ResultSlot(inventory, 8, 118, 46));

        this.addSlot(new ResultSlot(inventory, 9, 136, 28));
        this.addSlot(new ResultSlot(inventory, 10, 136, 46));

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
}
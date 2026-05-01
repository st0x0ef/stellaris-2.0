package org.exodusstudio.stellaris.common.menus.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.menus.MenuQuickMoveHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseContainer extends AbstractContainerMenu {

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int PLAYER_TOTAL_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    private final int menuSlotCount;

    protected BaseContainer(@Nullable MenuType<?> menuType, int containerId, int size, Inventory inventory, int inventoryXOffset, int inventoryYOffset) {
        super(menuType, containerId);
        this.menuSlotCount = size;

        addPlayerHotbar(inventory, inventoryXOffset, inventoryYOffset + 58);
        addPlayerInventory(inventory, inventoryXOffset, inventoryYOffset);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return MenuQuickMoveHelper.quickMovePlayerFirst(this, player, index);
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    public int getPlayerSlotCount() {
        return PLAYER_TOTAL_SLOT_COUNT;
    }

    public int getMenuSlotCount() {
        return menuSlotCount;
    }

    public int getMenuSlotStart() {
        return PLAYER_TOTAL_SLOT_COUNT;
    }

    public int getMenuSlotEnd() {
        return PLAYER_TOTAL_SLOT_COUNT + menuSlotCount;
    }

    public void addPlayerHotbar(Inventory playerInventory, int xOffset, int yOffset) {
        for (int j = 0; j < HOTBAR_SLOT_COUNT; ++j) {
            this.addSlot(new Slot(playerInventory, j, xOffset + j * 18, yOffset));
        }
    }

    public void addPlayerInventory(Inventory playerInventory, int xOffset, int yOffset) {
        for (int row = 0; row < PLAYER_INVENTORY_ROW_COUNT; ++row) {
            for (int column = 0; column < PLAYER_INVENTORY_COLUMN_COUNT; ++column) {
                this.addSlot(new Slot(
                        playerInventory,
                        column + row * PLAYER_INVENTORY_COLUMN_COUNT + HOTBAR_SLOT_COUNT,
                        xOffset + column * 18,
                        yOffset + row * 18
                ));
            }
        }
    }
}
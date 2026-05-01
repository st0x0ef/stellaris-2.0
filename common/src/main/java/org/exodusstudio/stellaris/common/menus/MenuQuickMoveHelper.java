package org.exodusstudio.stellaris.common.menus;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class MenuQuickMoveHelper {
    public static final int PLAYER_SLOT_COUNT = 36;

    private MenuQuickMoveHelper() {
    }

    /**
     * For menus where BaseContainer adds the 36 player slots first 
     * then the other slots after that
     * Made by fishguy!
     * layout:
     * 0 - 35 = player inventory and hotbar
     * 36+ = menu slots
     */
    public static @NotNull ItemStack quickMovePlayerFirst(AbstractContainerMenu menu, Player player, int index) {
        return quickMove(menu, player, index, 0, PLAYER_SLOT_COUNT, PLAYER_SLOT_COUNT, menu.slots.size());
    }

    /**
     * For menus where other slots are added first
     * then player inventory after
     * Made by fishguy!
     * layout:
     * 0 - machineSlotCount - 1 = menu slots
     * machineSlotCount+ = player inventory and hotbar
     */
    public static @NotNull ItemStack quickMoveMachineFirst(AbstractContainerMenu menu, Player player, int index, int machineSlotCount) {
        return quickMove(menu, player, index, machineSlotCount, menu.slots.size(), 0, machineSlotCount);
    }

    public static @NotNull ItemStack quickMove(
            AbstractContainerMenu menu,
            Player player,
            int index,
            int playerStart,
            int playerEnd,
            int menuStart,
            int menuEnd
    ) {
        if (index < 0 || index >= menu.slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot sourceSlot = menu.slots.get(index);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack originalCopy = sourceStack.copy();

        boolean moved;

        if (index >= menuStart && index < menuEnd) {
            moved = moveItemStackToAcceptingSlotsOnly(menu, sourceStack, playerStart, playerEnd, true);
        } else if (index >= playerStart && index < playerEnd) {
            moved = moveItemStackToAcceptingSlotsOnly(menu, sourceStack, menuStart, menuEnd, false);
        } else {
            return ItemStack.EMPTY;
        }

        if (!moved) {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        if (sourceStack.getCount() == originalCopy.getCount()) {
            return ItemStack.EMPTY;
        }

        sourceSlot.onTake(player, sourceStack);
        return originalCopy;
    }

    private static boolean moveItemStackToAcceptingSlotsOnly(
            AbstractContainerMenu menu,
            ItemStack movingStack,
            int startIndex,
            int endIndex,
            boolean reverseDirection
    ) {
        boolean changed = false;

        if (movingStack.isEmpty()) {
            return false;
        }

        int index = reverseDirection ? endIndex - 1 : startIndex;

        if (movingStack.isStackable()) {
            while (!movingStack.isEmpty() && isInRange(index, startIndex, endIndex)) {
                Slot targetSlot = menu.slots.get(index);
                ItemStack targetStack = targetSlot.getItem();

                if (!targetStack.isEmpty()
                        && targetSlot.mayPlace(movingStack)
                        && ItemStack.isSameItemSameComponents(movingStack, targetStack)) {
                    int maxStackSize = Math.min(targetSlot.getMaxStackSize(), movingStack.getMaxStackSize());
                    int space = maxStackSize - targetStack.getCount();

                    if (space > 0) {
                        int movedAmount = Math.min(space, movingStack.getCount());
                        targetStack.grow(movedAmount);
                        movingStack.shrink(movedAmount);
                        targetSlot.setChanged();
                        changed = true;
                    }
                }

                index += reverseDirection ? -1 : 1;
            }
        }

        if (!movingStack.isEmpty()) {
            index = reverseDirection ? endIndex - 1 : startIndex;

            while (!movingStack.isEmpty() && isInRange(index, startIndex, endIndex)) {
                Slot targetSlot = menu.slots.get(index);
                ItemStack targetStack = targetSlot.getItem();

                if (targetStack.isEmpty() && targetSlot.mayPlace(movingStack)) {
                    int maxStackSize = Math.min(targetSlot.getMaxStackSize(), movingStack.getMaxStackSize());
                    int movedAmount = Math.min(maxStackSize, movingStack.getCount());

                    ItemStack placedStack = movingStack.copy();
                    placedStack.setCount(movedAmount);

                    targetSlot.set(placedStack);
                    targetSlot.setChanged();

                    movingStack.shrink(movedAmount);
                    changed = true;
                }

                index += reverseDirection ? -1 : 1;
            }
        }

        return changed;
    }

    private static boolean isInRange(int index, int startIndex, int endIndex) {
        return index >= startIndex && index < endIndex;
    }
}
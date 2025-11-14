package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SpecificItemsSlot extends Slot {
    private final ItemStack[] allowedItems;

    public SpecificItemsSlot(Container container, int slot, int x, int y, ItemStack... allowedItems) {
        super(container, slot, x, y);
        this.allowedItems = allowedItems;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        for (ItemStack allowedItem : allowedItems) {
            if (stack.is(allowedItem.getItem())) {
                return true;
            }
        }
        return false;
    }
}

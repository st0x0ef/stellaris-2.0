package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SpecificItemsSlot extends Slot {
    private final Item[] allowedItems;

    public SpecificItemsSlot(Container container, int slot, int x, int y, Item... allowedItems) {
        super(container, slot, x, y);
        this.allowedItems = allowedItems;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        for (Item allowedItem : allowedItems) {
            if (stack.is(allowedItem)) {
                return true;
            }
        }
        return false;
    }
}

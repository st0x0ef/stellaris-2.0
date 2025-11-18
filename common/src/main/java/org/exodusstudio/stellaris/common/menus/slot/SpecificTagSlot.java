package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SpecificTagSlot extends Slot {
    private final TagKey<Item>[] allowedTags;

    public SpecificTagSlot(Container container, int slot, int x, int y, TagKey<Item>... allowedTags) {
        super(container, slot, x, y);
        this.allowedTags = allowedTags;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        for (TagKey<Item> tag : allowedTags) {
            if (stack.is(tag)) {
                return true;
            }
        }
        return false;
    }
}

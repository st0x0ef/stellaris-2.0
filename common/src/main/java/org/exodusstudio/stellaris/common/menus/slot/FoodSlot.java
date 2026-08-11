package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

public class FoodSlot extends Slot {

    public FoodSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.has(DataComponents.FOOD) && !stack.is(TagsRegistry.ItemTags.CAN);
    }
}

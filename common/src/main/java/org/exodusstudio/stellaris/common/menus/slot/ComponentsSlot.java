package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ComponentsSlot extends Slot {

    private final DataComponentType<?>[] neededComponents;

    public ComponentsSlot(Container container, int slot, int x, int y, DataComponentType<?>... neededComponents) {
        super(container, slot, x, y);
        this.neededComponents = neededComponents;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        for (DataComponentType<?> component : neededComponents) {
            if (!stack.has(component)) {
                return false;
            }
        }

        return true;
    }
}

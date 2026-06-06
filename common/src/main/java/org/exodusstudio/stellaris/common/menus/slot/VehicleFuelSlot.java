package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.vehicle_upgrade.FuelType;

public class VehicleFuelSlot extends Slot {
    public VehicleFuelSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return FuelType.Type.getTypeBasedOnItem(stack.getItem()) != null;
    }
}

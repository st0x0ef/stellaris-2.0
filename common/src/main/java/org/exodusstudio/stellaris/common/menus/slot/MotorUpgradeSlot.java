package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.items.VehicleUpgradeItem;
import org.exodusstudio.stellaris.common.vehicle_upgrade.MotorUpgrade;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;

public class MotorUpgradeSlot extends Slot {
    private final RoverEntity rover;
    public MotorUpgradeSlot(Container container, int index, int x, int y, RoverEntity rover) {
        super(container, index, x, y);
        this.rover = rover;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof VehicleUpgradeItem item && item.getUpgrade() instanceof MotorUpgrade;
    }
}

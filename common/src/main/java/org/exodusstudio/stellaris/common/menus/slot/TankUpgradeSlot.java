package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.items.VehicleUpgradeItem;
import org.exodusstudio.stellaris.common.vehicle_upgrade.TankUpgrade;

public class TankUpgradeSlot extends Slot {
    public TankUpgradeSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof VehicleUpgradeItem item && item.getUpgrade() instanceof TankUpgrade;
    }
}

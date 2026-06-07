package org.exodusstudio.stellaris.common.items;

import org.exodusstudio.stellaris.common.vehicle_upgrade.VehicleUpgrade;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VehicleUpgradeItem extends Item {

    private final VehicleUpgrade upgrade;

    public VehicleUpgradeItem(Properties properties, VehicleUpgrade upgrade) {
        super(properties);
        this.upgrade = upgrade;
    }

    public VehicleUpgrade getUpgrade() {
        return upgrade;
    }


}

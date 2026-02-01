package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;

public class DieselTankModuleItem extends TankModuleItem {
    public DieselTankModuleItem(Properties properties, int dieselCapacity) {
        super(properties, dieselCapacity);
    }

    @Override
    public Fluid getFuel() {
        return FluidsRegistry.FLOWING_DIESEL.get();
    }
}

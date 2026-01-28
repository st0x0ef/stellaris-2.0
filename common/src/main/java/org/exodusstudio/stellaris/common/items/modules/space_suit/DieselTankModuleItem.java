package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;

public class DieselTankModuleItem extends TankModuleItem {
    public DieselTankModuleItem(Properties properties, int dieselCapacity) {
        super(properties, dieselCapacity);
    }

    @Override
    public MutableComponent displayName() {
        return Component.literal("Diesel Tank (" + capacity + " mb)");
    }

    @Override
    public Fluid getFuel() {
        return FluidsRegistry.FLOWING_DIESEL.get();
    }
}

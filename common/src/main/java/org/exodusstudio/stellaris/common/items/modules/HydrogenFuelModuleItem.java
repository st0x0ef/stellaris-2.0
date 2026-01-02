package org.exodusstudio.stellaris.common.items.modules;

import dev.architectury.fluid.FluidStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;

public class HydrogenFuelModuleItem extends Item implements RocketModule.CustomFuelModule {
    public HydrogenFuelModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public FluidStack getFuel() {
        return FluidStack.create(FluidsRegistry.HYDROGEN_STILL.get(), 0);
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.MOTOR;
    }

    @Override
    public MutableComponent displayName() {
        return Component.literal("Hydrogen Fuel");
    }
}

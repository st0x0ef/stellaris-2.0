package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;

public class HydrogenTankModuleItem extends TankModuleItem {
    public HydrogenTankModuleItem(Properties properties, int hydrogenCapacity) {
        super(properties, hydrogenCapacity);
    }

    @Override
    public MutableComponent displayName() {
        return Component.literal("Hydrogen Tank (" + capacity + " mb)");
    }

    @Override
    public Fluid getFuel() {
        return FluidsRegistry.HYDROGEN_FLOWING.get();
    }

    @Override
    public boolean isCompatibleWith(SpaceSuitModule module) {
        return !(module instanceof SpaceSuitModule.CustomFuelModule);
    }
}

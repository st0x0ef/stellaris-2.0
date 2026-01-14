package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;

public class OxygenModuleItem extends Item implements SpaceSuitModule.OxygenModule {
    private final int oxygenCapacity;

    public OxygenModuleItem(Properties properties, int oxygenCapacity) {
        super(properties);
        this.oxygenCapacity = oxygenCapacity;
    }

    @Override
    public MutableComponent displayName() {
        return Component.literal("Oxygen Module (" + oxygenCapacity + " mb)");
    }

    @Override
    public int getCapacity() {
        return oxygenCapacity;
    }
}

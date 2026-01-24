package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;

public class OilFinderModuleItem extends Item implements SpaceSuitModule.OilFinderModule {
    private final int range;

    public OilFinderModuleItem(Properties properties, int range) {
        super(properties);
        this.range = range;
    }

    @Override
    public MutableComponent displayName() {
        return Component.literal("Oil Finder Module (" + range + " x " + range + ")");
    }

    @Override
    public int getRange() {
        return range;
    }
}

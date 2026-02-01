package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;

public class OilFinderModuleItem extends Item implements SpaceSuitModule.OilFinderModule {
    private final int range;

    public OilFinderModuleItem(Properties properties, int range) {
        super(properties);
        this.range = range;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_HELMET.get());
    }
}

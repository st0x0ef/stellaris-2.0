package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;

public class JetModuleItem extends Item implements SpaceSuitModule.JetModule {
    private final long consumptionPerTick;

    public JetModuleItem(Properties properties, long consumptionPerTick) {
        super(properties);
        this.consumptionPerTick = consumptionPerTick;
    }

    @Override
    public long getConsumptionPerTick() {
        return consumptionPerTick;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_BOOTS.get());
    }
}

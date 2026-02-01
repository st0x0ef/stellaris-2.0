package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;

public class TankModuleItem extends Item implements SpaceSuitModule.CustomFuelModule {
    public int capacity;

    public TankModuleItem(Properties properties, int capacity) {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public Fluid getFuel() {
        return null;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_CHESTPLATE.get());
    }
}

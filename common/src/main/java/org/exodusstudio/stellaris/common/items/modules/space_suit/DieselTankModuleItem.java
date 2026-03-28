package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class DieselTankModuleItem extends TankModuleItem {
    public DieselTankModuleItem(Properties properties, int dieselCapacity) {
        super(properties, dieselCapacity);
    }

    @Override
    public Fluid getFuel() {
        return FluidsRegistry.FLOWING_DIESEL.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Store up to " + this.capacity + "mb of diesel on your space suit.").withColor(Utils.getMinecraftColor("gray")));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
    }
}

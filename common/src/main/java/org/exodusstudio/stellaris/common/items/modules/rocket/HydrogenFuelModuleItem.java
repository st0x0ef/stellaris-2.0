package org.exodusstudio.stellaris.common.items.modules.rocket;

import dev.architectury.fluid.FluidStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

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
    public String getDisplayName() {
        return "Hydrogen Fuel Module";
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rocket_module").withColor(Utils.getMinecraftColor("gray")));
    }
}

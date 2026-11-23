package org.exodusstudio.stellaris.common.items.modules.rocket;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class HydrogenFuelModuleItem extends Item implements RocketModule.CustomFuelModule {
    public HydrogenFuelModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public Fluid getFuel() {
        return FluidsRegistry.HYDROGEN_STILL.get();
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.MOTOR;
    }

    @Override
    public boolean fitsRover() {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rocket_module").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rover_module").withColor(Utils.getMinecraftColor("gray")));
    }
}

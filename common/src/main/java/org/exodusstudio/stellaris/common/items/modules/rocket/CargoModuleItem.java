package org.exodusstudio.stellaris.common.items.modules.rocket;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class CargoModuleItem extends Item implements RocketModule {
    public CargoModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.OTHER;
    }

    @Override
    public String getDisplayName() {
        return "Cargo Module";
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rocket_module").withColor(Utils.getMinecraftColor("gray")));
    }
}

package org.exodusstudio.stellaris.common.items.modules.rocket;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class ShieldModule extends Item implements RocketModule {

    public ShieldModule(Properties properties) {
        super(properties);
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.OTHER;
    }

    @Override
    public void renderModule(RocketRenderer.RenderingContext context) {
        if (context.rocketModel != null) {
            context.rocketModel.shield1.visible = true;
            context.rocketModel.shield2.visible = true;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rocket_module").withColor(Utils.getMinecraftColor("gray")));
    }
}

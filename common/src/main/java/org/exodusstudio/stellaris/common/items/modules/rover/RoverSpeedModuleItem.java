package org.exodusstudio.stellaris.common.items.modules.rover;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.modules.rover.RoverModule;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class RoverSpeedModuleItem extends Item implements RoverModule {

    private final float speedModifier;

    public RoverSpeedModuleItem(Properties properties, float speedModifier) {
        super(properties);
        this.speedModifier = speedModifier;
    }

    @Override
    public RoverFeature getRoverFeature() {
        return RoverFeature.SPEED;
    }

    @Override
    public float getSpeedModifier() {
        return speedModifier;
    }

    @Override
    public String getDisplayName() {
        return "Rover Speed Module";
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rover_module").withColor(Utils.getMinecraftColor("gray")));
    }
}

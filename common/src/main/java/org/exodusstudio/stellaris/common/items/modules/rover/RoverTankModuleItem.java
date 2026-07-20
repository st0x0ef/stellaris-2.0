package org.exodusstudio.stellaris.common.items.modules.rover;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.modules.rover.RoverModule;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class RoverTankModuleItem extends Item implements RoverModule {

    private final int tankCapacity;

    public RoverTankModuleItem(Properties properties, int tankCapacity) {
        super(properties);
        this.tankCapacity = tankCapacity;
    }

    @Override
    public RoverFeature getRoverFeature() {
        return RoverFeature.TANK;
    }

    @Override
    public int getTankCapacity() {
        return tankCapacity;
    }

    @Override
    public String getDisplayName() {
        return "Rover Tank Module";
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rover_module").withColor(Utils.getMinecraftColor("gray")));
    }
}

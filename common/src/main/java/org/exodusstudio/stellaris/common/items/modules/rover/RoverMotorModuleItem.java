package org.exodusstudio.stellaris.common.items.modules.rover;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.modules.rover.RoverModule;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.exodusstudio.stellaris.common.vehicle_upgrade.FuelType;

import java.util.function.Consumer;

public class RoverMotorModuleItem extends Item implements RoverModule {

    private final FuelType.Type fuelType;

    public RoverMotorModuleItem(Properties properties, FuelType.Type fuelType) {
        super(properties);
        this.fuelType = fuelType;
    }

    @Override
    public RoverFeature getRoverFeature() {
        return RoverFeature.MOTOR;
    }

    @Override
    public FuelType.Type getFuelType() {
        return fuelType;
    }

    @Override
    public String getDisplayName() {
        return "Rover Motor Module";
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rover_module").withColor(Utils.getMinecraftColor("gray")));
    }
}

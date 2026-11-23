package org.exodusstudio.stellaris.common.items.modules.rocket;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketStats;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class RocketTankModuleItem extends Item implements RocketModule {
    private final int extraCapacity;

    public RocketTankModuleItem(Properties properties, int extraCapacity) {
        super(properties);
        this.extraCapacity = extraCapacity;
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.TANK;
    }

    @Override
    public void applyStats(RocketStats.Builder builder) {
        builder.addTankCapacity(this.extraCapacity);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rocket_module").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.stellaris.tank_bonus", this.extraCapacity).withColor(Utils.getMinecraftColor("gray")));
        addRequiredForTooltip(tooltipAdder);
    }
}

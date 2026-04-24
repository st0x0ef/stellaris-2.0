package org.exodusstudio.stellaris.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class TooltipItem extends Item {

    public List<Function<ItemStack, Component>> tooltip;

    public TooltipItem(Item.Properties properties) {
        super(properties);
        tooltip = new ArrayList<>();
    }

    public TooltipItem addTooltip(Function<ItemStack, Component> tooltip) {
        this.tooltip.add(tooltip);
        return this;
    }

    public TooltipItem addTooltip(Component tooltip) {
        this.tooltip.add((s) -> tooltip);
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        for(Function<ItemStack, Component> tooltipFunction : tooltip) {
            tooltipAdder.accept(tooltipFunction.apply(stack));
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

    }
}

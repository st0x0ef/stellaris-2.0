package org.exodusstudio.stellaris.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class TooltipBlockItem extends BlockItem {

    public List<Function<ItemStack, Component>> tooltip;

    public TooltipBlockItem(Block block, Properties properties) {
        super(block, properties);
        tooltip = new ArrayList<>();
    }

    public TooltipBlockItem addTooltip(Function<ItemStack, Component> tooltip) {
        this.tooltip.add(tooltip);
        return this;
    }

    public TooltipBlockItem addTooltip(Component tooltip) {
        this.tooltip.add((s) -> tooltip);
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        for(Function<ItemStack, Component> tooltipFunction : tooltip) {
            tooltipAdder.accept(tooltipFunction.apply(stack));
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

    }
}

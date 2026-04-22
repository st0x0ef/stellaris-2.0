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

public class TooltipBlockItem extends BlockItem {

    public List<Component> tooltip;

    public TooltipBlockItem(Block block, Properties properties) {
        super(block, properties);
        tooltip = new ArrayList<>();
    }

    public TooltipBlockItem addTooltip(Component tooltip) {
        this.tooltip.add(tooltip);
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        for(Component tooltip : tooltip) {
            tooltipAdder.accept(tooltip);
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

    }
}

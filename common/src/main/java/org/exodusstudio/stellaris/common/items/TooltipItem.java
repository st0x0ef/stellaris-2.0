package org.exodusstudio.stellaris.common.items;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class TooltipItem extends Item {

    public final List<Function<ItemStack, Component>> tooltip;
    public final List<ComponentTooltipHolder> componentTooltips;

    public TooltipItem(Item.Properties properties) {
        super(properties);
        tooltip = new ArrayList<>();
        componentTooltips = new ArrayList<>();
    }

    public TooltipItem addTooltip(Function<ItemStack, Component> tooltip) {
        this.tooltip.add(tooltip);
        return this;
    }

    public <T> TooltipItem addComponentTooltip(DataComponentType<T> dataComponentType, ComponentTooltip<T> tooltip) {
        this.componentTooltips.add(new ComponentTooltipHolder(dataComponentType, tooltip));
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
        for(ComponentTooltipHolder componentTooltipHolder : componentTooltips) {

            if(stack.has(componentTooltipHolder.dataComponentType)) {
                tooltipAdder.accept(componentTooltipHolder.componentTooltip.addTooltip(stack, stack.get(componentTooltipHolder.dataComponentType)));

            }
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

    }

    record ComponentTooltipHolder(DataComponentType<?> dataComponentType, ComponentTooltip componentTooltip){}

    @FunctionalInterface
    public interface ComponentTooltip<T> {
        Component addTooltip(ItemStack stack, T value);
    }
}

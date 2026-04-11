package org.exodusstudio.stellaris.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

import java.util.function.Consumer;

public class SDCardItem extends Item {
    public SDCardItem(Properties properties, String name) {
        super(properties.component(DataComponentsRegistry.SD_CARD_NAME.get(), name));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("ID: " + stack.get(DataComponentsRegistry.SD_CARD_NAME.get())).withStyle(ChatFormatting.GRAY));
    }
}

package org.exodusstudio.stellaris.common.items.infection;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.components.PathogenStorageComponents;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

import java.util.function.Consumer;

public class PathogenStorageCellItem extends Item {
    private static final PathogenStorageComponents DEFAULT_COMPONENT = new PathogenStorageComponents(0, 100); // TODO : make max capacity configurable

    public PathogenStorageCellItem(Properties properties) {
        super(properties.component(DataComponentsRegistry.PATHOGEN_STORED.get(), DEFAULT_COMPONENT));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        PathogenStorageComponents pathogenStorageComponents = stack.getOrDefault(DataComponentsRegistry.PATHOGEN_STORED.get(), DEFAULT_COMPONENT);
        String stored = String.valueOf(pathogenStorageComponents.stored());
        String capacity = String.valueOf(pathogenStorageComponents.capacity());

        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.pathogen_storage_cell_info"));
        tooltipAdder.accept(Component.literal("Parasites ").append(Component.literal(stored).append(" / ").append(capacity).withStyle(ChatFormatting.GRAY)));
    }
}

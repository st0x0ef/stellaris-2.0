package org.exodusstudio.stellaris.common.items;

import com.fej1fun.potentials.fluid.ItemFluidStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FluidCellItem extends Item implements FluidProvider.ITEM {
    private final int capacity;

    public FluidCellItem(Properties properties, final int capacity) {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack stack) {
        return new ItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), stack, 1, capacity);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        UniversalFluidItemStorage fluidStorage = getFluidTank(stack);
        if (fluidStorage != null) {
            FluidStack fluidStack = fluidStorage.getFluidInTank(0);
            if (!fluidStack.isEmpty()) {
                String fluidInfo = fluidStack.getName().getString() + " : " + fluidStack.getAmount() + " / " + capacity;
                tooltipAdder.accept(Component.literal(fluidInfo));
            } else {
                String fluidInfo = "Empty: 0 / " + capacity;
                tooltipAdder.accept(Component.literal(fluidInfo));
            }
        }
    }
}

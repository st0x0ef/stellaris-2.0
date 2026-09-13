package org.exodusstudio.stellaris.common.items;

import com.fej1fun.potentials.fluid.ItemFluidStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.blocks.FluidTankBlock;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FluidTankItem extends BlockItem implements FluidProvider.ITEM {

    final long capacity;

    public FluidTankItem(FluidTankBlock block, Properties properties) {
        super(block, properties);
        this.capacity = block.capacity;
    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack stack) {
        return new ItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), stack, 1, capacity);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        FluidStack fluidStack = FluidUtil.readStoredFluid(stack, DataComponentsRegistry.FLUID_LIST.get(), 0);
        if (!fluidStack.isEmpty()) {
            tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.fluid_tank.content",
                    fluidStack.getName(), fluidStack.getAmount(), capacity).withStyle(ChatFormatting.GRAY));
        } else {
            tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.fluid_tank.empty",
                    Component.translatable("fluid.stellaris.empty"), capacity).withStyle(ChatFormatting.GRAY));
        }
    }
}

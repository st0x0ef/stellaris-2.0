package org.exodusstudio.stellaris.common.items.space_suit;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.energy.InfiniteItemEnergyStorage;
import org.exodusstudio.stellaris.common.fluid.InfiniteOxygenItemFluidStorage;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CreativeSpaceSuitHelmet extends SpaceSuitHelmet {
    public static final int OXYGEN_CAPACITY = Integer.MAX_VALUE;

    public CreativeSpaceSuitHelmet(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack itemStack) {
        return new InfiniteOxygenItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), itemStack, 1, OXYGEN_CAPACITY);
    }

    @Override
    public @Nullable UniversalEnergyStorage getEnergy(@NotNull ItemStack stack) {
        return new InfiniteItemEnergyStorage(stack, DataComponentsRegistry.ENERGY.get(), ENERGY_CAPACITY, 20, 25);
    }

    /** Never zero, so the helmet breathes on its own without an oxygen module installed. */
    @Override
    protected int oxygenCapacity(ItemStack stack) {
        return OXYGEN_CAPACITY;
    }

    @Override
    protected void appendEnergyHoverText(ItemStack stack, Consumer<Component> tooltipAdder) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.energy.unlimited"));
    }

    @Override
    protected void appendOxygenHoverText(ItemStack stack, Consumer<Component> tooltipAdder) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.space_suit.oxygen_module.header").withColor(Utils.getMinecraftColor("cyan")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.space_suit.oxygen_module.unlimited").withColor(Utils.getMinecraftColor("cyan")));
    }
}

package org.exodusstudio.stellaris.common.items;

import com.fej1fun.potentials.energy.ItemEnergyStorage;
import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.providers.EnergyProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.blocks.PowerBankBlock;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class PowerBankItem extends BlockItem implements EnergyProvider.ITEM {

    final int capacity;

    public  PowerBankItem(PowerBankBlock block, Properties properties) {
        super(block, properties);
        this.capacity = (int) Math.pow(2, 4 * block.tier) * 1000;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        UniversalEnergyStorage energy = getEnergy(stack);
        tooltipAdder.accept(Component.literal(energy.getEnergy() + " / " + energy.getMaxEnergy() + "FE").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull UniversalEnergyStorage getEnergy(@NotNull ItemStack stack) {
        return new ItemEnergyStorage(stack, DataComponentsRegistry.ENERGY.get(), capacity, capacity, capacity);
    }
}
package org.exodusstudio.stellaris.common.items.space_suit;

import com.fej1fun.potentials.energy.ItemEnergyStorage;
import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.fluid.ItemFluidStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.EnergyProvider;
import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SpaceSuitHelmet extends SpaceSuitItem implements FluidProvider.ITEM, EnergyProvider.ITEM {
    public SpaceSuitHelmet(Properties properties) {
        super(properties, ArmorType.HELMET);
    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack itemStack) {
        return new ItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), itemStack, 1, getOxygenCapacity(itemStack));
    }

    public int getOxygenCapacity(ItemStack stack) {
        AtomicInteger oxygenCapacity = new AtomicInteger(0);
        ModuleUtils.getSpaceSuitModules(stack).getModules().forEach(module -> {
            if (module instanceof SpaceSuitModule.OxygenModule oxygenModule) {
                if (oxygenCapacity.get() < oxygenModule.getCapacity()) {
                    oxygenCapacity.set(oxygenModule.getCapacity());
                }
            }
        });

        return oxygenCapacity.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        UniversalEnergyStorage energy = getEnergy(stack);
        if (energy != null) {
            tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.energy", getEnergy(stack).getEnergy(), getEnergy(stack).getMaxEnergy()));
        }

        if (ModuleUtils.hasSpaceSuitModule(stack, SpaceSuitModule.OxygenModule.class)) {
            int oxygenCapacity = getOxygenCapacity(stack);
            UniversalFluidItemStorage fluidTank = getFluidTank(stack);
            tooltipAdder.accept(Component.literal("-- Oxygen Module --").withColor(Utils.getMinecraftColor("cyan")));
            tooltipAdder.accept(Component.literal("Oxygen " + fluidTank.getFluidInTank(0).getAmount() + " / " + oxygenCapacity + " mb").withColor(Utils.getMinecraftColor("cyan")));
        }

        SpaceSuitModule.OilFinderModule oilFinderModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.OilFinderModule.class);
        if (oilFinderModule != null) {
            tooltipAdder.accept(Component.literal("-- Oil Finder Module --").withColor(Utils.getMinecraftColor("gold")));
            tooltipAdder.accept(Component.literal("Allows detection of oil deposits in the surrounding " + oilFinderModule.getRange() +  " x " + oilFinderModule.getRange() + " area").withColor(Utils.getMinecraftColor("gold")));
        }
    }

    @Override
    public @Nullable UniversalEnergyStorage getEnergy(@NotNull ItemStack stack) {
        return new ItemEnergyStorage(stack, DataComponentsRegistry.ENERGY.get(), 1000, 20, 1);
    }

    public static void tickOilFinderEnergy(ItemStack stack) {
        SpaceSuitModule.OilFinderModule oilFinderModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.OilFinderModule.class);

        if (stack.getItem() instanceof SpaceSuitHelmet spaceSuitHelmet && oilFinderModule != null) {
            spaceSuitHelmet.getEnergy(stack).extract(oilFinderModule.getRange() * oilFinderModule.getRange(), false);
        }
    }
}

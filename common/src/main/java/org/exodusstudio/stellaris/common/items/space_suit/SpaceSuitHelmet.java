package org.exodusstudio.stellaris.common.items.space_suit;

import com.fej1fun.potentials.fluid.ItemFluidStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModules;
import org.exodusstudio.stellaris.common.registries.ArmorMaterialsRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SpaceSuitHelmet extends Item implements FluidProvider.ITEM {
    public SpaceSuitHelmet(Properties properties) {
        super(properties.humanoidArmor(ArmorMaterialsRegistry.JET_SUIT, ArmorType.HELMET));
    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack itemStack) {
        return new ItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), itemStack, 1, getOxygenCapacity(itemStack));
    }

    private Modules<SpaceSuitModule> getModules(ItemStack stack) {;
        return stack.getOrDefault(DataComponentsRegistry.SPACE_SUIT_MODULES.get(), SpaceSuitModules.empty());
    }

    private int getOxygenCapacity(ItemStack stack) {
        AtomicInteger oxygenCapacity = new AtomicInteger(0);
        this.getModules(stack).getModules().forEach(module -> {
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

        int oxygenCapacity = getOxygenCapacity(stack);
        UniversalFluidItemStorage fluidTank = getFluidTank(stack);
        if (oxygenCapacity > 0 && fluidTank != null) {
            tooltipAdder.accept(Component.literal("-- Oxygen Module --").withColor(Utils.getMinecraftColor("cyan")));
            tooltipAdder.accept(Component.literal("Oxygen " + fluidTank.getFluidInTank(0).getAmount() + " / " + oxygenCapacity + " mb").withColor(Utils.getMinecraftColor("cyan")));
        }
    }
}

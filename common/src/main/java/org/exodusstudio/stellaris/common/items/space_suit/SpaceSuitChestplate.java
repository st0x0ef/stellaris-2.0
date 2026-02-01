package org.exodusstudio.stellaris.common.items.space_suit;

import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.exodusstudio.stellaris.common.fluid.SpaceSuitItemFluidStorage;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.ArmorMaterialsRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SpaceSuitChestplate extends SpaceSuitItem implements FluidProvider.ITEM {
    public SpaceSuitChestplate(Properties properties) {
        super(properties.humanoidArmor(ArmorMaterialsRegistry.SPACE_SUIT, ArmorType.CHESTPLATE));
    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack itemStack) {
        SpaceSuitModule.CustomFuelModule tankModule = ModuleUtils.getSpaceSuitModule(itemStack, SpaceSuitModule.CustomFuelModule.class);
        return new SpaceSuitItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), itemStack, 1, getFuelCapacity(tankModule), tankModule);
    }

    public int getFuelCapacity(SpaceSuitModule.CustomFuelModule tankModule) {
        if (tankModule != null) {
            return tankModule.getCapacity();
        }

        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        SpaceSuitModule.CustomFuelModule tankModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.CustomFuelModule.class);
        if (tankModule != null) {
            int oxygenCapacity = getFuelCapacity(tankModule);
            UniversalFluidItemStorage fluidTank = getFluidTank(stack);
            if (fluidTank != null) {
                FluidStack fluidStackToGetName = FluidStack.create(tankModule.getFuel(), 1);
                String fluidName = fluidStackToGetName.getName().getString().replace("_", " ");
                tooltipAdder.accept(Component.literal("-- " + fluidName + " Tank Module --").withColor(Utils.getMinecraftColor("cyan")));
                tooltipAdder.accept(Component.literal(fluidName + " " + fluidTank.getFluidInTank(0).getAmount() + " / " + oxygenCapacity + " mb").withColor(Utils.getMinecraftColor("cyan")));
            }
        }
    }
}

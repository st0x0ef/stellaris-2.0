package org.exodusstudio.stellaris.common.fluid;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.ItemFluidStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.jetbrains.annotations.Nullable;

public class SpaceSuitItemFluidStorage extends ItemFluidStorage {
    private final @Nullable SpaceSuitModule.CustomFuelModule tankModule;

    public SpaceSuitItemFluidStorage(DataComponentType<FluidAmountMapDataComponent> component, ItemStack stack, int tanks, long maxAmount, @Nullable SpaceSuitModule.CustomFuelModule tankModule) {
        super(component, stack, tanks, maxAmount);
        this.tankModule = tankModule;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack fluidStack) {
        if (tankModule == null || maxAmount == 0) return false;
        return fluidStack.getFluid().isSame(tankModule.getFuel());
    }
}

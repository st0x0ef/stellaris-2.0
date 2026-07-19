package org.exodusstudio.stellaris.common.fluid;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.ItemFluidStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;

public class OxygenItemFluidStorage extends ItemFluidStorage {
    public OxygenItemFluidStorage(DataComponentType<FluidAmountMapDataComponent> component, ItemStack stack, int tanks, long maxAmount) {
        super(component, stack, tanks, maxAmount);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack fluidStack) {
        if (maxAmount == 0) return false;
        Fluid fluid = fluidStack.getFluid();
        return fluid.isSame(FluidsRegistry.OXYGEN_STILL.get()) || fluid.isSame(FluidsRegistry.OXYGEN_FLOWING.get());
    }
}

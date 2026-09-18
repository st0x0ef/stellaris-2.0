package org.exodusstudio.stellaris.common.fluid;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class InfiniteOxygenItemFluidStorage extends OxygenItemFluidStorage {
    public InfiniteOxygenItemFluidStorage(DataComponentType<FluidAmountMapDataComponent> component, ItemStack stack, int tanks, long maxAmount) {
        super(component, stack, tanks, maxAmount);
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.create(FluidsRegistry.OXYGEN_STILL.get(), maxAmount);
    }

    @Override
    public long fill(FluidStack fluidStack, boolean simulate) {
        return 0L;
    }

    @Override
    public FluidStack drain(FluidStack fluidStack, boolean simulate) {
        if (!isFluidValid(0, fluidStack)) {
            return FluidStack.empty();
        }

        return FluidStack.create(FluidsRegistry.OXYGEN_STILL.get(), fluidStack.getAmount());
    }

    @Override
    public @NotNull Iterator<FluidStack> iterator() {
        return List.of(getFluidInTank(0)).iterator();
    }
}

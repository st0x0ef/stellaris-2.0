package org.exodusstudio.stellaris.common.fluid;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public abstract class VehicleFuelStorage implements UniversalFluidStorage {

    public abstract long getFuelAmount();

    public abstract void setFuelAmount(long amount);

    /** Maximum fuel the vehicle can hold. */
    public abstract long getCapacity();

    public abstract Fluid getFuelFluid();

    protected void onFill(FluidStack inserted) {}

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        Fluid fluid = getFuelFluid();
        long amount = getFuelAmount();
        if (fluid == null || amount <= 0) {
            return FluidStack.empty();
        }
        return FluidStack.create(fluid, amount);
    }

    @Override
    public long getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        Fluid fluid = getFuelFluid();
        return fluid != null && stack.getFluid().isSame(fluid);
    }

    @Override
    public long fill(FluidStack stack, boolean simulate) {
        if (!isFluidValid(0, stack)) {
            return 0L;
        }

        long current = getFuelAmount();
        long space = getCapacity() - current;
        if (space <= 0) {
            return 0L;
        }

        long filled = Math.min(space, stack.getAmount());
        if (!simulate) {
            if (current <= 0) {
                onFill(stack);
            }
            setFuelAmount(current + filled);
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack stack, boolean simulate) {
        long current = getFuelAmount();
        if (current <= 0) {
            return FluidStack.empty();
        }

        Fluid fluid = getFuelFluid();
        if (fluid == null || !fluid.isSame(stack.getFluid())) {
            return FluidStack.empty();
        }

        long drained = Math.min(current, stack.getAmount());
        if (!simulate) {
            setFuelAmount(current - drained);
        }
        return FluidStack.create(fluid, drained);
    }

    @Override
    public @NotNull Iterator<FluidStack> iterator() {
        return List.of(getFluidInTank(0)).iterator();
    }
}

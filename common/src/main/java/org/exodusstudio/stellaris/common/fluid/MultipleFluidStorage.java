package org.exodusstudio.stellaris.common.fluid;

import com.fej1fun.potentials.fluid.BaseFluidStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class MultipleFluidStorage extends BaseFluidStorage {


    public MultipleFluidStorage(int tanks, long capacity, long maxFill, long maxDrain) {
        super(tanks, capacity, maxFill, maxDrain);
    }

    public MultipleFluidStorage(int tanks, long capacity) {
        super(tanks, capacity);
    }

    @Override
    public long fill(FluidStack stack, boolean simulate) {
        long filled = 0;
        for (int i = 0; i < getTanks(); i++) {
            if (!isFluidValid(i, stack)) {
                continue;
            }
            FluidStack fluidStack = fluidStacks.get(i);
            if (!(fluidStack.getFluid() == stack.getFluid() || fluidStack.isEmpty())) {
                continue;
            }
            if (fluidStack.getAmount() >= capacity) {
                continue;
            }
            long inTank = getFluidValueInTank(i);
            filled = Math.clamp(this.capacity - inTank, 0L, Math.min(this.maxFill, stack.getAmount()));
            if (!simulate) {
                fluidStacks.set(i, stack.copyWithAmount(inTank + filled));
                onChange(i);
            }
            break;
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack stack, boolean simulate) {
        long drained = 0;
        for (int i = 0; i < getTanks(); i++) {
            if (!isFluidValid(i, stack)) {
                continue;
            }
            FluidStack fluid = getFluidInTank(i);
            if (fluid.isEmpty()) {
                continue;
            }
            if (fluid.getFluid() != stack.getFluid()) {
                continue;
            }
            drained = Math.min(getFluidValueInTank(i), Math.min(this.maxDrain, stack.getAmount()));
            if (!simulate) {
                setFluidInTank(i, FluidStack.create(fluid, getFluidValueInTank(i) - drained));
                onChange(i);
            }

            break;
        }
        return FluidStack.create(stack, drained);
    }

    @Override
    public long fillWithoutLimits(FluidStack stack, boolean simulate) {
        long filled = 0;
        for (int i = 0; i < getTanks(); i++) {
            if (!isFluidValid(i, stack)) {
                continue;
            }
            if (!(fluidStacks.get(i).getFluid() == stack.getFluid() || fluidStacks.get(i).isEmpty())) {
                continue;
            }
            if (fluidStacks.get(i).getAmount() >= capacity) {
                continue;
            }
            filled = Math.clamp(this.capacity - getFluidValueInTank(i), 0L, stack.getAmount());
            if (!simulate) {
                fluidStacks.set(i, stack.copyWithAmount(getFluidValueInTank(i) + filled));
                onChange(i);
            }
            break;
        }
        return filled;
    }

    @Override
    public FluidStack drainWithoutLimits(FluidStack stack, boolean simulate) {
        long drained = 0;
        for (int i = 0; i < getTanks(); i++) {
            if (!isFluidValid(i, stack)) {
                continue;
            }
            if (getFluidInTank(i).isEmpty()) {
                continue;
            }
            if (getFluidInTank(i).getFluid() != stack.getFluid()) {
                continue;
            }
            drained = Math.min(getFluidValueInTank(i), stack.getAmount());
            if (!simulate) {
                fluidStacks.get(i).shrink(drained);
                onChange(i);
            }

            break;
        }
        return FluidStack.create(stack, drained);
    }


    public void save(ValueOutput output, String name) {
        for (int i = 0; i < getTanks(); i++) {
            FluidStack stack = getFluidInTank(i);
            if (!stack.isEmpty()) {
                output.store(name + "-tank-" + i, FluidStack.CODEC, stack);
            }
        }
    }

    public void load(ValueInput input, String name) {
        for (int i = 0; i < getTanks(); i++) {
            final int tank = i;
            input.read(name + "-tank-" + i, FluidStack.CODEC).ifPresent(stack -> setFluidInTank(tank, stack));
        }
    }


    public boolean isEmpty() {
        for (FluidStack stack : this) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    protected abstract void onChange(int tank);
}

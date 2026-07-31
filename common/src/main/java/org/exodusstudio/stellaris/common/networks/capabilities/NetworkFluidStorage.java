package org.exodusstudio.stellaris.common.networks.capabilities;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class NetworkFluidStorage implements UniversalFluidStorage {

    private Runnable markDirtyCallback;

    public static final Codec<NetworkFluidStorage> CODEC = RecordCodecBuilder.create(i -> i.group(
            FluidStack.CODEC.fieldOf("fluidStack").forGetter(s -> s.getFluidInTank(0)),
            Codec.LONG.fieldOf("capacity").forGetter(s -> s.getTankCapacity(0))
    ).apply(i, NetworkFluidStorage::new));

    protected FluidStack fluidStack;
    protected long       capacity;

    public NetworkFluidStorage(FluidStack stack, long capacity) {
        this.fluidStack = stack;
        this.capacity = capacity;
    }

    public NetworkFluidStorage(long capacity) {
        this(FluidStack.empty(), capacity);
    }

    public NetworkFluidStorage() {
        this(FluidStack.empty(), 0);
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
        markDirty();
    }

    public void setAmount(long amount) {
        this.getFluidInTank(0).setAmount(amount);
        markDirty();
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0) {
        }
        return fluidStack.copy();
    }

    @Override
    public long getTankCapacity(int tank) {
        if (tank != 0) {
        }
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        if (tank != 0) {
        }
        return true;
    }

    public long getFluidValueInTank() {
        return fluidStack.getAmount();
    }

    public void setFluidInTank(FluidStack stack) {
        this.fluidStack = stack;
        this.fluidStack.setAmount(Math.clamp(stack.getAmount(), 0, getTankCapacity(0)));
    }

    @Override
    public FluidStack drain(FluidStack stack, boolean simulate) {
        if (!isFluidValid(0, stack)) {
            return FluidStack.empty();
        }
        if (getFluidInTank(0).isEmpty()) {
            return FluidStack.empty();
        }

        if (!getFluidInTank(0).isFluidEqual(stack)) {
            return FluidStack.empty();
        }

        long drained = Math.min(stack.getAmount(), getFluidValueInTank());
        if (!simulate) {
            this.fluidStack.shrink(drained);
            markDirty();
        }

        return FluidStack.create(stack, drained);
    }

    @Override
    public long fill(FluidStack stack, boolean simulate) {
        if (!isFluidValid(0, stack)) {
            return 0L;
        }
        if (!(this.fluidStack.getFluid() == stack.getFluid() || this.fluidStack.isEmpty())) {
            return 0L;
        }
        if (this.fluidStack.getAmount() >= getTankCapacity(0)) {
            return 0L;
        }

        long filled = Math.clamp(getTankCapacity(0) - getFluidValueInTank(), 0L,stack.getAmount());
        if (!simulate) {
            this.fluidStack = stack.copyWithAmount(this.fluidStack.getAmount() + filled);
            markDirty();
        }

        return filled;
    }

    @Override
    public @NotNull Iterator<FluidStack> iterator() {
        return List.of(fluidStack).iterator();
    }

    public void setMarkDirtyCallback(Runnable callback) {
        this.markDirtyCallback = callback;
    }

    protected void markDirty() {
        if (this.markDirtyCallback != null) {
            this.markDirtyCallback.run();
        }
    }

}

package org.exodusstudio.stellaris.common.transport;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public final class PassthroughFluidStorage implements UniversalFluidStorage {

    private final Level level;
    private final BlockPos pos;
    private final @Nullable Direction side;
    private final long maxIn;

    public PassthroughFluidStorage(Level level, BlockPos pos, @Nullable Direction side, long maxIn) {
        this.level = level;
        this.pos = pos;
        this.side = side;
        this.maxIn = maxIn;
    }

    @Override
    public long fill(FluidStack stack, boolean simulate) {
        if (stack.isEmpty() || level.isClientSide()) {
            return 0L;
        }
        long capped = Math.min(stack.getAmount(), maxIn);
        if (capped <= 0) {
            return 0L;
        }

        FluidStack incoming = stack.copyWithAmount(capped);
        SingleFluidStorage source = new SingleFluidStorage(capped, 0, capped) {
            @Override
            protected void onChange() {
            }
        };
        source.setFluidInTank(incoming);

        TransportGraph.Network network = TransportGraph.get(level, pos, TransportMedium.FLUID);
        BlockPos[] exclude = side != null ? new BlockPos[]{pos.relative(side)} : new BlockPos[0];
        return Transport.spreadAcrossNetwork(level, pos, source, Transport.fluidMover(incoming),
                TransportMedium.FLUID, network, simulate, exclude);
    }

    @Override
    public FluidStack drain(FluidStack stack, boolean simulate) {
        return FluidStack.empty();
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.empty();
    }

    @Override
    public long getTankCapacity(int tank) {
        return maxIn;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public Iterator<FluidStack> iterator() {
        return List.of(FluidStack.empty()).iterator();
    }
}

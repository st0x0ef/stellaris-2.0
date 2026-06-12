package org.exodusstudio.stellaris.common.fluid;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.fluid.BaseFluidStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.PipeBlock;
import org.exodusstudio.stellaris.common.blocks.PumpjackProxyBlock;
import org.exodusstudio.stellaris.common.transport.TransportGraph;
import org.exodusstudio.stellaris.common.transport.TransportType;

import java.util.*;

@SuppressWarnings("all")
public class FluidUtil {

    private static final List<Direction> ALL_DIRECTIONS = List.of(Direction.values());

    public static void moveFluidToItem(int tank, UniversalFluidStorage from, int slot, int resultSlot, NonNullList<ItemStack> items, long amount) {
        if (items.get(slot).isEmpty()) {
            return;
        }

        UniversalFluidItemStorage to = Capabilities.Fluid.ITEM.getCapability(items.get(slot));

        if (to == null) {
            return;
        }

        amount = Math.min(amount, to.getTankCapacity(0) - to.getFluidInTank(0).getAmount());
        moveFluid(from, to, from.getFluidInTank(tank).copyWithAmount(amount));

        if (slot != resultSlot) items.set(slot, ItemStack.EMPTY);
        items.set(resultSlot, to.getContainer());
    }

    public static void moveFluidFromItem(int tank, int slot, int remainingItemSlot, NonNullList<ItemStack> items, UniversalFluidStorage to, long amount) {
        if (items.get(slot).isEmpty()) {
            return;
        }

        ItemStack actualRemainingItems = items.get(remainingItemSlot);

        if (actualRemainingItems.getCount() == actualRemainingItems.getMaxStackSize()) {
            return;
        }

        UniversalFluidItemStorage from = Capabilities.Fluid.ITEM.getCapability(items.get(slot));

        if (from == null) {
            return;
        }

        amount = Math.min(amount, to.getTankCapacity(tank) - to.getFluidInTank(tank).getAmount());
        FluidStack fluidMoved = moveFluid(from, to, from.getFluidInTank(tank).copyWithAmount(amount));

        if (!fluidMoved.isEmpty() && slot != remainingItemSlot) {
            items.set(slot, ItemStack.EMPTY);
        }

        if (!fluidMoved.isEmpty() && actualRemainingItems.isEmpty()) {
            items.set(remainingItemSlot, from.getContainer());
        }
        else if (!fluidMoved.isEmpty() && actualRemainingItems.is(from.getContainer().getItem())) {
            if (actualRemainingItems.getCount() + from.getContainer().getCount() < actualRemainingItems.getMaxStackSize()) {
                items.set(remainingItemSlot, actualRemainingItems.copyWithCount(actualRemainingItems.getCount() + from.getContainer().getCount()));
            }
            else {
                items.set(remainingItemSlot, actualRemainingItems.copyWithCount(actualRemainingItems.getMaxStackSize()));
            }
        }
    }

    public static FluidStack moveFluid(UniversalFluidStorage from, UniversalFluidStorage to, FluidStack stack) {
        if (stack.isEmpty()) {
            return FluidStack.empty();
        }

        FluidStack inserted = FluidStack.create(stack, to.fill(from.drain(stack, true), true));

        if (inserted.isEmpty()) {
            return FluidStack.empty();
        }

        from.drain(inserted.copy(), false);
        to.fill(inserted.copy(), false);

        return inserted;
    }

    /// ignores max fill/drain limits
    public static FluidStack moveFluidWithSet(BaseFluidStorage from, BaseFluidStorage to, FluidStack stack) {
        FluidStack inserted = FluidStack.create(stack, to.fill(from.drain(stack, true), true));

        if (inserted.isEmpty()) {
            return FluidStack.empty();
        }

        from.drainWithoutLimits(inserted, false);
        to.fillWithoutLimits(inserted, false);

        return inserted;
    }

    public static void distributeFluidNearby(Level level, BlockPos pos, FluidStack stack) {
        distributeFluidNearby(level, pos, stack, null);
    }

    /**
     * Pushes {@code stack} out of the tank exposed on each output direction. A direction backed by a
     * {@link PipeBlock} routes the fluid across the whole connected network (instantly, losslessly)
     * to every sink that accepts it; any other neighbour receives a direct transfer.
     *
     * @param outputDirections the faces to push from, or {@code null}/empty to try all six.
     */
    public static void distributeFluidNearby(Level level, BlockPos pos, FluidStack stack, List<Direction> outputDirections) {
        if (stack.isEmpty() || level.isClientSide()) {
            return;
        }

        List<Direction> directions = (outputDirections == null || outputDirections.isEmpty())
                ? ALL_DIRECTIONS : outputDirections;

        for (Direction direction : directions) {
            UniversalFluidStorage from = getFluidCapability(level, pos, direction);
            if (from == null) {
                continue;
            }

            FluidStack drainable = from.drain(stack, true);
            if (drainable.getAmount() <= 0) {
                continue;
            }
            FluidStack fluid = stack.copyWithAmount(drainable.getAmount());

            BlockPos neighbor = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighbor);

            if (TransportType.FLUID.isNode(neighborState)) {
                routeToNetwork(level, pos, from, fluid, TransportGraph.get(level, neighbor, TransportType.FLUID));
            } else {
                UniversalFluidStorage to = getFluidCapability(level, neighbor, direction.getOpposite());
                if (to != null) {
                    moveFluid(from, to, fluid);
                }
            }
        }
    }

    /**
     * Distributes {@code fluid} from {@code from} across every boundary sink of {@code network} that
     * accepts it, capped by the network's remaining per-tick throughput. The remainder of an
     * uneven split is carried forward so nothing is lost.
     */
    private static void routeToNetwork(Level level, BlockPos sourcePos, UniversalFluidStorage from, FluidStack fluid, TransportGraph.Network network) {
        if (network.throughputRemaining <= 0) {
            return;
        }

        List<UniversalFluidStorage> sinks = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        seen.add(sourcePos); // never push back into the producer

        for (TransportGraph.BoundaryFace face : network.boundary) {
            if (!seen.add(face.pos())) {
                continue;
            }
            UniversalFluidStorage to = getFluidCapability(level, face.pos(), face.side());
            if (to == null || to.fill(fluid, true) <= 0) {
                continue;
            }
            sinks.add(to);
        }

        if (sinks.isEmpty()) {
            return;
        }

        long remaining = Math.min(fluid.getAmount(), network.throughputRemaining);
        int count = sinks.size();
        for (int i = 0; i < count && remaining > 0; i++) {
            long share = remaining / (count - i);
            if (share <= 0) {
                share = remaining;
            }
            long moved = moveFluid(from, sinks.get(i), fluid.copyWithAmount(share)).getAmount();
            remaining -= moved;
            network.throughputRemaining -= moved;
        }
    }

    private static UniversalFluidStorage getFluidCapability(Level level, BlockPos pos, Direction direction) {
        UniversalFluidStorage direct = Capabilities.Fluid.BLOCK.getCapability(level, pos, direction);

        if (direct != null) {
            return direct;
        }

        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof PumpjackProxyBlock) {
            BlockPos mainPos = PumpjackProxyBlock.getMainPos(pos, state);
            return Capabilities.Fluid.BLOCK.getCapability(level, mainPos, direction);
        }

        return null;
    }
}
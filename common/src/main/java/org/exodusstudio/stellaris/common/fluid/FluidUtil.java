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
import org.exodusstudio.stellaris.common.transport.Transport;
import org.exodusstudio.stellaris.common.transport.TransportMedium;

import java.util.*;

@SuppressWarnings("all")
public class FluidUtil {

    private static final List<Direction> ALL_DIRECTIONS = List.of(Direction.values());

    public static void moveFluidToItem(int tank, UniversalFluidStorage from, int slot, int resultSlot, NonNullList<ItemStack> items, long amount) {
        if (items.get(slot).isEmpty()) {
            return;
        }

        // Don't proceed if the result slot already holds something: filling would overwrite (destroy) it.
        if (slot != resultSlot && !items.get(resultSlot).isEmpty()) {
            return;
        }

        UniversalFluidItemStorage to = Capabilities.Fluid.ITEM.getCapability(items.get(slot));

        if (to == null) {
            return;
        }

        amount = Math.min(amount, to.getTankCapacity(0) - to.getFluidInTank(0).getAmount());
        if (amount <= 0) {
            return;
        }

        FluidStack moved = moveFluid(from, to, from.getFluidInTank(tank).copyWithAmount(amount));

        if (moved.isEmpty()) {
            return;
        }

        if (slot != resultSlot) items.set(slot, ItemStack.EMPTY);
        items.set(resultSlot, to.getContainer().copy());
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

        ItemStack remainingContainer = from.getContainer().copy();

        if (!fluidMoved.isEmpty() && actualRemainingItems.isEmpty()) {
            items.set(remainingItemSlot, remainingContainer);
        }
        else if (!fluidMoved.isEmpty() && actualRemainingItems.is(remainingContainer.getItem())) {
            if (actualRemainingItems.getCount() + remainingContainer.getCount() < actualRemainingItems.getMaxStackSize()) {
                items.set(remainingItemSlot, actualRemainingItems.copyWithCount(actualRemainingItems.getCount() + remainingContainer.getCount()));
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
     * pipe routes the fluid across the whole connected network (instantly, losslessly) to every sink
     * that accepts it; any other neighbour receives a direct transfer. The actual routing is handled
     * by the shared {@link Transport} engine.
     *
     * @param outputDirections the faces to push from, or {@code null}/empty to try all six.
     */
    public static void distributeFluidNearby(Level level, BlockPos pos, FluidStack stack, List<Direction> outputDirections) {
        if (stack.isEmpty() || level.isClientSide()) {
            return;
        }

        List<Direction> directions = (outputDirections == null || outputDirections.isEmpty())
                ? ALL_DIRECTIONS : outputDirections;

        Transport.distribute(level, pos, directions, TransportMedium.FLUID, Transport.fluidMover(stack));
    }
}
package org.exodusstudio.stellaris.common.fluid;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.BaseFluidStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.transport.Transport;
import org.exodusstudio.stellaris.common.transport.TransportMedium;

import java.util.List;

@SuppressWarnings("all")
public class FluidUtil {

    private static final List<Direction> ALL_DIRECTIONS = List.of(Direction.values());

    public static FluidStack readStoredFluid(ItemStack stack, DataComponentType<FluidAmountMapDataComponent> component, int tank) {
        FluidAmountMapDataComponent comp = stack.get(component);
        return comp == null ? FluidStack.empty() : comp.getAsFluidStack(tank);
    }

    public static UniversalFluidItemStorage getItemFluidStorage(ItemStack stack) {
        if (stack.getItem() instanceof FluidProvider.ITEM provider) {
            UniversalFluidItemStorage storage = provider.getFluidTank(stack);
            if (storage != null) {
                return storage;
            }
        }
        return Capabilities.Fluid.ITEM.getCapability(stack);
    }

    public static void moveFluidToItem(int tank, UniversalFluidStorage from, int slot, int resultSlot, Container container, long amount) {
        moveFluidToItem(tank, from, slot, resultSlot, container, amount, false);
    }

    public static void moveFluidToItem(int tank, UniversalFluidStorage from, int slot, int resultSlot, Container container, long amount, boolean ignoreDrainLimit) {
        if (container.getItem(slot).isEmpty()) {
            return;
        }

        // Don't proceed if the result slot already holds something: filling would overwrite (destroy) it.
        if (slot != resultSlot && !container.getItem(resultSlot).isEmpty()) {
            return;
        }

        UniversalFluidItemStorage to = getItemFluidStorage(container.getItem(slot));

        if (to == null) {
            return;
        }

        amount = Math.min(amount, to.getTankCapacity(0) - to.getFluidInTank(0).getAmount());
        if (amount <= 0) {
            return;
        }

        FluidStack stack = from.getFluidInTank(tank).copyWithAmount(amount);
        FluidStack moved = ignoreDrainLimit ? moveFluidIgnoringDrainLimit(from, to, stack) : moveFluid(from, to, stack);

        if (moved.isEmpty()) {
            return;
        }

        if (slot != resultSlot) container.setItem(slot, ItemStack.EMPTY);
        container.setItem(resultSlot, to.getContainer().copy());
    }

    public static void moveFluidFromItem(int tank, int slot, int remainingItemSlot, Container container, UniversalFluidStorage to, long amount) {
        ItemStack input = container.getItem(slot);
        if (input.isEmpty()) {
            return;
        }

        ItemStack remaining = container.getItem(remainingItemSlot);
        if (slot != remainingItemSlot && remaining.getCount() >= remaining.getMaxStackSize()) {
            return;
        }

        UniversalFluidItemStorage from = getItemFluidStorage(input);
        if (from == null) {
            return;
        }

        amount = Math.min(amount, to.getTankCapacity(tank) - to.getFluidInTank(tank).getAmount());
        if (amount <= 0) {
            return;
        }

        ItemStack inputBackup = input.copy();
        FluidStack fluidMoved = moveFluid(from, to, from.getFluidInTank(tank).copyWithAmount(amount));
        if (fluidMoved.isEmpty()) {
            return;
        }

        // Single-slot tanks (input == output) keep the emptied container in place.
        if (slot == remainingItemSlot) {
            return;
        }

        // Deposit the *actual* emptied container (which may be a different item than the input, e.g. a
        // bucket) into the output slot. If it can't be deposited (slot holds a different item, or is
        // full), roll the drain back so nothing is consumed or destroyed.
        ItemStack emptied = from.getContainer().copy();
        if (addToSlot(container, remainingItemSlot, emptied)) {
            container.setItem(slot, ItemStack.EMPTY);
        }
        else {
            to.drain(fluidMoved.copy(), false);
            container.setItem(slot, inputBackup);
        }
    }

    /**
     * Deposits {@code stack} into {@code slot}, stacking onto a matching item and respecting the max
     * stack size. Returns {@code true} if it was deposited (the slot was updated); {@code false} if the
     * slot holds a different item or has no room, in which case nothing changes.
     */
    public static boolean addToSlot(Container container, int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        ItemStack existing = container.getItem(slot);
        if (existing.isEmpty()) {
            container.setItem(slot, stack.copy());
            return true;
        }

        if (existing.is(stack.getItem())
                && existing.getCount() + stack.getCount() <= existing.getMaxStackSize()) {
            container.setItem(slot, existing.copyWithCount(existing.getCount() + stack.getCount()));
            return true;
        }

        return false;
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

    private static FluidStack moveFluidIgnoringDrainLimit(UniversalFluidStorage from, UniversalFluidStorage to, FluidStack stack) {
        if (stack.isEmpty() || !(from instanceof SingleFluidStorage single)) {
            return moveFluid(from, to, stack);
        }

        FluidStack inserted = FluidStack.create(stack, to.fill(single.drainWithoutLimits(stack, true), true));

        if (inserted.isEmpty()) {
            return FluidStack.empty();
        }

        single.drainWithoutLimits(inserted.copy(), false);
        to.fill(inserted.copy(), false);

        return inserted;
    }

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
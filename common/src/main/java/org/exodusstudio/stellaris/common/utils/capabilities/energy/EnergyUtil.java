package org.exodusstudio.stellaris.common.utils.capabilities.energy;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.transport.Transport;
import org.exodusstudio.stellaris.common.transport.TransportMedium;

import java.util.*;

public class EnergyUtil {

    private static final List<Direction> ALL_DIRECTIONS = List.of(Direction.values());

    public static int moveEnergyToItem(UniversalEnergyStorage from, ItemStack stackTo, int amount) {
        if (stackTo.isEmpty()) return 0;
        UniversalEnergyStorage to = Capabilities.Energy.ITEM.getCapability(stackTo);
        if (to == null) {
            return 0;
        }
        return moveEnergy(from, to, amount);
    }

    public static int moveEnergyFromItem(UniversalEnergyStorage to, ItemStack stackFrom, int amount) {
        if (stackFrom.isEmpty()) return 0;
        UniversalEnergyStorage from = Capabilities.Energy.ITEM.getCapability(stackFrom);
        if (from == null) {
            return 0;
        }
        return moveEnergy(from, to, amount);
    }

    public static void distributeEnergyNearby(Level level, BlockPos pos, int amount) {
        distributeEnergyNearby(level, pos, amount, null);
    }

    /**
     * Pushes up to {@code amount} energy out of the storage exposed on each output direction. A
     * direction backed by a cable routes the energy across the whole connected network (instantly,
     * losslessly) to every storage that accepts it; any other neighbour receives a direct transfer.
     * The actual routing is handled by the shared {@link Transport} engine.
     *
     * @param outputDirections the faces to push from, or {@code null}/empty to try all six.
     */
    public static void distributeEnergyNearby(Level level, BlockPos pos, int amount, List<Direction> outputDirections) {
        if (amount <= 0 || level.isClientSide()) {
            return;
        }

        List<Direction> directions = (outputDirections == null || outputDirections.isEmpty())
                ? ALL_DIRECTIONS : outputDirections;

        Transport.distribute(level, pos, directions, TransportMedium.ENERGY, Transport.energyMover(amount));
    }

    public static int moveEnergy(UniversalEnergyStorage from, UniversalEnergyStorage to, int amount) {
        int inserted = to.insert(from.extract(amount, true), true);

        if (inserted > 0) {
            from.extract(inserted, false);
            to.insert(inserted, false);
        }

        return inserted;
    }
}
package org.exodusstudio.stellaris.common.utils.capabilities.energy;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.CableBlock;
import org.exodusstudio.stellaris.common.blocks.PumpjackProxyBlock;
import org.exodusstudio.stellaris.common.transport.TransportGraph;
import org.exodusstudio.stellaris.common.transport.TransportType;

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
     * direction backed by a {@link CableBlock} routes the energy across the whole connected network
     * (instantly, losslessly) to every storage that accepts it; any other neighbour receives a
     * direct transfer.
     *
     * @param outputDirections the faces to push from, or {@code null}/empty to try all six.
     */
    public static void distributeEnergyNearby(Level level, BlockPos pos, int amount, List<Direction> outputDirections) {
        if (amount <= 0 || level.isClientSide()) {
            return;
        }

        List<Direction> directions = (outputDirections == null || outputDirections.isEmpty())
                ? ALL_DIRECTIONS : outputDirections;

        for (Direction direction : directions) {
            UniversalEnergyStorage from = getEnergyCapability(level, pos, direction);
            if (from == null || !from.canExtractEnergy()) {
                continue;
            }

            int budget = from.extract(amount, true);
            if (budget <= 0) {
                continue;
            }

            BlockPos neighbor = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighbor);

            if (TransportType.ENERGY.isNode(neighborState)) {
                routeToNetwork(level, pos, from, budget, TransportGraph.get(level, neighbor, TransportType.ENERGY));
            } else {
                UniversalEnergyStorage to = getEnergyCapability(level, neighbor, direction.getOpposite());
                if (to != null && to.canInsertEnergy()) {
                    moveEnergy(from, to, budget);
                }
            }
        }
    }

    /**
     * Distributes up to {@code budget} energy from {@code from} across every boundary storage of
     * {@code network} that accepts it, capped by the network's remaining per-tick throughput. The
     * remainder of an uneven split is carried forward so nothing is lost.
     */
    private static void routeToNetwork(Level level, BlockPos sourcePos, UniversalEnergyStorage from, int budget, TransportGraph.Network network) {
        if (network.throughputRemaining <= 0) {
            return;
        }

        List<UniversalEnergyStorage> sinks = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        seen.add(sourcePos); // never push back into the producer

        for (TransportGraph.BoundaryFace face : network.boundary) {
            if (!seen.add(face.pos())) {
                continue;
            }
            UniversalEnergyStorage to = getEnergyCapability(level, face.pos(), face.side());
            if (to == null || !to.canInsertEnergy() || to.insert(budget, true) <= 0) {
                continue;
            }
            sinks.add(to);
        }

        if (sinks.isEmpty()) {
            return;
        }

        long remaining = Math.min(budget, network.throughputRemaining);
        int count = sinks.size();
        for (int i = 0; i < count && remaining > 0; i++) {
            long share = remaining / (count - i);
            if (share <= 0) {
                share = remaining;
            }
            int moved = moveEnergy(from, sinks.get(i), (int) Math.min(share, Integer.MAX_VALUE));
            remaining -= moved;
            network.throughputRemaining -= moved;
        }
    }

    public static int moveEnergy(UniversalEnergyStorage from, UniversalEnergyStorage to, int amount) {
        int inserted = to.insert(from.extract(amount, true), true);

        if (inserted > 0) {
            from.extract(inserted, false);
            to.insert(inserted, false);
        }

        return inserted;
    }

    private static UniversalEnergyStorage getEnergyCapability(Level level, BlockPos pos, Direction direction) {
        UniversalEnergyStorage direct = Capabilities.Energy.BLOCK.getCapability(level, pos, direction);

        if (direct != null) {
            return direct;
        }

        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof PumpjackProxyBlock) {
            BlockPos mainPos = PumpjackProxyBlock.getMainPos(pos, state);
            return Capabilities.Energy.BLOCK.getCapability(level, mainPos, direction);
        }

        return null;
    }
}
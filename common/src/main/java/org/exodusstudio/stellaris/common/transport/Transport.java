package org.exodusstudio.stellaris.common.transport;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.PumpjackProxyBlock;

import java.util.*;

/**
 * The single, generic distribution engine shared by fluids and energy. It replaces the duplicated
 * routing that used to live in {@code FluidUtil} and {@code EnergyUtil}: a producer pushes a resource
 * out of a face, and depending on the neighbour the resource is either flooded across a whole
 * {@link TransportGraph} network or handed directly to an adjacent storage. Everything is expressed in
 * {@code long} amounts through a {@link Mover}, which closes over the concrete resource (a specific
 * fluid, or energy) so this class never needs to know which one it is moving.
 */
@Deprecated(forRemoval = true)
public final class Transport {

    private Transport() {
    }

    /**
     * Resource-specific strategy bound to one in-flight resource (a fixed fluid, or energy). All
     * amounts are {@code long}; the implementation maps them onto {@link UniversalFluidStorage} /
     * {@link UniversalEnergyStorage} operations.
     */
    public interface Mover<S> {
        /** Whether {@code storage} can give up this resource. */
        boolean canExtract(S storage);

        /** Whether {@code storage} can accept this resource. */
        boolean canInsert(S storage);

        /** Simulated amount currently available to pull out of {@code from}. */
        long extractable(S from);

        /** Moves up to {@code amount} from {@code from} into {@code to}; returns the amount actually moved. */
        long move(S from, S to, long amount, boolean simulate);
    }

    /**
     * Pushes the resource exposed on each of {@code directions} out of {@code pos}. A neighbour that is
     * a network connector floods the whole connected network; any other neighbour receives a direct
     * transfer.
     */
    public static <S> void distribute(Level level, BlockPos pos, List<Direction> directions,
                                      TransportMedium<S> medium, Mover<S> mover) {
        if (level.isClientSide()) {
            return;
        }

        for (Direction direction : directions) {
            BlockPos neighbor = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighbor);
            boolean isNode = medium.isNode(neighborState);

            // Resolve the sink before calling extractable() so we never pay the cost
            // of a drain-simulate when there is nothing on the other side to receive.
            S directTo = null;
            if (!isNode) {
                directTo = capability(level, neighbor, direction.getOpposite(), medium);
                if (directTo == null || !mover.canInsert(directTo)) {
                    continue;
                }
            }

            S from = capability(level, pos, direction, medium);
            if (from == null || !mover.canExtract(from)) {
                continue;
            }
            long budget = mover.extractable(from);
            if (budget <= 0) {
                continue;
            }

            if (isNode) {
                spreadAcrossNetwork(level, pos, from, mover, medium,
                        TransportGraph.get(level, neighbor, medium), false);
            } else {
                mover.move(from, directTo, budget, false);
            }
        }
    }

    /**
     * Spreads what can be pulled from {@code from} across every boundary sink of {@code network} that
     * accepts it, capped by the network's remaining per-tick throughput. The remainder of an uneven
     * split is carried forward so nothing is lost. Returns the total moved.
     * <p>
     * Used both by producer pushes (from a machine tank) and by the passthrough capability a pipe /
     * cable exposes to other mods (from a temporary source holding the incoming resource).
     *
     * @param exclude positions that must never be used as sinks (e.g. the external pusher's face).
     */
    @SafeVarargs
    public static <S> long spreadAcrossNetwork(Level level, BlockPos sourcePos, S from, Mover<S> mover,
                                               TransportMedium<S> medium, TransportGraph.Network network,
                                               boolean simulate, BlockPos... exclude) {
        if (network.throughputRemaining <= 0) {
            return 0L;
        }

        List<S> sinks = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        seen.add(sourcePos); // never push back into the producer
        seen.addAll(Arrays.asList(exclude));

        for (TransportGraph.BoundaryFace face : network.boundary) {
            if (!seen.add(face.pos())) {
                continue;
            }
            S to = capability(level, face.pos(), face.side(), medium);
            if (to == null || !mover.canInsert(to) || mover.move(from, to, mover.extractable(from), true) <= 0) {
                continue;
            }
            sinks.add(to);
        }

        if (sinks.isEmpty()) {
            return 0L;
        }

        long remaining = Math.min(mover.extractable(from), network.throughputRemaining);
        long total = 0L;
        int count = sinks.size();
        for (int i = 0; i < count && remaining > 0; i++) {
            long share = remaining / (count - i);
            if (share <= 0) {
                share = remaining;
            }
            long moved = mover.move(from, sinks.get(i), share, simulate);
            remaining -= moved;
            total += moved;
            if (!simulate) {
                network.throughputRemaining -= moved;
            }
        }
        return total;
    }

    /**
     * Resolves {@code medium}'s capability at a face, falling back through a pumpjack proxy.
     * <p>
     * When {@code medium} has a {@code rawGet} provider, that is tried first. This bypasses the
     * NeoForge capability wrapper ({@code UniversalFluidHandler → NeoForgeFluidStorage}) whose
     * simulate operations call {@code drain/fill(false)} inside a transaction, but our storage
     * classes don't register rollback callbacks with the transaction, so the rollback is a no-op
     * and every simulate check permanently mutates the tank. The raw provider returns the
     * underlying {@code SingleFluidStorage} / {@code MultipleFluidStorage} directly, where
     * {@code drain/fill(simulate=true)} is correctly side-effect-free.
     */
    public static <S> S capability(Level level, BlockPos pos, Direction direction, TransportMedium<S> medium) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && medium.rawGet != null) {
            S raw = medium.rawGet.apply(be, direction);
            if (raw != null) return raw;
        }

        S direct = medium.capability.getCapability(level, pos, direction);
        if (direct != null) {
            return direct;
        }

        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof PumpjackProxyBlock) {
            BlockPos mainPos = PumpjackProxyBlock.getMainPos(pos, state);
            if (medium.rawGet != null) {
                BlockEntity mainBe = level.getBlockEntity(mainPos);
                if (mainBe != null) {
                    S raw = medium.rawGet.apply(mainBe, direction);
                    if (raw != null) return raw;
                }
            }
            return medium.capability.getCapability(level, mainPos, direction);
        }
        return null;
    }

    /** A {@link Mover} for one specific {@code fluid} (its fluid type fixes what flows; amount is the cap). */
    public static Mover<UniversalFluidStorage> fluidMover(FluidStack fluid) {
        return new Mover<>() {
            @Override
            public boolean canExtract(UniversalFluidStorage storage) {
                return true;
            }

            @Override
            public boolean canInsert(UniversalFluidStorage storage) {
                return true;
            }

            @Override
            public long extractable(UniversalFluidStorage from) {
                return from.drain(fluid, true).getAmount();
            }

            @Override
            public long move(UniversalFluidStorage from, UniversalFluidStorage to, long amount, boolean simulate) {
                if (amount <= 0) {
                    return 0L;
                }
                FluidStack drained = from.drain(fluid.copyWithAmount(amount), true);
                if (drained.isEmpty()) {
                    return 0L;
                }
                long movable = to.fill(drained, true);
                if (movable <= 0) {
                    return 0L;
                }
                if (!simulate) {
                    FluidStack moved = fluid.copyWithAmount(movable);
                    from.drain(moved, false);
                    to.fill(moved, false);
                }
                return movable;
            }
        };
    }

    /** A {@link Mover} for energy, capped at {@code request} per push. */
    public static Mover<UniversalEnergyStorage> energyMover(int request) {
        return new Mover<>() {
            @Override
            public boolean canExtract(UniversalEnergyStorage storage) {
                return storage.canExtractEnergy();
            }

            @Override
            public boolean canInsert(UniversalEnergyStorage storage) {
                return storage.canInsertEnergy();
            }

            @Override
            public long extractable(UniversalEnergyStorage from) {
                return from.extract(request, true);
            }

            @Override
            public long move(UniversalEnergyStorage from, UniversalEnergyStorage to, long amount, boolean simulate) {
                int budget = (int) Math.min(amount, Integer.MAX_VALUE);
                int movable = to.insert(from.extract(budget, true), true);
                if (movable <= 0) {
                    return 0L;
                }
                if (!simulate) {
                    from.extract(movable, false);
                    to.insert(movable, false);
                }
                return movable;
            }
        };
    }
}

package org.exodusstudio.stellaris.common.transport;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.capabilities.types.NoProviderBlockCapabilityHolder;
import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.CableBlock;
import org.exodusstudio.stellaris.common.blocks.PipeBlock;

import java.util.function.Predicate;
import java.util.function.ToLongFunction;

/**
 * Describes one kind of transport network in a generic, capability-driven way. It replaces the old
 * {@code TransportType} enum: instead of hard switching between {@code FLUID} and {@code ENERGY}, a
 * medium pairs three things so the same {@link TransportGraph} and {@link Transport} code works for
 * any resource backed by a Potentials capability:
 * <ul>
 *     <li>{@link #capability} – the universal block capability used to resolve a storage at any face
 *     (works across loaders and with other mods);</li>
 *     <li>{@link #isNode} – whether a block is a connector of this network (a pipe / cable);</li>
 *     <li>{@link #throughput} – the per-tick throughput a connector contributes (network bottleneck
 *     is the minimum across its members).</li>
 * </ul>
 * Adding a new medium (or a new connector block) no longer means editing an enum.
 *
 * @param <S> the storage type the capability exposes ({@link UniversalFluidStorage} / {@link UniversalEnergyStorage})
 */
public final class TransportMedium<S> {

    public final NoProviderBlockCapabilityHolder<S, Direction> capability;
    private final Predicate<BlockState> isNode;
    private final ToLongFunction<BlockState> throughput;

    public TransportMedium(NoProviderBlockCapabilityHolder<S, Direction> capability,
                           Predicate<BlockState> isNode,
                           ToLongFunction<BlockState> throughput) {
        this.capability = capability;
        this.isNode = isNode;
        this.throughput = throughput;
    }

    /** Whether {@code state} is a connector of this network. */
    public boolean isNode(BlockState state) {
        return isNode.test(state);
    }

    /** Per-tick throughput {@code state} contributes as a connector, or {@code 0} if it is not one. */
    public long throughput(BlockState state) {
        return throughput.applyAsLong(state);
    }

    public static final TransportMedium<UniversalFluidStorage> FLUID = new TransportMedium<>(
            Capabilities.Fluid.BLOCK,
            state -> state.getBlock() instanceof PipeBlock,
            state -> state.getBlock() instanceof PipeBlock pipe ? pipe.maxOut : 0L);

    public static final TransportMedium<UniversalEnergyStorage> ENERGY = new TransportMedium<>(
            Capabilities.Energy.BLOCK,
            state -> state.getBlock() instanceof CableBlock,
            state -> state.getBlock() instanceof CableBlock cable ? cable.transferRate : 0L);
}

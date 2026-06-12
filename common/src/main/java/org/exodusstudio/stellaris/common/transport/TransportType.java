package org.exodusstudio.stellaris.common.transport;

import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.CableBlock;
import org.exodusstudio.stellaris.common.blocks.PipeBlock;

/**
 * Distinguishes the two kinds of transport network: fluid (carried by {@link PipeBlock}) and
 * energy (carried by {@link CableBlock}).
 * <p>
 * A transport node is a bufferless connector. Producers push a resource into an adjacent node and
 * {@link TransportGraph} routes it across the whole connected network to every valid sink in a
 * single tick. The type only needs to answer two questions about a block: is it a node of this
 * network, and what per-tick throughput does it contribute (the network bottleneck is the minimum
 * across its members).
 */
public enum TransportType {
    FLUID,
    ENERGY;

    public boolean isNode(BlockState state) {
        return switch (this) {
            case FLUID -> state.getBlock() instanceof PipeBlock;
            case ENERGY -> state.getBlock() instanceof CableBlock;
        };
    }

    public long throughput(BlockState state) {
        return switch (this) {
            case FLUID -> state.getBlock() instanceof PipeBlock pipe ? pipe.maxOut : 0L;
            case ENERGY -> state.getBlock() instanceof CableBlock cable ? cable.transferRate : 0L;
        };
    }
}

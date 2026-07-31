package org.exodusstudio.stellaris.common.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Computes connected transport networks on demand and caches them for the duration of a single
 * server tick.
 * <p>
 * A {@link Network} is the set of connected node positions plus the list of boundary faces where the
 * network touches a non-node block (a machine, tank, etc.). The producer that pushes a resource into
 * the network resolves the actual storage capability at each boundary face itself, since that lookup
 * is resource specific (fluid vs energy).
 * <p>
 * Networks are never persisted: they are cheap to rebuild and always reflect the current world.
 * The per-tick cache guarantees each network is flooded at most once per tick no matter how many
 * producers push into it. All access happens on the single server thread.
 */
@Deprecated(forRemoval = true)
public final class TransportGraph {

    /** Hard cap so a pathological build can never freeze the server. */
    private static final int MAX_NODES = 4096;
    private static final Direction[] DIRECTIONS = Direction.values();

    private TransportGraph() {
    }

    /** A face of the network that borders a non-node block. */
    public record BoundaryFace(BlockPos pos, Direction side) {
    }

    public static final class Network {
        public final Set<BlockPos> nodes;
        public final Set<BoundaryFace> boundary;
        public final long throughput;
        /** Decremented as resource is pushed in this tick; shared across producers and directions. */
        public long throughputRemaining;

        private Network(Set<BlockPos> nodes, Set<BoundaryFace> boundary, long throughput) {
            this.nodes = nodes;
            this.boundary = boundary;
            this.throughput = throughput;
            this.throughputRemaining = throughput;
        }
    }

    private static Level cachedLevel = null;
    private static long cachedTick = Long.MIN_VALUE;
    private static final java.util.Map<BlockPos, Network> CACHE = new java.util.HashMap<>();

    /**
     * Returns the network containing {@code nodePos}, building (and caching) it if necessary.
     * {@code nodePos} must be a node of {@code medium}.
     */
    public static Network get(Level level, BlockPos nodePos, TransportMedium<?> medium) {
        if (level != cachedLevel || level.getGameTime() != cachedTick) {
            cachedLevel = level;
            cachedTick = level.getGameTime();
            CACHE.clear();
        }

        Network cached = CACHE.get(nodePos);
        if (cached != null) {
            return cached;
        }

        Network network = build(level, nodePos, medium);
        for (BlockPos member : network.nodes) {
            CACHE.put(member, network);
        }
        return network;
    }

    private static Network build(Level level, BlockPos start, TransportMedium<?> medium) {
        Set<BlockPos> nodes = new HashSet<>();
        Set<BoundaryFace> boundary = new LinkedHashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        long throughput = Long.MAX_VALUE;

        BlockPos startImmutable = start.immutable();
        nodes.add(startImmutable);
        queue.add(startImmutable);

        while (!queue.isEmpty() && nodes.size() <= MAX_NODES) {
            BlockPos current = queue.poll();
            throughput = Math.min(throughput, medium.throughput(level.getBlockState(current)));

            for (Direction direction : DIRECTIONS) {
                BlockPos neighbor = current.relative(direction);

                // Never force-load chunks; an unloaded neighbour simply isn't part of the network.
                if (!level.isLoaded(neighbor)) {
                    continue;
                }

                BlockState neighborState = level.getBlockState(neighbor);
                if (medium.isNode(neighborState)) {
                    BlockPos immutable = neighbor.immutable();
                    if (nodes.add(immutable)) {
                        queue.add(immutable);
                    }
                } else {
                    boundary.add(new BoundaryFace(neighbor.immutable(), direction.getOpposite()));
                }
            }
        }

        if (throughput == Long.MAX_VALUE) {
            throughput = 0L;
        }
        return new Network(nodes, boundary, throughput);
    }
}

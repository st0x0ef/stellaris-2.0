package org.exodusstudio.stellaris.common.networks;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.exodusstudio.stellaris.common.blocks.cables.BaseCableLikeBlock;
import org.exodusstudio.stellaris.common.blocks.cables.ConnectionMode;

import java.util.*;

public abstract class Network {

    private final UUID id;
    private final HashSet<BlockPos> cables;
    private boolean invalid = false;
    private Runnable markDirtyCallback;

    protected final List<NetworkEndpoint> cachedEndpoints = new ArrayList<>();

    public Network(UUID id, HashSet<BlockPos> cables) {
        this.id = id;
        this.cables = cables;
    }
    public Network(UUID id) {
        this.id = id;
        this.cables = new HashSet<>();
    }
    public Network() {
        this(UUID.randomUUID());
    }

    public void setMarkDirtyCallback(Runnable callback) {
        this.markDirtyCallback = callback;
    }

    protected void markDirty() {
        if (this.markDirtyCallback != null)
            this.markDirtyCallback.run();
    }

    public void addCable(BlockPos pos) {
        if (cables.add(pos))
            markDirty();
    }

    public void removeCable(BlockPos pos) {
        if (cables.remove(pos))
            markDirty();
    }

    public void invalidate() {
        this.invalid = true;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public UUID id() {
        return id;
    }
    public HashSet<BlockPos> cables() {
        return cables;
    }
    public List<NetworkEndpoint> getEndpoints() {
        return cachedEndpoints;
    }

    public void updateEndpoint(BlockPos cablePos, Direction dir, ConnectionMode newMode) {
        // 1. Always purge any existing endpoint for this cable and direction
        cachedEndpoints.removeIf(e -> e.cablePos().equals(cablePos) && e.direction() == dir);

        // 2. Only add if the mode is PUSH or PULL
        if (newMode == ConnectionMode.PUSH || newMode == ConnectionMode.PULL) {
            BlockPos targetPos = cablePos.relative(dir);

            // 3. Ensure target is an external block, not a cable inside this network
            if (!cables.contains(targetPos)) {
                cachedEndpoints.add(new NetworkEndpoint(cablePos, targetPos, dir, newMode));
            }
        }
    }

    public void removeCableEndpoints(BlockPos cablePos) {
        cachedEndpoints.removeIf(e -> e.cablePos().equals(cablePos));
    }

    public void rebuildEndpoints(ServerLevel level) {
        this.cachedEndpoints.clear();

        for (BlockPos cablePos : cables) {
            BlockState state = level.getBlockState(cablePos);

            if (!(state.getBlock() instanceof BaseCableLikeBlock)) continue;

            for (Direction dir : Direction.values()) {
                EnumProperty<ConnectionMode> modeProp = BaseCableLikeBlock.MODE_BY_DIRECTION.get(dir);

                if (state.hasProperty(modeProp)) {
                    ConnectionMode mode = state.getValue(modeProp);

                    // Strictly PUSH or PULL only
                    if (mode == ConnectionMode.PUSH || mode == ConnectionMode.PULL) {
                        BlockPos targetPos = cablePos.relative(dir);

                        if (!cables.contains(targetPos)) {
                            this.cachedEndpoints.add(new NetworkEndpoint(cablePos, targetPos, dir, mode));
                        }
                    }
                }
            }
        }
    }

    public abstract Codec<? extends Network> getCodec();
    public abstract <N extends Network> boolean canMergeWith(N other);
    public abstract void tick(ServerLevel level);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return id.equals(network.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

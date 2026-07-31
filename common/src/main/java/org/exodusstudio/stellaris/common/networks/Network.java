package org.exodusstudio.stellaris.common.networks;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public abstract class Network {

    private final UUID id;
    private final HashSet<BlockPos> cables;
    private boolean invalid = false;
    private Runnable markDirtyCallback;

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

    public abstract Codec<? extends Network> getCodec();
    public abstract <N extends Network> boolean canMergeWith(N other);

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

package org.exodusstudio.stellaris.common.blocks.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.networks.Network;
import org.exodusstudio.stellaris.common.networks.NetworkManager;
import org.exodusstudio.stellaris.common.networks.NetworkProvider;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class NetworkBlockEntity<N extends Network> extends BlockEntity implements NetworkProvider<N> {

    protected UUID uuid;
    private transient N cachedNetwork;

    public NetworkBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    public abstract SavedDataType<NetworkManager<N>> getNetworkDataType();

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        // Reads network UUID on server load
        input.read("network", UUIDUtil.CODEC).ifPresent(uuid -> this.uuid = uuid);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.uuid != null)
            output.store("network", UUIDUtil.CODEC, uuid);
    }

    @Override
    public @Nullable N getNetwork(@Nullable Direction dir) {
        // FAST PATH: Return cached reference directly if still valid
        if (cachedNetwork != null && !cachedNetwork.isInvalid()) {
            return cachedNetwork;
        }

        // SLOW PATH: Cache miss or invalidated network -> fetch from Level Data
        if (uuid == null || level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        NetworkManager<N> manager = serverLevel.getDataStorage().computeIfAbsent(getNetworkDataType());

        // 1. Try UUID lookup
        N network = manager.getNetworks().get(this.uuid);

        // 2. Self-healing fallback if UUID was stale from an unloaded chunk merge
        if (network == null) {
            network = manager.getNetworkAt(this.worldPosition);
            if (network != null) {
                this.setUuid(network.id());
            }
        }

        // Cache the result for future ticks!
        this.cachedNetwork = network;
        return this.cachedNetwork;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        this.cachedNetwork = null;
        setChanged();
    }

}

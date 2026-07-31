package org.exodusstudio.stellaris.common.blocks.cables;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.ValueInput;
import org.exodusstudio.stellaris.common.networks.NetworkManager;
import org.exodusstudio.stellaris.common.networks.capabilities.FluidNetwork;
import org.exodusstudio.stellaris.common.networks.capabilities.NetworkFluidStorage;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidPipeBlockEntity extends NetworkBlockEntity<FluidNetwork> implements FluidProvider.BLOCK {

    private transient NetworkFluidStorage clientDummyTank;

    public FluidPipeBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntitiesRegistry.PIPE_ENTITY.get(), worldPosition, blockState);
    }

    @Override
    public SavedDataType<NetworkManager<FluidNetwork>> getNetworkDataType() {
        return NetworkManager.FLUID_DATA_TYPE;
    }

    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        if (level != null && level.isClientSide()) return clientDummyTank;
        FluidNetwork network = getNetwork(direction);
        return network != null ? network.tank() : null;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        // Safely reads synced stats for client dummy storage (No level check required!)
        input.read("client_fluidstack", FluidAmountMapDataComponent.CODEC).ifPresent(component -> {
            if (this.clientDummyTank == null) this.clientDummyTank = new NetworkFluidStorage();
            this.clientDummyTank.setFluidInTank(component.getAsFluidStack(0));
        });

        input.read("client_capacity", Codec.LONG).ifPresent(capacity -> {
            if (this.clientDummyTank == null) this.clientDummyTank = new NetworkFluidStorage();
            this.clientDummyTank.setCapacity(capacity);
        });
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);

        // Runs on the SERVER when creating network sync packets
        FluidNetwork network = getNetwork(null);
        if (network != null) {
            tag.store("client_fluidstack", FluidAmountMapDataComponent.CODEC, FluidAmountMapDataComponent.create(List.of(network.getFluidStack())));
            tag.putLong("client_capacity", network.tank().getTankCapacity(0));
        }
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

package org.exodusstudio.stellaris.common.blocks.cables;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.providers.EnergyProvider;
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
import org.exodusstudio.stellaris.common.networks.capabilities.EnergyNetwork;
import org.exodusstudio.stellaris.common.networks.capabilities.NetworkEnergyStorage;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.Nullable;

public class EnergyCableBlockEntity extends NetworkBlockEntity<EnergyNetwork> implements EnergyProvider.BLOCK {

    private transient NetworkEnergyStorage clientDummyEnergy;

    public EnergyCableBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntitiesRegistry.CABLE_ENTITY.get(), worldPosition, blockState);
    }

    @Override
    public @Nullable UniversalEnergyStorage getEnergy(@Nullable Direction direction) {
        if (level != null && level.isClientSide()) return clientDummyEnergy;
        EnergyNetwork network = getNetwork(direction);
        return network != null ? network.energy() : null;
    }

    @Override
    public SavedDataType<NetworkManager<EnergyNetwork>> getNetworkDataType() {
        return NetworkManager.ENERGY_DATA_TYPE;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        // Safely reads synced stats for client dummy storage (No level check required!)
        input.read("client_energy", Codec.INT).ifPresent(energy -> {
            if (this.clientDummyEnergy == null) this.clientDummyEnergy = new NetworkEnergyStorage();
            this.clientDummyEnergy.setEnergyStored(energy);
        });

        input.read("client_capacity", Codec.INT).ifPresent(capacity -> {
            if (this.clientDummyEnergy == null) this.clientDummyEnergy = new NetworkEnergyStorage();
            this.clientDummyEnergy.setCapacity(capacity);
        });
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);

        // Runs on the SERVER when creating network sync packets
        EnergyNetwork network = getNetwork(null);
        if (network != null) {
            tag.putInt("client_energy", network.energy().getEnergy());
            tag.putInt("client_capacity", network.energy().getMaxEnergy());
        }
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

//    public UUID getUuid() {
//        return uuid;
//    }
}

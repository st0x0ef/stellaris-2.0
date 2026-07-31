package org.exodusstudio.stellaris.common.networks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.exodusstudio.stellaris.common.networks.capabilities.EnergyNetwork;
import org.exodusstudio.stellaris.common.networks.capabilities.FluidNetwork;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

public class NetworkManager<T extends Network> extends SavedData {

    public static final SavedDataType<NetworkManager<EnergyNetwork>> ENERGY_DATA_TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(MOD_ID,"energy_networks"), NetworkManager::new, createCodec(EnergyNetwork.CODEC), DataFixTypes.LEVEL);

    public static final SavedDataType<NetworkManager<FluidNetwork>> FLUID_DATA_TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(MOD_ID,"fluid_networks"), NetworkManager::new, createCodec(FluidNetwork.CODEC), DataFixTypes.LEVEL);


    private final Map<UUID, T> networks;

    public NetworkManager() {
        this.networks = new HashMap<>();
    }

    public Map<UUID, T> getNetworks() {
        return networks;
    }

    public void removeNetwork(UUID id) {
        T removed = this.networks.remove(id);
        if (removed != null) {
            removed.invalidate(); // Instantly breaks direct cached references!
            this.setDirty();
        }
    }

    public void addNetwork(T network) {
        network.setMarkDirtyCallback(this::setDirty);
        this.networks.put(network.id(), network);
        this.setDirty();
    }

    public @Nullable T getNetworkAt(BlockPos pos) {
        for (T network : networks.values())
            if (network.cables().contains(pos))
                return network;

        return null;
    }

    public static <T extends Network> NetworkManager<T> load(Map<UUID, T> networks) {
        NetworkManager<T> manager = new NetworkManager<>();
        // Attach the dirty listener to all loaded networks from disk
        for (T network : networks.values()) {
            network.setMarkDirtyCallback(manager::setDirty);
            manager.networks.put(network.id(), network);
        }

        return manager;
    }

    public static <T extends Network> Codec<NetworkManager<T>> createCodec(Codec<T> networkCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                // Use a String-based key codec for the unboundedMap!
                Codec.unboundedMap(Codec.STRING.xmap(UUID::fromString, UUID::toString), networkCodec)
                        .fieldOf("networks")
                        .forGetter(NetworkManager::getNetworks)
        ).apply(instance, NetworkManager::load));
    }

}

package org.exodusstudio.stellaris.common.network;

import dev.architectury.impl.NetworkAggregator;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.network.packets.SyncEnergyPacket;
import org.exodusstudio.stellaris.common.network.packets.SyncEnergyPacketWithoutDirection;

import java.util.Collections;
import java.util.List;

public interface NetworkRegistry {
    static void init() {
        registerS2C(SyncEnergyPacket.TYPE, SyncEnergyPacket.STREAM_CODEC, SyncEnergyPacket::handle);
        registerS2C(SyncEnergyPacketWithoutDirection.TYPE, SyncEnergyPacketWithoutDirection.STREAM_CODEC, SyncEnergyPacketWithoutDirection::handle);
    }

    static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> packetType, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> receiver) {
        if (Platform.getEnvironment().equals(Env.SERVER)) {
            NetworkAggregator.registerS2CType(packetType, codec, List.of());
        } else {
            NetworkAggregator.registerReceiver(NetworkManager.s2c(), packetType, codec, Collections.emptyList(), receiver);
        }
    }

    static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> packetType, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> receiver) {
        NetworkAggregator.registerReceiver(NetworkManager.c2s(), packetType, codec, Collections.emptyList(), receiver);
    }
}

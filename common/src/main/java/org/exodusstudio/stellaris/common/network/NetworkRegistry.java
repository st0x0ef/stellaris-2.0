package org.exodusstudio.stellaris.common.network;

import dev.architectury.impl.NetworkAggregator;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.network.packets.OpenScreenPacket;

import java.util.Collections;
import java.util.List;

public interface NetworkRegistry {

    CustomPacketPayload.Type<OpenScreenPacket> OPEN_SCREEN_PACKET_TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, "open_screen"));

    static void init() {
        registerS2C(OPEN_SCREEN_PACKET_TYPE, OpenScreenPacket.STREAM_CODEC, OpenScreenPacket::handle);
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

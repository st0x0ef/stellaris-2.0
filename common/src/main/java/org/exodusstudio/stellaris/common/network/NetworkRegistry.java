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
import org.exodusstudio.stellaris.common.network.packets.*;

import java.util.Collections;
import java.util.List;

public interface NetworkRegistry {

    CustomPacketPayload.Type<OpenScreenPacket> OPEN_SCREEN_PACKET_TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, "open_screen"));
    CustomPacketPayload.Type<OpenMenuPacket> OPEN_MENU_PACKET_TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, "open_menu"));

    static void init() {
        registerC2S(OPEN_MENU_PACKET_TYPE, OpenMenuPacket.STREAM_CODEC, OpenMenuPacket::handle);
        registerC2S(OpenRocketMenuPacket.TYPE, OpenRocketMenuPacket.STREAM_CODEC, OpenRocketMenuPacket::handle);

        registerS2C(SyncFluidPacket.TYPE, SyncFluidPacket.STREAM_CODEC, SyncFluidPacket::handle);
        registerS2C(OPEN_SCREEN_PACKET_TYPE, OpenScreenPacket.STREAM_CODEC, OpenScreenPacket::handle);
        registerS2C(SyncRocketModule.TYPE, SyncRocketModule.STREAM_CODEC, SyncRocketModule::handle);
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

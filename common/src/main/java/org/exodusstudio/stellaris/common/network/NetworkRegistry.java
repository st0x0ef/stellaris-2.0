package org.exodusstudio.stellaris.common.network;

import dev.architectury.impl.NetworkAggregator;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.network.packets.*;

import java.util.Collections;
import java.util.List;

public interface NetworkRegistry {

    CustomPacketPayload.Type<OpenMenuPacket> OPEN_MENU_PACKET_TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, "open_menu"));

    static void init() {
        registerC2S(OPEN_MENU_PACKET_TYPE, OpenMenuPacket.STREAM_CODEC, OpenMenuPacket::handle);
        registerC2S(OpenBlockEntityMenusPacket.TYPE, OpenBlockEntityMenusPacket.STREAM_CODEC, OpenBlockEntityMenusPacket::handle);
        registerC2S(KeyHandlerPacket.TYPE, KeyHandlerPacket.STREAM_CODEC, KeyHandlerPacket::handle);
        registerS2C(SyncOilLevelPacket.TYPE, SyncOilLevelPacket.STREAM_CODEC, SyncOilLevelPacket::handle);
        registerS2C(StartFadePacket.TYPE, StartFadePacket.STREAM_CODEC, StartFadePacket::handle);
        registerC2S(OpenWikiEntry.TYPE, OpenWikiEntry.STREAM_CODEC, OpenWikiEntry::handle);

        registerC2S(OpenRocketMenuPacket.TYPE, OpenRocketMenuPacket.STREAM_CODEC, OpenRocketMenuPacket::handle);
        registerC2S(AntennasOperations.TYPE, AntennasOperations.STREAM_CODEC, AntennasOperations::handle);

        registerS2C(SyncFluidPacket.TYPE, SyncFluidPacket.STREAM_CODEC, SyncFluidPacket::handle);
        registerS2C(SyncFluidPacketWithoutDirection.TYPE, SyncFluidPacketWithoutDirection.STREAM_CODEC, SyncFluidPacketWithoutDirection::handle);
        registerS2C(SyncRocketPacket.TYPE, SyncRocketPacket.STREAM_CODEC, SyncRocketPacket::handle);
        registerS2C(SyncEnergyPacket.TYPE, SyncEnergyPacket.STREAM_CODEC, SyncEnergyPacket::handle);
        registerS2C(SyncEnergyPacketWithoutDirection.TYPE, SyncEnergyPacketWithoutDirection.STREAM_CODEC, SyncEnergyPacketWithoutDirection::handle);
        registerS2C(SyncOutputManager.S2C.TYPE, SyncOutputManager.S2C.STREAM_CODEC, SyncOutputManager::handle);

        registerS2C(SyncPlanetMenuState.TYPE, SyncPlanetMenuState.STREAM_CODEC, SyncPlanetMenuState::handle);
        registerS2C(ParasiteCameraShakePacket.TYPE, ParasiteCameraShakePacket.STREAM_CODEC, ParasiteCameraShakePacket::handle);

        registerS2C(SyncGravityManipulatorDataPacketS2C.TYPE_S2C, SyncGravityManipulatorDataPacketS2C.STREAM_CODEC, SyncGravityManipulatorDataPacketS2C::handle);
        registerC2S(SyncGravityManipulatorDataPacketC2S.TYPE_C2S, SyncGravityManipulatorDataPacketC2S.STREAM_CODEC, SyncGravityManipulatorDataPacketC2S::handle);
        registerC2S(AwardStatPacket.TYPE, AwardStatPacket.STREAM_CODEC, AwardStatPacket::handle);
        registerC2S(TeleportToPlanetPacket.TYPE, TeleportToPlanetPacket.STREAM_CODEC, TeleportToPlanetPacket::handle);
        registerC2S(SyncOutputManager.C2S.TYPE, SyncOutputManager.C2S.STREAM_CODEC, SyncOutputManager::handle);
        registerC2S(SelectPlanetPacket.TYPE, SelectPlanetPacket.STREAM_CODEC, SelectPlanetPacket::handle);

        registerC2S(InfectionResearchPacket.TYPE, InfectionResearchPacket.STREAM_CODEC, InfectionResearchPacket::handle);
        registerC2S(PlanSpaceStationPacket.TYPE, PlanSpaceStationPacket.STREAM_CODEC, PlanSpaceStationPacket::handle);

        registerS2C(RecipeSyncerPacket.TYPE, RecipeSyncerPacket.STREAM_CODEC, RecipeSyncerPacket::handle);
        registerS2C(FuelRefinerySyncerPacket.TYPE, FuelRefinerySyncerPacket.STREAM_CODEC, FuelRefinerySyncerPacket::handle);
        registerS2C(ElectrolyzerSyncerPacket.TYPE, ElectrolyzerSyncerPacket.STREAM_CODEC, ElectrolyzerSyncerPacket::handle);
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

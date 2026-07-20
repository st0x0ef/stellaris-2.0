package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;

public record SyncPlanetsPacket(List<Planet> planets) implements CustomPacketPayload {

    public static final Type<SyncPlanetsPacket> TYPE = new Type<>(IdentifierUtils.id("sync_planets"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlanetsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, Planet.STREAM_CODEC), SyncPlanetsPacket::planets,
            SyncPlanetsPacket::new
    );

    public static void handle(SyncPlanetsPacket packet, NetworkManager.PacketContext context) {
        PlanetsData.setPlanets(packet.planets());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

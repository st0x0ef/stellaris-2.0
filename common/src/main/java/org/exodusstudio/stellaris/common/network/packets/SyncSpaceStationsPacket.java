package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.compats.jei.JEICompat;
import org.exodusstudio.stellaris.common.compats.rei.REICompat;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationData;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Space station recipes are loaded by a server data pack listener, so a connected client has none
 * of them until they are sent over. Without this the planner lists no stations at all, leaving
 * nothing to select and a build button that can never do anything.
 */
public record SyncSpaceStationsPacket(List<SpaceStationRecipe> recipes) implements CustomPacketPayload {

    public static final Type<SyncSpaceStationsPacket> TYPE = new Type<>(IdentifierUtils.id("sync_space_stations"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSpaceStationsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, SpaceStationRecipe.STREAM_CODEC), SyncSpaceStationsPacket::recipes,
            SyncSpaceStationsPacket::new
    );

    public static void handle(SyncSpaceStationsPacket packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            SpaceStationData.SPACE_STATION_RECIPES.clear();
            SpaceStationData.SPACE_STATION_RECIPES.addAll(packet.recipes());

            JEICompat.reloadRecipesSafe();
            REICompat.reloadDisplaysSafe();
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

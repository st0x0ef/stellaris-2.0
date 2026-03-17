package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.TeleportUtil;
import org.exodusstudio.stellaris.common.utils.Utils;

public record TeleportToPlanetPacket(Planet destination) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TeleportToPlanetPacket> TYPE = new CustomPacketPayload.Type<>(IdentifierUtils.id("teleport_to_planet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportToPlanetPacket> STREAM_CODEC = StreamCodec.composite(
            Planet.STREAM_CODEC, TeleportToPlanetPacket::destination,
            TeleportToPlanetPacket::new
    );

    public static void handle(TeleportToPlanetPacket data, NetworkManager.PacketContext context) {
        //TODO: Check if it's not a player/teleport the lander/friends, etc...
        TeleportUtil.teleportToPlanet(context.getPlayer(), data.destination());
        context.getPlayer().closeContainer();
        Utils.stopFade(context.getPlayer());

    }


    @Override
    public Type<TeleportToPlanetPacket> type() {
        return TYPE;
    }
}

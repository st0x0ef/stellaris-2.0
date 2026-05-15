package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
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
        MinecraftServer server = context.getPlayer().level().getServer();
        if (server != null && context.getPlayer().getVehicle() instanceof RocketEntity rocket) {
            TeleportUtil.teleportRocketToPlanet(context.getPlayer(), server.getLevel(ResourceKey.create(Registries.DIMENSION, data.destination.dimension())), rocket, false);
            context.getPlayer().stellaris$setPlanetMenuOpen(false, context.getPlayer(), true);
            Utils.stopFade(context.getPlayer());
            context.getPlayer().closeContainer();
        }
    }


    @Override
    public Type<TeleportToPlanetPacket> type() {
        return TYPE;
    }
}

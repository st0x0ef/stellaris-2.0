package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.TeleportUtil;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.Optional;

public record TeleportToPlanetPacket(Planet destination, Optional<BlockPos> pos, Optional<SpaceStationRecipe> recipe) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TeleportToPlanetPacket> TYPE = new CustomPacketPayload.Type<>(IdentifierUtils.id("teleport_to_planet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportToPlanetPacket> STREAM_CODEC = StreamCodec.composite(
            Planet.STREAM_CODEC, TeleportToPlanetPacket::destination,
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), TeleportToPlanetPacket::pos,
            ByteBufCodecs.optional(SpaceStationRecipe.STREAM_CODEC), TeleportToPlanetPacket::recipe,
            TeleportToPlanetPacket::new
    );

    public static void handle(TeleportToPlanetPacket data, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (!(context.getPlayer() instanceof ServerPlayer player)) {
                return;
            }

            MinecraftServer server = player.level().getServer();

            if (!(player.getVehicle() instanceof RocketEntity rocket)) {
                return;
            }

            ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, data.destination().dimension());
            Planet planet = PlanetsData.getPlanet(dimensionKey);
            if (planet == null) {
                return;
            }

            ServerLevel level = server.getLevel(dimensionKey);
            if (level == null) {
                return;
            }

            BlockPos destPos = data.pos().orElse(player.getOnPos());

            TeleportUtil.teleportRocketToPlanet(player, level, rocket, destPos, false);
            player.stellaris$setPlanetMenuOpen(false, player, true);

            if (data.recipe().isPresent()) {
                if (planet.allowSpaceStation()) {
                    Utils.placeSpaceStation(player, level, data.recipe().get());
                } else {
                    Stellaris.LOG.warn("{} asked for a space station on {}, which does not allow them", player.getGameProfile().name(), planet.dimension());
                }
            }

            Utils.stopFade(player);
            player.closeContainer();
        });
    }

    @Override
    public Type<TeleportToPlanetPacket> type() {
        return TYPE;
    }
}

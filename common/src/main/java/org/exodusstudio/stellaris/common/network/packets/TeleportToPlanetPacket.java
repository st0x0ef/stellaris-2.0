package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationData;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.TeleportUtil;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.Optional;

public record TeleportToPlanetPacket(Planet destination, Optional<BlockPos> pos, boolean buildStation) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TeleportToPlanetPacket> TYPE = new CustomPacketPayload.Type<>(IdentifierUtils.id("teleport_to_planet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportToPlanetPacket> STREAM_CODEC = StreamCodec.composite(
            Planet.STREAM_CODEC, TeleportToPlanetPacket::destination,
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), TeleportToPlanetPacket::pos,
            ByteBufCodecs.BOOL, TeleportToPlanetPacket::buildStation,
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

            BlockPos destPos = player.getOnPos();
            if (data.pos().isPresent()) {
                BlockPos requested = data.pos().get();
                boolean allowed = AntennaSavedData.getSavedAntennas(server)
                        .getAvailableAntennaPerLevel(player.getGameProfile().id(), dimensionKey)
                        .stream()
                        .anyMatch(antenna -> antenna.blockPos.equals(requested));

                if (!allowed) {
                    return;
                }

                destPos = requested;
            }

            // Resolve the station before teleporting: the rocket is discarded on the way, and a
            // player who cannot actually build should not be stranded in orbit.
            SpaceStationRecipe stationRecipe = null;
            ItemStack blueprint = ItemStack.EMPTY;

            if (data.buildStation()) {
                if (!planet.allowSpaceStation()) {
                    Stellaris.LOG.warn("{} asked for a space station on {}, which does not allow them", player.getGameProfile().name(), planet.dimension());
                    player.sendSystemMessage(Component.translatable("message.stellaris.space_station.not_allowed"));
                    return;
                }

                // The blueprint carried by the rocket is the authority. The recipe used to travel in
                // this packet, which let a modified client name any structure and have it placed.
                // It counts from anywhere in the cargo, so players do not have to find one exact slot.
                Container rocketInventory = rocket.getInventory();
                SpaceStationRecipe found = null;
                for (int slot = 0; slot < rocketInventory.getContainerSize() && found == null; slot++) {
                    ItemStack candidateStack = rocketInventory.getItem(slot);
                    SpaceStationRecipe candidate = candidateStack.get(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get());
                    if (candidate != null) {
                        blueprint = candidateStack;
                        found = candidate;
                    }
                }

                if (found == null) {
                    player.sendSystemMessage(Component.translatable("message.stellaris.space_station.no_blueprint"));
                    return;
                }

                SpaceStationRecipe carried = found;
                stationRecipe = SpaceStationData.SPACE_STATION_RECIPES.stream()
                        .filter(candidate -> candidate.structureId().equals(carried.structureId()))
                        .findFirst().orElse(null);

                if (stationRecipe == null) {
                    Stellaris.LOG.warn("{} carried a blueprint for unknown space station {}", player.getGameProfile().name(), carried.structureId());
                    player.sendSystemMessage(Component.translatable("message.stellaris.space_station.failed"));
                    return;
                }
            }

            TeleportUtil.teleportRocketToPlanet(player, level, rocket, destPos, false);
            player.stellaris$setPlanetMenuOpen(false, player, true);

            if (stationRecipe != null) {
                if (Utils.placeSpaceStation(player, level, stationRecipe, destPos) != null) {
                    // The lander holds the same stacks the rocket did, so this consumes the real one.
                    blueprint.shrink(1);
                } else {
                    player.sendSystemMessage(Component.translatable("message.stellaris.space_station.failed"));
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

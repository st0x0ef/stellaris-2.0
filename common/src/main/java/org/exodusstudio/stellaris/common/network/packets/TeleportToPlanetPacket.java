package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;

import net.minecraft.server.MinecraftServer;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.TeleportUtil;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record TeleportToPlanetPacket(Planet destination, Optional<BlockPos>  pos, Optional<SpaceStationRecipe>  recipe) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TeleportToPlanetPacket> TYPE = new CustomPacketPayload.Type<>(IdentifierUtils.id("teleport_to_planet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportToPlanetPacket> STREAM_CODEC = StreamCodec.composite(
            Planet.STREAM_CODEC, TeleportToPlanetPacket::destination,
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), TeleportToPlanetPacket::pos,
            ByteBufCodecs.optional(SpaceStationRecipe.STREAM_CODEC), TeleportToPlanetPacket::recipe,
            TeleportToPlanetPacket::new
    );

    public static void handle(TeleportToPlanetPacket data, NetworkManager.PacketContext context) {
        BlockPos destPos = data.pos.orElse(context.getPlayer().getOnPos());


        TeleportUtil.teleportToPlanet(context.getPlayer(), data.destination(), destPos);
        context.getPlayer().stellaris$setPlanetMenuOpen(false, context.getPlayer(), true);

        if(data.recipe.isPresent()) {
            ServerLevel level = context.getPlayer().level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, data.destination().dimension()));
            if (level != null) {
                 Utils.placeSpaceStation(context.getPlayer(), level, data.recipe.get());
            }
        }

        Utils.stopFade(context.getPlayer());
        context.getPlayer().closeContainer();


    }


    @Override
    public Type<TeleportToPlanetPacket> type() {
        return TYPE;
    }
}

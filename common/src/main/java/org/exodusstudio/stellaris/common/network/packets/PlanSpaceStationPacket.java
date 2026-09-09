package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationData;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.menus.engineering_station.SpaceStationPlannerMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record PlanSpaceStationPacket(Identifier structureId) implements CustomPacketPayload {

    public static final Type<PlanSpaceStationPacket> TYPE = new Type<>(IdentifierUtils.id("plan_space_station"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlanSpaceStationPacket> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, PlanSpaceStationPacket::structureId,
            PlanSpaceStationPacket::new
    );

    public static void handle(PlanSpaceStationPacket data, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (!(context.getPlayer() instanceof ServerPlayer player)
                    || !(player.containerMenu instanceof SpaceStationPlannerMenu menu)
                    || !menu.stillValid(player)) {
                return;
            }

            SpaceStationData.SPACE_STATION_RECIPES.stream()
                    .filter(candidate -> candidate.structureId().equals(data.structureId()))
                    .findFirst().ifPresent(menu::planStation);

        });
    }


    @Override
    public Type<PlanSpaceStationPacket> type() {
        return TYPE;
    }
}

package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.menus.engineering_station.SpaceStationPlannerMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record PlanSpaceStationPacket(SpaceStationRecipe recipe) implements CustomPacketPayload {

    public static final Type<PlanSpaceStationPacket> TYPE = new Type<>(IdentifierUtils.id("plan_space_station"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlanSpaceStationPacket> STREAM_CODEC = StreamCodec.composite(
            SpaceStationRecipe.STREAM_CODEC, PlanSpaceStationPacket::recipe,
            PlanSpaceStationPacket::new
    );

    public static void handle(PlanSpaceStationPacket data, NetworkManager.PacketContext context) {
        if(context.getPlayer().containerMenu instanceof SpaceStationPlannerMenu menu) {
            menu.planStation(data.recipe);
        }

    }


    @Override
    public Type<PlanSpaceStationPacket> type() {
        return TYPE;
    }
}

package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.menus.laboratory.ResearchMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public record InfectionResearchResultPacket(boolean success) implements CustomPacketPayload {

    public static final Type<InfectionResearchResultPacket> TYPE = new Type<>(IdentifierUtils.id("infection_research_result"));

    public static final StreamCodec<ByteBuf, InfectionResearchResultPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, InfectionResearchResultPacket::success,
            InfectionResearchResultPacket::new
    );

    @Override
    public @NotNull Type<InfectionResearchResultPacket> type() {
        return TYPE;
    }

    public static void handle(InfectionResearchResultPacket data, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player != null && player.containerMenu instanceof ResearchMenu menu) {
                menu.setResearchResult(data.success());
            }
        });
    }
}

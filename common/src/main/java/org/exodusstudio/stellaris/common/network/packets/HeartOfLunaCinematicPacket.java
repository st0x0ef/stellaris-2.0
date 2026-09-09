package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.client.cinematic.HeartOfLunaCinematic;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.UUID;

public record HeartOfLunaCinematicPacket(UUID bossUuid, int state, int remainingTicks) implements CustomPacketPayload {
    public static final Type<HeartOfLunaCinematicPacket> TYPE = new Type<>(IdentifierUtils.id("heart_of_luna_cinematic"));
    public static final StreamCodec<ByteBuf, HeartOfLunaCinematicPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, HeartOfLunaCinematicPacket::bossUuid,
            ByteBufCodecs.VAR_INT, HeartOfLunaCinematicPacket::state,
            ByteBufCodecs.VAR_INT, HeartOfLunaCinematicPacket::remainingTicks,
            HeartOfLunaCinematicPacket::new
    );

    public static void handle(HeartOfLunaCinematicPacket packet, NetworkManager.PacketContext context) {
        context.queue(() -> HeartOfLunaCinematic.sync(packet.bossUuid(), packet.state(), packet.remainingTicks()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

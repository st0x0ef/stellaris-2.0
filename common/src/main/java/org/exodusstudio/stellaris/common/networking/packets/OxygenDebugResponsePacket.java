package org.exodusstudio.stellaris.common.networking.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import org.exodusstudio.stellaris.client.debug.OxygenDebugRenderer;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;

public record OxygenDebugResponsePacket(List<BlockPos> oxygenated, List<ChunkPos> coveredChunks) implements CustomPacketPayload {

    public static final Type<OxygenDebugResponsePacket> TYPE = new Type<>(IdentifierUtils.id("oxygen_debug_response"));

    public static final StreamCodec<ByteBuf, OxygenDebugResponsePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), OxygenDebugResponsePacket::oxygenated,
            ChunkPos.STREAM_CODEC.apply(ByteBufCodecs.list()), OxygenDebugResponsePacket::coveredChunks,
            OxygenDebugResponsePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OxygenDebugResponsePacket packet, NetworkManager.PacketContext context) {
        context.queue(() -> OxygenDebugRenderer.INSTANCE.updateCache(packet.oxygenated(), packet.coveredChunks()));
    }
}

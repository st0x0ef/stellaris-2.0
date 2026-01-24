package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public record SyncOilLevelPacket(int oilLevel, int chunkX, int chunkZ) implements CustomPacketPayload {

    public static final Type<SyncOilLevelPacket> TYPE = new Type<>(IdentifierUtils.id("energy_oil_level_packet"));

    public static final StreamCodec<ByteBuf, SyncOilLevelPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncOilLevelPacket::oilLevel,
            ByteBufCodecs.INT, SyncOilLevelPacket::chunkX,
            ByteBufCodecs.INT, SyncOilLevelPacket::chunkZ,
            SyncOilLevelPacket::new
    );


    public static void handle(SyncOilLevelPacket packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        player.level().getChunk(packet.chunkX, packet.chunkZ).stellaris$setChunkOilLevel(packet.oilLevel);
    }

    @Override
    public @NotNull Type<SyncOilLevelPacket> type() {
        return TYPE;
    }
}

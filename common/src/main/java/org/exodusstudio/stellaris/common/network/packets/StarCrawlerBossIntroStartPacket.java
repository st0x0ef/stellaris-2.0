package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossIntroController;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record StarCrawlerBossIntroStartPacket(
        int bossEntityId,
        UUID bossUuid,
        long serverStartGameTime,
        long serverGameTimeAtSend,
        int durationTicks,
        double bossX,
        double bossY,
        double bossZ
) implements CustomPacketPayload {
    public static final Type<StarCrawlerBossIntroStartPacket> TYPE =
            new Type<>(IdentifierUtils.id("star_crawler_boss_intro_start"));

    public static final StreamCodec<ByteBuf, StarCrawlerBossIntroStartPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StarCrawlerBossIntroStartPacket::bossEntityId,
            UUIDUtil.STREAM_CODEC, StarCrawlerBossIntroStartPacket::bossUuid,
            ByteBufCodecs.VAR_LONG, StarCrawlerBossIntroStartPacket::serverStartGameTime,
            ByteBufCodecs.VAR_LONG, StarCrawlerBossIntroStartPacket::serverGameTimeAtSend,
            ByteBufCodecs.VAR_INT, StarCrawlerBossIntroStartPacket::durationTicks,
            ByteBufCodecs.DOUBLE, StarCrawlerBossIntroStartPacket::bossX,
            ByteBufCodecs.DOUBLE, StarCrawlerBossIntroStartPacket::bossY,
            ByteBufCodecs.DOUBLE, StarCrawlerBossIntroStartPacket::bossZ,
            StarCrawlerBossIntroStartPacket::new
    );

    public static void handle(
            StarCrawlerBossIntroStartPacket packet,
            NetworkManager.PacketContext context
    ) {
        context.queue(() -> StarCrawlerBossIntroController.start(packet));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

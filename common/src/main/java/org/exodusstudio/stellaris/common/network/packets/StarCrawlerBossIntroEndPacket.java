package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossIntroController;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record StarCrawlerBossIntroEndPacket(UUID bossUuid) implements CustomPacketPayload {
    public static final Type<StarCrawlerBossIntroEndPacket> TYPE =
            new Type<>(IdentifierUtils.id("star_crawler_boss_intro_end"));

    public static final StreamCodec<ByteBuf, StarCrawlerBossIntroEndPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, StarCrawlerBossIntroEndPacket::bossUuid,
            StarCrawlerBossIntroEndPacket::new
    );

    public static void handle(
            StarCrawlerBossIntroEndPacket packet,
            NetworkManager.PacketContext context
    ) {
        context.queue(() -> StarCrawlerBossIntroController.finish(packet.bossUuid()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

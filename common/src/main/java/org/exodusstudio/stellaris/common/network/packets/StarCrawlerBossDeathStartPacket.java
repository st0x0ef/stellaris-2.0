package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossDeathController;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Starts one player's view of the server-owned Star Crawler death timeline.
 * The two server times let the client compensate for packet transit without
 * ever becoming authoritative over when rewards or removal occur.
 */
public record StarCrawlerBossDeathStartPacket(
        int bossEntityId,
        UUID bossUuid,
        long serverStartGameTime,
        long serverGameTimeAtSend,
        int durationTicks,
        double bossX,
        double bossY,
        double bossZ
) implements CustomPacketPayload {

    public static final Type<StarCrawlerBossDeathStartPacket> TYPE =
            new Type<>(
                    IdentifierUtils.id(
                            "star_crawler_boss_death_start"
                    )
            );

    public static final StreamCodec<ByteBuf, StarCrawlerBossDeathStartPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    StarCrawlerBossDeathStartPacket::bossEntityId,
                    UUIDUtil.STREAM_CODEC,
                    StarCrawlerBossDeathStartPacket::bossUuid,
                    ByteBufCodecs.VAR_LONG,
                    StarCrawlerBossDeathStartPacket::serverStartGameTime,
                    ByteBufCodecs.VAR_LONG,
                    StarCrawlerBossDeathStartPacket::serverGameTimeAtSend,
                    ByteBufCodecs.VAR_INT,
                    StarCrawlerBossDeathStartPacket::durationTicks,
                    ByteBufCodecs.DOUBLE,
                    StarCrawlerBossDeathStartPacket::bossX,
                    ByteBufCodecs.DOUBLE,
                    StarCrawlerBossDeathStartPacket::bossY,
                    ByteBufCodecs.DOUBLE,
                    StarCrawlerBossDeathStartPacket::bossZ,
                    StarCrawlerBossDeathStartPacket::new
            );

    public static void handle(
            StarCrawlerBossDeathStartPacket packet,
            NetworkManager.PacketContext context
    ) {
        context.queue(
                () -> StarCrawlerBossDeathController.start(packet)
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

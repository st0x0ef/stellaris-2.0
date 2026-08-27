package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossDeathController;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/** Defensive camera/input cleanup for one authoritative death encounter. */
public record StarCrawlerBossDeathEndPacket(
        UUID bossUuid
) implements CustomPacketPayload {

    public static final Type<StarCrawlerBossDeathEndPacket> TYPE =
            new Type<>(
                    IdentifierUtils.id(
                            "star_crawler_boss_death_end"
                    )
            );

    public static final StreamCodec<ByteBuf, StarCrawlerBossDeathEndPacket> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    StarCrawlerBossDeathEndPacket::bossUuid,
                    StarCrawlerBossDeathEndPacket::new
            );

    public static void handle(
            StarCrawlerBossDeathEndPacket packet,
            NetworkManager.PacketContext context
    ) {
        context.queue(
                () -> StarCrawlerBossDeathController.finish(
                        packet.bossUuid()
                )
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

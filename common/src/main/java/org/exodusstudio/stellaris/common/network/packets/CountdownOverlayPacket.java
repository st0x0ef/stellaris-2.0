package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.client.overlays.RocketTimerOverlay;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public record CountdownOverlayPacket(int number) implements CustomPacketPayload {

    public static final Type<CountdownOverlayPacket> TYPE = new Type<>(IdentifierUtils.id("countdown_overlay"));

    public static final StreamCodec<ByteBuf, CountdownOverlayPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CountdownOverlayPacket::number,
            CountdownOverlayPacket::new
    );

    @Override
    public @NotNull Type<CountdownOverlayPacket> type() {
        return TYPE;
    }

    public static void handle(final CountdownOverlayPacket data, final NetworkManager.PacketContext context) {
        context.queue(() -> RocketTimerOverlay.showCountdown(data.number()));
    }
}

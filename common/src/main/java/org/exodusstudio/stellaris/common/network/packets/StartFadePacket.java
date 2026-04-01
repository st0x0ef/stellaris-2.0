package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.client.overlays.FadingHolder;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public record StartFadePacket(FadingHolder fadingHolder) implements CustomPacketPayload {

    public static final Type<StartFadePacket> TYPE = new Type<>(IdentifierUtils.id("start_fade"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StartFadePacket> STREAM_CODEC = StreamCodec.composite(
            FadingHolder.STREAM_CODEC, StartFadePacket::fadingHolder,
            StartFadePacket::new
    );

    @Override
    public @NotNull Type<StartFadePacket> type() {
        return TYPE;
    }

    public static void handle(final StartFadePacket data, final NetworkManager.PacketContext context) {
        context.getPlayer().saveDataAttachments(IdentifierUtils.id("player_fade"), data.fadingHolder());
    }
}
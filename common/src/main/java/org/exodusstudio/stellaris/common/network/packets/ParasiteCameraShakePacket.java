package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.client.effects.ParasiteCameraShake;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public record ParasiteCameraShakePacket(int ticks, float intensity) implements CustomPacketPayload {
    public static final Type<ParasiteCameraShakePacket> TYPE = new Type<>(IdentifierUtils.id("parasite_camera_shake"));

    public static final StreamCodec<ByteBuf, ParasiteCameraShakePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ParasiteCameraShakePacket::ticks,
            ByteBufCodecs.FLOAT, ParasiteCameraShakePacket::intensity,
            ParasiteCameraShakePacket::new
    );

    public static void handle(ParasiteCameraShakePacket packet, NetworkManager.PacketContext context) {
        ParasiteCameraShake.start(packet.ticks(), packet.intensity());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

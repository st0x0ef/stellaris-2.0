package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public class OpenRocketMenuPacket implements CustomPacketPayload {

    public int rocketId;
    public static final Type<OpenRocketMenuPacket> TYPE = new Type<>(IdentifierUtils.id("open_rocket_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenRocketMenuPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenRocketMenuPacket decode(RegistryFriendlyByteBuf buf) {
            return new OpenRocketMenuPacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, OpenRocketMenuPacket packet) {
            buf.writeInt(packet.rocketId);
        }
    };

    public OpenRocketMenuPacket(RegistryFriendlyByteBuf buffer) {
        this.rocketId = buffer.readInt();
    }

    public OpenRocketMenuPacket(int rocketId) {
        this.rocketId = rocketId;
    }

    public static void handle(OpenRocketMenuPacket packet, NetworkManager.PacketContext context) {

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
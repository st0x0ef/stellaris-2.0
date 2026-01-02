package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.jetbrains.annotations.NotNull;

public class KeyHandlerPacket implements CustomPacketPayload {

    public final String key;
    public final boolean condition;

    public static CustomPacketPayload.Type<KeyHandlerPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, "key_handler"));


    public static final StreamCodec<RegistryFriendlyByteBuf, KeyHandlerPacket> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public @NotNull KeyHandlerPacket decode(RegistryFriendlyByteBuf buf) {
            return new KeyHandlerPacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, KeyHandlerPacket packet) {
            buf.writeUtf(packet.key);
            buf.writeBoolean(packet.condition);
        }
    };

    public KeyHandlerPacket(String key, boolean condition) {
        this.key = key;
        this.condition = condition;
    }

    public KeyHandlerPacket(RegistryFriendlyByteBuf buffer) {
        this.key = buffer.readUtf();
        this.condition = buffer.readBoolean();
    }

    public static void handle(KeyHandlerPacket packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        context.queue(() -> {
            switch (packet.key) {
                case "start_rocket":
                    if (player.getVehicle() != null && player.getVehicle() instanceof RocketEntity rocketEntity) {
                        rocketEntity.startRocket();
                    }

                    break;
                default:
                    Stellaris.LOG.error("unknown key action {}", packet.key);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}

package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.client.screen.TestScreen;
import org.exodusstudio.stellaris.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Function;

public class OpenScreenPacket implements CustomPacketPayload {

    public static final ScreenType TEST_SCREEN = new ScreenType("test", (c) -> new TestScreen());

    private final String screenName;

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreenPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenScreenPacket decode(RegistryFriendlyByteBuf buf) {
            return new OpenScreenPacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, OpenScreenPacket packet) {
            buf.writeUtf(packet.screenName);
        }
    };

    public OpenScreenPacket(RegistryFriendlyByteBuf buffer) {
        this.screenName = buffer.readUtf();
    }

    public OpenScreenPacket(String screenName) {
        this.screenName = screenName;
    }

    public static void handle(OpenScreenPacket packet, NetworkManager.PacketContext context) {
        Minecraft.getInstance().setScreen(ScreenType.TYPES.get(packet.screenName).screen.apply(Component.empty()));
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return NetworkRegistry.OPEN_SCREEN_PACKET_TYPE;
    }

    public static class ScreenType {

        static final HashMap<String, ScreenType> TYPES = new HashMap<>();
        final String id;
        final Function<Component, Screen> screen;

        public ScreenType(String id, Function<Component, Screen> screen) {
            this.id = id;
            this.screen = screen;
            TYPES.put(this.id, this);
        }
    }


}

package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.TestScreen;
import org.exodusstudio.stellaris.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Function;

public record OpenScreenPacket(String screenId) implements CustomPacketPayload {
    public static CustomPacketPayload.Type<OpenScreenPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, "open_screen"));

    public static final ScreenType TEST_SCREEN = new ScreenType("test", (c) -> new TestScreen());

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreenPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenScreenPacket decode(RegistryFriendlyByteBuf buf) {
            return new OpenScreenPacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, OpenScreenPacket packet) {
            buf.writeUtf(packet.screenId);
        }
    };

    public OpenScreenPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readUtf());
    }

    public static void handle(OpenScreenPacket packet, NetworkManager.PacketContext context) {
        Minecraft.getInstance().setScreen(ScreenType.TYPES.get(packet.screenId).screen.apply(Component.empty()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record ScreenType(String id, Function<Component, Screen> screen) {

        static final HashMap<String, ScreenType> TYPES = new HashMap<>();

        public ScreenType(String id, Function<Component, Screen> screen) {
            this.id = id;
            this.screen = screen;
            TYPES.put(this.id, this);
        }
    }
}

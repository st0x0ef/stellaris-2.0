package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.menus.SDCardReaderApplicationMenu;
import org.exodusstudio.stellaris.common.network.NetworkRegistry;

import java.util.HashMap;

public record OpenMenuPacket(String menuId) implements CustomPacketPayload {

    public static final MenuType MAIN_TABLET = new MenuType("main_tablet", MainTabletMenu.createProvider());
    public static final MenuType SD_CARD_READER = new MenuType("sd_card_reader", SDCardReaderApplicationMenu.createProvider());

    public static final StreamCodec<ByteBuf, OpenMenuPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenMenuPacket::menuId,
            OpenMenuPacket::new
    );


    public OpenMenuPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readUtf());
    }

    public static void handle(OpenMenuPacket packet, NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof ServerPlayer player) {
            MenuRegistry.openExtendedMenu(player, MenuType.TYPES.get(packet.menuId).menu);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return NetworkRegistry.OPEN_MENU_PACKET_TYPE;
    }

    public static class MenuType {

        static final HashMap<String, MenuType> TYPES = new HashMap<>();
        final String id;
        final ExtendedMenuProvider menu;

        public MenuType(String id, ExtendedMenuProvider menu) {
            this.id = id;
            this.menu = menu;
            TYPES.put(this.id, this);
        }
    }


}

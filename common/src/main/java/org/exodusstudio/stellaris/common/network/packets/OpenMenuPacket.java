package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.menus.PlanetSelectionMenu;
import org.exodusstudio.stellaris.common.menus.SDCardReaderApplicationMenu;
import org.exodusstudio.stellaris.common.menus.WikiApplicationMenu;
import org.exodusstudio.stellaris.common.network.NetworkRegistry;

import java.util.HashMap;

@SuppressWarnings("unused")
public record OpenMenuPacket(String menuId) implements CustomPacketPayload {

    public static final MenuType MAIN_TABLET = new MenuType("main_tablet", (c) -> MainTabletMenu.createProvider());
    public static final MenuType SD_CARD_READER = new MenuType("sd_card_reader", (c) -> SDCardReaderApplicationMenu.createProvider());
    public static final MenuType WIKI = new MenuType("wiki", (c) -> WikiApplicationMenu.createProvider(null));
    public static final MenuType PLANET_SELECTION = new MenuType("planet_selection", (c) -> PlanetSelectionMenu.createProvider(c.getPlayer().level().getServer()));


    public static final StreamCodec<ByteBuf, OpenMenuPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenMenuPacket::menuId,
            OpenMenuPacket::new
    );



    public static void handle(OpenMenuPacket packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer() instanceof ServerPlayer player) {
                MenuType menuType = MenuType.TYPES.get(packet.menuId);
                if (menuType == null) {
                    return;
                }

                ExtendedMenuProvider extendedMenuProvider = menuType.menu.open(context);
                if (extendedMenuProvider == null) {
                    return;
                }

                MenuRegistry.openExtendedMenu(player, extendedMenuProvider);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return NetworkRegistry.OPEN_MENU_PACKET_TYPE;
    }

    public static class MenuType {

        static final HashMap<String, MenuType> TYPES = new HashMap<>();
        final String id;
        final MenuOpener menu;

        public MenuType(String id, MenuOpener menu) {
            this.id = id;
            this.menu = menu;
            TYPES.put(this.id, this);
        }
    }

    public interface MenuOpener {
        ExtendedMenuProvider open(NetworkManager.PacketContext context);
    }


}

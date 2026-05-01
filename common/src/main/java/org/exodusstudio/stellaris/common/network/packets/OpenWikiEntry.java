package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.wiki.WikiPacks;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.menus.WikiApplicationMenu;

public record OpenWikiEntry(Identifier entryId) implements CustomPacketPayload {

    public static CustomPacketPayload.Type<OpenWikiEntry> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, "open_wiki"));

    public static final StreamCodec<ByteBuf, OpenWikiEntry> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, OpenWikiEntry::entryId,
            OpenWikiEntry::new
    );

    public static void handle(OpenWikiEntry packet, NetworkManager.PacketContext context) {
        if(context.getPlayer() instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, WikiApplicationMenu.createProvider(packet.entryId()));
        }



    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

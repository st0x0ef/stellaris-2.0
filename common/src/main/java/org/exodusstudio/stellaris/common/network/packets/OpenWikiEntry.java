package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.wiki.WikiPacks;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;

public record OpenWikiEntry(Identifier entryId) implements CustomPacketPayload {

    public static CustomPacketPayload.Type<OpenWikiEntry> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, "open_wiki"));

    public static final StreamCodec<ByteBuf, OpenWikiEntry> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, OpenWikiEntry::entryId,
            OpenWikiEntry::new
    );

    public static void handle(OpenWikiEntry packet, NetworkManager.PacketContext context) {
        NetworkManager.sendToServer(new OpenMenuPacket("wiki"));

        if(Minecraft.getInstance().screen instanceof WikiApplicationScreen wiki) {
            var entryInfo = WikiPacks.ENTRY_COMPONENTS.get(packet.entryId);
            if(entryInfo != null) {
                wiki.setEntryInfo(entryInfo);

            }
        }


    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

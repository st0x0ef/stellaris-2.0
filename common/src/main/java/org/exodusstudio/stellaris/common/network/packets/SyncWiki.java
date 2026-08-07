package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.data.wiki.MarkdownPage;
import org.exodusstudio.stellaris.common.data.wiki.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncWiki(Map<Identifier, MarkdownPage> entryComponent, List<WikiEntry> wikiEntries) implements CustomPacketPayload {



    public static final Type<SyncWiki> TYPE = new Type<>(IdentifierUtils.id("sync_wiki_entries"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncWiki> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, MarkdownPage.STREAM_CODEC), SyncWiki::entryComponent,
            WikiEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncWiki::wikiEntries,
            SyncWiki::new
    );


    public static void handle(SyncWiki packet, NetworkManager.PacketContext context) {
        WikiEntryPack.ENTRIES.clear();
        WikiEntryPack.ENTRIES.addAll(packet.wikiEntries);

        WikiMarkdownData.ENTRY_PAGES.clear();
        WikiMarkdownData.ENTRY_PAGES.putAll(packet.entryComponent);
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

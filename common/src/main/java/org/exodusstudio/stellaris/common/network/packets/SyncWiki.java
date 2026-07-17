package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.common.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.common.data.wiki.WikiPacks;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncWiki(Map<Identifier, EntryInfo> entryComponent, List<WikiEntry> wikiEntries) implements CustomPacketPayload {



    public static final Type<SyncWiki> TYPE = new Type<>(IdentifierUtils.id("sync_wiki_entries"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncWiki> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, EntryInfo.STREAM_CODEC), SyncWiki::entryComponent,
            WikiEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncWiki::wikiEntries,
            SyncWiki::new
    );


    public static void handle(SyncWiki packet, NetworkManager.PacketContext context) {
        WikiPacks.ENTRIES.clear();
        WikiPacks.ENTRIES.addAll(packet.wikiEntries);

        WikiPacks.ENTRY_COMPONENTS.clear();
        WikiPacks.ENTRY_COMPONENTS.putAll(packet.entryComponent);
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

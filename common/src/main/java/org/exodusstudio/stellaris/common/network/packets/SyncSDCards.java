package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.data.SdCard;
import org.exodusstudio.stellaris.common.data.SdCardData;
import org.exodusstudio.stellaris.common.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.common.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.common.data.wiki.WikiPacks;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncSDCards(Map<String, SdCard> sdCards) implements CustomPacketPayload {



    public static final Type<SyncSDCards> TYPE = new Type<>(IdentifierUtils.id("sync_sd_cards"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSDCards> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, SdCard.STREAM_CODEC), SyncSDCards::sdCards,
            SyncSDCards::new
    );


    public static void handle(SyncSDCards packet, NetworkManager.PacketContext context) {
        SdCardData.SD_CARDS.clear();
        SdCardData.SD_CARDS.putAll(packet.sdCards);
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

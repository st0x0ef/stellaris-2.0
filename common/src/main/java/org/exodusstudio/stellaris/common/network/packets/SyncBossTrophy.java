package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.data.SdCard;
import org.exodusstudio.stellaris.common.data.SdCardData;
import org.exodusstudio.stellaris.common.data.trophy.BossTrophy;
import org.exodusstudio.stellaris.common.data.trophy.BossTrophyData;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.HashMap;
import java.util.Map;

public record SyncBossTrophy(Map<Identifier, BossTrophy> bossTrophies) implements CustomPacketPayload {

    public static final Type<SyncBossTrophy> TYPE = new Type<>(IdentifierUtils.id("sync_boss_trophies"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBossTrophy> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, BossTrophy.STREAM_CODEC), SyncBossTrophy::bossTrophies,
            SyncBossTrophy::new
    );


    public static void handle(SyncBossTrophy packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            BossTrophyData.TROPHY_BOSSES.clear();
            SdCardData.SD_CARDS.clear();
            BossTrophyData.TROPHY_BOSSES.putAll(packet.bossTrophies);
        });
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

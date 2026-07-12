package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.compats.jei.recipe_cache.ElectrolyzerRecipeCache;
import org.exodusstudio.stellaris.common.compats.jei.JEICompat;
import org.exodusstudio.stellaris.common.data.recipes.ElectrolyzeRecipe;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;

public record ElectrolyzerSyncerPacket(List<ElectrolyzeRecipe> recipes) implements CustomPacketPayload {

    public static final Type<ElectrolyzerSyncerPacket> TYPE = new Type<>(IdentifierUtils.id("electrolyzer_syncer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ElectrolyzerSyncerPacket> STREAM_CODEC = StreamCodec.composite(
            ElectrolyzeRecipe.Serializer.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ElectrolyzerSyncerPacket::recipes,
            ElectrolyzerSyncerPacket::new
    );

    public static void handle(ElectrolyzerSyncerPacket packet, PacketContext context) {
        context.queue(() -> {
            ElectrolyzerRecipeCache.set(packet.recipes);
            JEICompat.reloadRecipesSafe();
        });
    }

    @Override
    public Type<ElectrolyzerSyncerPacket> type() {
        return TYPE;
    }
}

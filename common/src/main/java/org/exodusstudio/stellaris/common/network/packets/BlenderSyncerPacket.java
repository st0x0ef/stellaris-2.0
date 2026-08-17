package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.compats.jei.JEICompat;
import org.exodusstudio.stellaris.common.compats.jei.recipe_cache.BlenderRecipeCache;
import org.exodusstudio.stellaris.common.data.recipes.BlendingRecipe;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;

public record BlenderSyncerPacket(List<BlendingRecipe> recipes) implements CustomPacketPayload {

    public static final Type<BlenderSyncerPacket> TYPE = new Type<>(IdentifierUtils.id("blender_syncer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlenderSyncerPacket> STREAM_CODEC = StreamCodec.composite(
            BlendingRecipe.Serializer.STREAM_CODEC.apply(ByteBufCodecs.list()),
            BlenderSyncerPacket::recipes,
            BlenderSyncerPacket::new
    );

    public static void handle(BlenderSyncerPacket packet, PacketContext context) {
        context.queue(() -> {
            BlenderRecipeCache.set(packet.recipes);
            JEICompat.reloadRecipesSafe();
        });
    }

    @Override
    public Type<BlenderSyncerPacket> type() {
        return TYPE;
    }
}

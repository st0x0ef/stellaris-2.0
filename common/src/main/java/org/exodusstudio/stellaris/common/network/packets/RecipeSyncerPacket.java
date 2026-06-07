package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.compats.jei.JEIPlugin;
import org.exodusstudio.stellaris.common.compats.jei.recipe_cache.RocketStationRecipeCache;
import org.exodusstudio.stellaris.common.data.recipes.RocketStationRecipe;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;

public record RecipeSyncerPacket(List<RocketStationRecipe> recipes) implements CustomPacketPayload {

    public static final Type<RecipeSyncerPacket> TYPE = new Type<>(IdentifierUtils.id("recipe_syncer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeSyncerPacket> STREAM_CODEC = StreamCodec.composite(
            RocketStationRecipe.Serializer.STREAM_CODEC.apply(ByteBufCodecs.list()),
            RecipeSyncerPacket::recipes,
            RecipeSyncerPacket::new
    );

    public static void handle(RecipeSyncerPacket packet, PacketContext context) {
        context.queue(() -> {
            RocketStationRecipeCache.set(packet.recipes);
            JEIPlugin.reloadRecipes();
        });
    }

    @Override
    public Type<RecipeSyncerPacket> type() {
        return TYPE;
    }
}
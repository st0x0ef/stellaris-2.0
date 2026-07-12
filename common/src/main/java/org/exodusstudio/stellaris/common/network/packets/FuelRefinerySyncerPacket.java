package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.compats.jei.recipe_cache.FuelRefineryRecipeCache;
import org.exodusstudio.stellaris.common.compats.jei.JEICompat;
import org.exodusstudio.stellaris.common.data.recipes.FuelRefineryRecipe;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;

public record FuelRefinerySyncerPacket(List<FuelRefineryRecipe> recipes) implements CustomPacketPayload {

    public static final Type<FuelRefinerySyncerPacket> TYPE = new Type<>(IdentifierUtils.id("fuel_refinery_syncer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FuelRefinerySyncerPacket> STREAM_CODEC = StreamCodec.composite(
            FuelRefineryRecipe.Serializer.STREAM_CODEC.apply(ByteBufCodecs.list()),
            FuelRefinerySyncerPacket::recipes,
            FuelRefinerySyncerPacket::new
    );

    public static void handle(FuelRefinerySyncerPacket packet, PacketContext context) {
        context.queue(() -> {
            FuelRefineryRecipeCache.set(packet.recipes);
            JEICompat.reloadRecipesSafe();
        });
    }

    @Override
    public Type<FuelRefinerySyncerPacket> type() {
        return TYPE;
    }
}

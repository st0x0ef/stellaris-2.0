package org.exodusstudio.stellaris.common.networking.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public record SelectPlanetPacket(Planet destination) implements CustomPacketPayload {

    public static final Type<SelectPlanetPacket> TYPE = new Type<>(IdentifierUtils.id("select_planet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectPlanetPacket> STREAM_CODEC = StreamCodec.composite(
            Planet.STREAM_CODEC, SelectPlanetPacket::destination,
            SelectPlanetPacket::new
    );

    public static void handle(SelectPlanetPacket data, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player == null) {
                return;
            }

            // Only accept a real planet as the destination.
            ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, data.destination().dimension());
            if (PlanetsData.getPlanet(dimensionKey) == null) {
                return;
            }


            ItemStack active = player.getActiveItem();

            if (!active.isEmpty()) {
                active.set(DataComponentsRegistry.AUTOPILOT.get(), data.destination());
            } else{
                Utils.stopFade(player);
            }

            player.stellaris$setPlanetMenuOpen(false, player, true);
            player.closeContainer();
        });
    }


    @Override
    public Type<SelectPlanetPacket> type() {
        return TYPE;
    }
}

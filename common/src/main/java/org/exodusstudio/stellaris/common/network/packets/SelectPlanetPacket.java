package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.data.Planet;
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
        context.getPlayer().getActiveItem().set(DataComponentsRegistry.AUTOPILOT.get(), data.destination);
        context.getPlayer().stellaris$setPlanetMenuOpen(false, context.getPlayer(), true);
        Utils.stopFade(context.getPlayer());
        context.getPlayer().closeContainer();
    }


    @Override
    public Type<SelectPlanetPacket> type() {
        return TYPE;
    }
}

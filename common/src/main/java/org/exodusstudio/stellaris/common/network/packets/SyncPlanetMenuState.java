package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;


public record SyncPlanetMenuState(boolean open) implements CustomPacketPayload {

    public static final Type<SyncPlanetMenuState> TYPE = new Type<>(IdentifierUtils.id("sync_planet_menu_state"));



    public static final StreamCodec<ByteBuf, SyncPlanetMenuState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncPlanetMenuState::open,
            SyncPlanetMenuState::new
    );


    public static void handle(SyncPlanetMenuState packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        player.stellaris$setPlanetMenuOpen(packet.open, player, false);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
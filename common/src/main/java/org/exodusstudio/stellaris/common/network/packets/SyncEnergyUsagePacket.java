package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.common.menus.base.EnergyUsageMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public record SyncEnergyUsagePacket(int containerId, int current, int max) implements CustomPacketPayload {

    public static final Type<SyncEnergyUsagePacket> TYPE = new Type<>(IdentifierUtils.id("energy_usage_sync_packet"));
    public static final StreamCodec<ByteBuf, SyncEnergyUsagePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncEnergyUsagePacket::containerId,
            ByteBufCodecs.VAR_INT, SyncEnergyUsagePacket::current,
            ByteBufCodecs.VAR_INT, SyncEnergyUsagePacket::max,
            SyncEnergyUsagePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final SyncEnergyUsagePacket data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            AbstractContainerMenu menu = context.getPlayer().containerMenu;
            if (menu.containerId == data.containerId && menu instanceof EnergyUsageMenu energyUsageMenu) {
                energyUsageMenu.getEnergyUsage().set(data.current, data.max);
            }
        });
    }
}

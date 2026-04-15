package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;
import org.exodusstudio.stellaris.Stellaris;
import org.jetbrains.annotations.NotNull;

/**
 * Packet to award a stat to a player.
 * Helpful for awarding stats from client-side events.
 * @param stat the stat to award
 * @param amount the amount to award. Default is 1
 */
public record AwardStatPacket(Identifier stat, int amount) implements CustomPacketPayload {


    public static CustomPacketPayload.Type<AwardStatPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, "award_stat_packet"));

    public static final StreamCodec<ByteBuf, AwardStatPacket> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AwardStatPacket::stat,
            ByteBufCodecs.INT, AwardStatPacket::amount,
            AwardStatPacket::new
    );

    public AwardStatPacket(Identifier stat) {
        this(stat, 1);
    }

    public AwardStatPacket(Identifier stat, int amount) {
        this.stat = stat;
        this.amount = amount;
    }

    public static void handle(AwardStatPacket packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();

        Identifier registeredStat = BuiltInRegistries.CUSTOM_STAT.getValue(packet.stat);
        if (registeredStat == null) {
            registeredStat = packet.stat;
        }

        player.awardStat(registeredStat, packet.amount);
    }

    @Override
    public @NotNull Type< AwardStatPacket> type() {
        return TYPE;
    }


}

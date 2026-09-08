package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.blocks.entities.AntennaBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record AntennasOperations(Antenna antenna, String action) implements CustomPacketPayload {

    public static CustomPacketPayload.Type<AntennasOperations> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, "antenna_operation"));

    private static final double MAX_INTERACTION_DISTANCE_SQR = 64.0;

    public static final StreamCodec<ByteBuf, AntennasOperations> STREAM_CODEC = StreamCodec.composite(
            Antenna.STREAM_CODEC, AntennasOperations::antenna,
            ByteBufCodecs.STRING_UTF8, AntennasOperations::action,
            AntennasOperations::new
    );


    public static void handle(AntennasOperations packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (!(context.getPlayer() instanceof ServerPlayer player)) {
                return;
            }

            Level level = player.level();
            MinecraftServer server = level.getServer();
            if (server == null) {
                return;
            }

            Antenna requested = packet.antenna;
            if (requested == null || !requested.dimension.equals(level.dimension())) {
                return;
            }

            if (!isAntennaInReach(player, requested.blockPos)) {
                return;
            }

            AntennaSavedData antennaSavedData = AntennaSavedData.getSavedAntennas(server);
            Map.Entry<UUID, Antenna> existing = antennaSavedData.getAntenna(requested);

            switch (packet.action) {
                case "set" -> {
                    if (existing == null) {
                        Antenna created = new Antenna(requested.blockPos, level.dimension(), requested.name,
                                requested.isPublic, player.getGameProfile().id(), List.of());
                        setUUIDToAntenna(level, requested.blockPos, antennaSavedData.addAntenna(created));
                    } else if (antennaSavedData.isPlayerOwner(existing.getKey(), player)) {
                        setUUIDToAntenna(level, requested.blockPos, existing.getKey());
                    }
                }
                case "modify" -> {
                    UUID uuid = getUUIDFromAntennaBlock(level, requested.blockPos);
                    if (uuid == null || !antennaSavedData.isPlayerOwner(uuid, player)) {
                        return;
                    }

                    Antenna stored = antennaSavedData.getAntenna(uuid);
                    if (stored == null) {
                        return;
                    }

                    antennaSavedData.modifyAntenna(uuid, new Antenna(stored.blockPos, stored.dimension,
                            requested.name, requested.isPublic, stored.ownerUUID, stored.whitelist));
                }
                case "remove" -> {
                    if (existing != null && antennaSavedData.isPlayerOwner(existing.getKey(), player)) {
                        antennaSavedData.removeAntenna(existing.getKey());
                    }
                }
            }
        });
    }

    private static boolean isAntennaInReach(ServerPlayer player, BlockPos pos) {
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= MAX_INTERACTION_DISTANCE_SQR;
    }

    public static void setUUIDToAntenna(Level level,  BlockPos pos, UUID uuid) {
        if(level.getBlockEntity(pos) instanceof AntennaBlockEntity blockEntity) {
            blockEntity.launchPadId = uuid;
            blockEntity.setChanged();
        }
    }

    @Nullable
    public static UUID getUUIDFromAntennaBlock(Level level, BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof AntennaBlockEntity blockEntity) {
            return blockEntity.launchPadId;
        }
        return null;

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

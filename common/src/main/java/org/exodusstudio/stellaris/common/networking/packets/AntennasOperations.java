package org.exodusstudio.stellaris.common.networking.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.blocks.entities.AntennaBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public record AntennasOperations(Antenna antenna, String action) implements CustomPacketPayload {

    public static CustomPacketPayload.Type<AntennasOperations> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, "antenna_operation"));


    public static final StreamCodec<ByteBuf, AntennasOperations> STREAM_CODEC = StreamCodec.composite(
            Antenna.STREAM_CODEC, AntennasOperations::antenna,
            ByteBufCodecs.STRING_UTF8, AntennasOperations::action,
            AntennasOperations::new
    );


    public static void handle(AntennasOperations packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            // On the server thread
            Antenna launchPad = packet.antenna;
            Level level = context.getPlayer().level();
            MinecraftServer server = level.getServer();
            if (server == null) {
                return;
            }

            AntennaSavedData antennaSavedData = AntennaSavedData.getSavedAntennas(server);

            switch (packet.action) {
                case "set" -> {
                    Map.Entry<UUID, Antenna> findedAntenna = antennaSavedData.getAntenna(launchPad);

                    if (findedAntenna == null) {
                        UUID newAntenna = antennaSavedData.addAntenna(launchPad);
                        setUUIDToAntenna(level, launchPad.blockPos, newAntenna);
                    } else {
                        setUUIDToAntenna(level, launchPad.blockPos, findedAntenna.getKey());
                    }
                }
                case "modify" -> {
                    UUID uuid = getUUIDFromAntennaBlock(level, launchPad.blockPos);
                    if (uuid != null) {
                        antennaSavedData.modifyAntenna(uuid, launchPad);
                    }
                }
                case "remove" -> antennaSavedData.removeAntenna(launchPad);
            }
        });
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
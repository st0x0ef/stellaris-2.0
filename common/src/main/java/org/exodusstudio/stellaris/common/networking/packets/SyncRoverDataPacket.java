package org.exodusstudio.stellaris.common.networking.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.vehicle_upgrade.FuelType;
import org.jetbrains.annotations.NotNull;

/**
 * Syncs the rover's dynamic fuel state (amount + type) to the client so the fuel gauge stays live.
 * The rover's modules ride on {@link net.minecraft.network.syncher.SynchedEntityData} and sync automatically.
 */
public record SyncRoverDataPacket(int entityId, int fuel, String fuelType) implements CustomPacketPayload {

    public static final Type<SyncRoverDataPacket> TYPE = new Type<>(IdentifierUtils.id("sync_rover_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRoverDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncRoverDataPacket::entityId,
            ByteBufCodecs.VAR_INT, SyncRoverDataPacket::fuel,
            ByteBufCodecs.STRING_UTF8, SyncRoverDataPacket::fuelType,
            SyncRoverDataPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final SyncRoverDataPacket data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.getEntity(data.entityId) instanceof RoverEntity rover) {
                rover.FUEL = data.fuel;
                FuelType.Type type = FuelType.Type.fromString(data.fuelType);
                if (type != null) {
                    rover.FUEL_TYPE = type;
                }
            }
        });
    }
}

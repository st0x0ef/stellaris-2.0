package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectricLightBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record SyncElectricLightDataPacketS2C(BlockPos electricLightPos, int brightness) implements CustomPacketPayload {
    public static final Type<SyncElectricLightDataPacketS2C> TYPE_S2C = new Type<>(IdentifierUtils.id("sync_electric_light_data_s2c"));

    public static final StreamCodec<ByteBuf, SyncElectricLightDataPacketS2C> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncElectricLightDataPacketS2C::electricLightPos,
            ByteBufCodecs.VAR_INT, SyncElectricLightDataPacketS2C::brightness,
            SyncElectricLightDataPacketS2C::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE_S2C;
    }

    public static void handle(final SyncElectricLightDataPacketS2C data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            Level level = context.getPlayer().level();
            if (level.getBlockEntity(data.electricLightPos()) instanceof ElectricLightBlockEntity blockEntity) {
                blockEntity.setBrightness(data.brightness(), false);
            }
        });
    }
}

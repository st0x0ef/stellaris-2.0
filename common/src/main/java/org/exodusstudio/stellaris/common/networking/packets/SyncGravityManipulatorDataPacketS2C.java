package org.exodusstudio.stellaris.common.networking.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record SyncGravityManipulatorDataPacketS2C(BlockPos gravityManipulatorPos, double gravity) implements CustomPacketPayload {
    public static final Type<SyncGravityManipulatorDataPacketS2C> TYPE_S2C = new Type<>(IdentifierUtils.id("sync_gravity_manipulator_data_s2c"));

    public static final StreamCodec<ByteBuf, SyncGravityManipulatorDataPacketS2C> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncGravityManipulatorDataPacketS2C::gravityManipulatorPos,
            ByteBufCodecs.DOUBLE, SyncGravityManipulatorDataPacketS2C::gravity,
            SyncGravityManipulatorDataPacketS2C::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE_S2C;
    }

    public static void handle(final SyncGravityManipulatorDataPacketS2C data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            Level level = context.getPlayer().level();
            if (level.getBlockEntity(data.gravityManipulatorPos()) instanceof GravityManipulatorBlockEntity blockEntity) {
                blockEntity.setGravity(data.gravity(), false);
            }
        });
    }
}

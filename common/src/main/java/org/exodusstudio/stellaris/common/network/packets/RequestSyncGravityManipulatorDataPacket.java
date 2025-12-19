package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public record RequestSyncGravityManipulatorDataPacket(BlockPos gravityManipulatorPos) implements CustomPacketPayload {
    public static final Type<RequestSyncGravityManipulatorDataPacket> TYPE = new Type<>(ResourceLocationUtils.id("request_sync_gravity_manipulator_data"));
    public static final StreamCodec<ByteBuf, RequestSyncGravityManipulatorDataPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RequestSyncGravityManipulatorDataPacket::gravityManipulatorPos,
            RequestSyncGravityManipulatorDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final RequestSyncGravityManipulatorDataPacket data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer().level().getBlockEntity(data.gravityManipulatorPos()) instanceof GravityManipulatorBlockEntity blockEntity) {
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new SyncGravityManipulatorDataPacket(data.gravityManipulatorPos(), blockEntity.isActive));
            }
        });
    }
}

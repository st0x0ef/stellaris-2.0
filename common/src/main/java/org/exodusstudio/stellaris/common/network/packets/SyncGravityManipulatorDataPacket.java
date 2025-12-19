package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public record SyncGravityManipulatorDataPacket(BlockPos gravityManipulatorPos, boolean isActive) implements CustomPacketPayload {
    public static final Type<SyncGravityManipulatorDataPacket> TYPE = new Type<>(ResourceLocationUtils.id("sync_gravity_manipulator_data"));
    public static final StreamCodec<ByteBuf, SyncGravityManipulatorDataPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncGravityManipulatorDataPacket::gravityManipulatorPos,
            ByteBufCodecs.BOOL, SyncGravityManipulatorDataPacket::isActive,
            SyncGravityManipulatorDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final SyncGravityManipulatorDataPacket data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.getBlockEntity(data.gravityManipulatorPos()) instanceof GravityManipulatorBlockEntity blockEntity) {
                blockEntity.isActive = data.isActive();
            }
        });
    }
}

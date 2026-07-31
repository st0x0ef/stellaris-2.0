package org.exodusstudio.stellaris.common.networking.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record SyncGravityManipulatorDataPacketC2S(BlockPos gravityManipulatorPos, double gravity) implements CustomPacketPayload {
    public static final Type<SyncGravityManipulatorDataPacketC2S> TYPE_C2S = new Type<>(IdentifierUtils.id("sync_gravity_manipulator_data_c2s"));

    public static final StreamCodec<ByteBuf, SyncGravityManipulatorDataPacketC2S> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncGravityManipulatorDataPacketC2S::gravityManipulatorPos,
            ByteBufCodecs.DOUBLE, SyncGravityManipulatorDataPacketC2S::gravity,
            SyncGravityManipulatorDataPacketC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE_C2S;
    }

    public static void handle(final SyncGravityManipulatorDataPacketC2S data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player == null) {
                return;
            }

            if (!Double.isFinite(data.gravity())) {
                return;
            }

            if (player.distanceToSqr(Vec3.atCenterOf(data.gravityManipulatorPos())) > 64.0) {
                return;
            }

            Level level = player.level();
            if (level.getBlockEntity(data.gravityManipulatorPos()) instanceof GravityManipulatorBlockEntity blockEntity) {
                blockEntity.setGravity(data.gravity(), false);
            }
        });
    }
}

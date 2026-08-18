package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectricLightBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record SyncElectricLightDataPacketC2S(BlockPos electricLightPos, int brightness) implements CustomPacketPayload {
    public static final Type<SyncElectricLightDataPacketC2S> TYPE_C2S = new Type<>(IdentifierUtils.id("sync_electric_light_data_c2s"));

    public static final StreamCodec<ByteBuf, SyncElectricLightDataPacketC2S> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncElectricLightDataPacketC2S::electricLightPos,
            ByteBufCodecs.VAR_INT, SyncElectricLightDataPacketC2S::brightness,
            SyncElectricLightDataPacketC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE_C2S;
    }

    public static void handle(final SyncElectricLightDataPacketC2S data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player == null) {
                return;
            }

            if (player.distanceToSqr(Vec3.atCenterOf(data.electricLightPos())) > 64.0) {
                return;
            }

            Level level = player.level();
            if (level.getBlockEntity(data.electricLightPos()) instanceof ElectricLightBlockEntity blockEntity) {
                blockEntity.setBrightness(data.brightness(), true);
            }
        });
    }
}

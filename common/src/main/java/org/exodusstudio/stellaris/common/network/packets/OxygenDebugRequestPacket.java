package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.client.debug.OxygenDebugRenderer;
import org.exodusstudio.stellaris.common.blocks.entities.machines.OxygenDistributorBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record OxygenDebugRequestPacket() implements CustomPacketPayload {

    public static final Type<OxygenDebugRequestPacket> TYPE = new Type<>(IdentifierUtils.id("oxygen_debug_request"));

    public static final StreamCodec<ByteBuf, OxygenDebugRequestPacket> STREAM_CODEC =
            StreamCodec.unit(new OxygenDebugRequestPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OxygenDebugRequestPacket packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            Level level = player.level();
            BlockPos playerPos = player.blockPosition();

            List<BlockPos> oxygenated = new ArrayList<>();
            Set<ChunkPos> coveredChunks = new HashSet<>();

            for (OxygenDistributorBlockEntity distributor : OxygenUtils.getDistributors(level)) {
                if (distributor.isRemoved() || distributor.getLevel() != level) continue;

                for (ChunkPos covered : distributor.getCoveredChunks()) {
                    if (isInRange(covered, playerPos)) {
                        coveredChunks.add(covered);
                    }
                }

                for (BlockPos pos : distributor.getOxygenatedPositions()) {
                    if (isInRange(pos, playerPos)) {
                        oxygenated.add(pos);
                    }
                }
            }

            NetworkManager.sendToPlayer(player, new OxygenDebugResponsePacket(oxygenated, new ArrayList<>(coveredChunks)));
        });
    }

    private static boolean isInRange(BlockPos pos, BlockPos playerPos) {
        return Math.abs(pos.getX() - playerPos.getX()) <= OxygenDebugRenderer.RENDER_RADIUS
                && Math.abs(pos.getY() - playerPos.getY()) <= OxygenDebugRenderer.RENDER_RADIUS
                && Math.abs(pos.getZ() - playerPos.getZ()) <= OxygenDebugRenderer.RENDER_RADIUS;
    }

    private static boolean isInRange(ChunkPos chunkPos, BlockPos playerPos) {
        return chunkPos.getMaxBlockX() >= playerPos.getX() - OxygenDebugRenderer.RENDER_RADIUS
                && chunkPos.getMinBlockX() <= playerPos.getX() + OxygenDebugRenderer.RENDER_RADIUS
                && chunkPos.getMaxBlockZ() >= playerPos.getZ() - OxygenDebugRenderer.RENDER_RADIUS
                && chunkPos.getMinBlockZ() <= playerPos.getZ() + OxygenDebugRenderer.RENDER_RADIUS;
    }
}

package org.exodusstudio.stellaris.client.debug;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.common.network.packets.OxygenDebugRequestPacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OxygenDebugRenderer implements DebugRenderer.SimpleDebugRenderer {

    public static final OxygenDebugRenderer INSTANCE = new OxygenDebugRenderer();

    // Pre-computed Vec3 centers — never allocated per-frame
    private static final List<Vec3> cachedOxygenatedCenters = new ArrayList<>();
    private static final List<Vec3> cachedCoveredBreathableCenters = new ArrayList<>();
    private static int cacheTimer = 0;

    public static final int RENDER_RADIUS = 24;
    private static final int CACHE_INTERVAL = 50;
    private static final float POINT_SIZE = 10f;

    private static final int COLOR_GREEN = 0xFF00FF00;
    private static final int COLOR_RED = 0xFFFF0000;

    private OxygenDebugRenderer() {}

    public static void clientTick(Minecraft mc) {
        if (!StellarisClient.CLIENT_CONFIG.showOxygenDebug) {
            cachedOxygenatedCenters.clear();
            cachedCoveredBreathableCenters.clear();
            cacheTimer = 0;
            return;
        }

        if (mc.player == null) return;

        if (++cacheTimer < CACHE_INTERVAL) return;
        cacheTimer = 0;

        NetworkManager.sendToServer(new OxygenDebugRequestPacket());
    }

    public void updateCache(List<BlockPos> oxygenated, List<ChunkPos> coveredChunks) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        cachedOxygenatedCenters.clear();
        cachedCoveredBreathableCenters.clear();

        // Build a lookup set for the oxygenated positions (used for red-dot exclusion)
        Set<BlockPos> oxygenatedSet = new HashSet<>(oxygenated);
        for (BlockPos pos : oxygenated) {
            cachedOxygenatedCenters.add(Vec3.atCenterOf(pos));
        }

        int minY = playerPos.getY() - RENDER_RADIUS;
        int maxY = playerPos.getY() + RENDER_RADIUS;

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (ChunkPos chunkPos : coveredChunks) {
            if (!level.hasChunk(chunkPos.x(), chunkPos.z())) continue;

            int baseX = chunkPos.getMinBlockX();
            int baseZ = chunkPos.getMinBlockZ();

            for (int x = baseX; x < baseX + 16; x++) {
                if (Math.abs(x - playerPos.getX()) > RENDER_RADIUS) continue;
                for (int z = baseZ; z < baseZ + 16; z++) {
                    if (Math.abs(z - playerPos.getZ()) > RENDER_RADIUS) continue;
                    for (int y = minY; y <= maxY; y++) {
                        mutablePos.set(x, y, z);
                        if (level.getBlockState(mutablePos).isAir()) {
                            BlockPos immutable = mutablePos.immutable();
                            if (!oxygenatedSet.contains(immutable)) {
                                cachedCoveredBreathableCenters.add(Vec3.atCenterOf(immutable));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void emitGizmos(double camX, double camY, double camZ, DebugValueAccess debugValueAccess, Frustum frustum, float partialTick) {
        if (!StellarisClient.CLIENT_CONFIG.showOxygenDebug) return;
        if (Minecraft.getInstance().player == null) return;

        for (Vec3 center : cachedOxygenatedCenters) {
            Gizmos.point(center, COLOR_GREEN, POINT_SIZE);
        }

        for (Vec3 center : cachedCoveredBreathableCenters) {
            Gizmos.point(center, COLOR_RED, POINT_SIZE);
        }
    }
}

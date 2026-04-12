package org.exodusstudio.stellaris.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

public class CraterFeature extends Feature<NoneFeatureConfiguration> {

    private final int radius;

    public CraterFeature(Codec<NoneFeatureConfiguration> codec, int radius) {
        super(codec);
        this.radius = radius;
    }

    private boolean isValidArea(WorldGenLevel level, int cx, int cz, int r) {
        int checkRadius = (int) (r * 1.2);
        int minH = Integer.MAX_VALUE;
        int maxH = Integer.MIN_VALUE;
        int step = Math.max(1, checkRadius / 2);

        for (int x = -checkRadius; x <= checkRadius; x += step) {
            for (int z = -checkRadius; z <= checkRadius; z += step) {
                int h = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, cx + x, cz + z);
                if (h < minH) minH = h;
                if (h > maxH) maxH = h;
            }
        }

        // Craters leave a sharp height variation. If the area already has a
        // high variance (> r * 0.75 + 4), it's either already a crater or a steep cliff!
        return (maxH - minH) <= (r * 0.75) + 4;
    }

    private int findTopSolid(WorldGenLevel level, int wx, int wz, int startY) {
        for (int y = startY + 4; y >= startY - 16; y--) {
            BlockState bs = level.getBlockState(new BlockPos(wx, y, wz));
            if (!bs.isAir() && bs.getFluidState().isEmpty()) return y;
        }
        return startY;
    }

    private BlockState getSurfaceBlock(WorldGenLevel level, int wx, int wz, int startY) {
        for (int y = startY; y >= startY - 4; y--) {
            BlockState bs = level.getBlockState(new BlockPos(wx, y, wz));
            if (!bs.isAir() && bs.getFluidState().isEmpty()) return bs;
        }
        return BlocksRegistry.MOON_SAND.block().get().defaultBlockState();
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos rawOrigin = context.origin();
        RandomSource random = context.random();
        int r = this.radius;

        // Force the origin to the center of its chunk to avoid crossing into far chunks (prevents setBlock out of bounds error)
        BlockPos origin = new BlockPos((rawOrigin.getX() & ~15) + 8, rawOrigin.getY(), (rawOrigin.getZ() & ~15) + 8);

        if (!isValidArea(level, origin.getX(), origin.getZ(), r)) return false;

        long seed = origin.asLong() ^ random.nextLong();
        int centerSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX(), origin.getZ());

        long s1 = seed % 1000L;
        long s2 = (seed >>> 32) % 1000L;

        double depthLimit = r * 0.45;
        double rimHeight = r * 0.15;

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                double xzDist = Math.sqrt(x * x + z * z);

                double angle = Math.atan2(z, x);
                double edgeWarp = Math.sin(angle * 4.0 + s1) * 0.05 + Math.cos(angle * 7.0 + s2) * 0.04;
                double effectiveR = r * (1.0 + edgeWarp);

                if (xzDist > effectiveR) continue;

                double normDist = xzDist / effectiveR;

                int colSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,
                        origin.getX() + x, origin.getZ() + z);

                int targetY;
                if (normDist <= 0.7) {
                    // Parabolic bowl: curves smoothly down to the center (-1.0 at center, 0.0 at edge)
                    double t = normDist / 0.7;
                    double curve = (t * t - 1.0);
                    targetY = centerSurfaceY + (int) Math.round(curve * depthLimit);

                    // Small, smooth continuous mounds on the floor for a natural feel (instead of jagged randomized noise)
                    double smallBumps = Math.sin(x * 0.4 + s1) * Math.cos(z * 0.4 + s2) * 1.3;
                    targetY += (int) Math.round(smallBumps);
                } else {
                    // Rim peak: sine wave bump blending gently into the natural terrain
                    double t = (normDist - 0.7) / 0.3;
                    double curve = Math.sin(t * Math.PI);
                    double baseY = centerSurfaceY * (1.0 - t) + colSurfaceY * t;
                    targetY = (int) Math.round(baseY + curve * rimHeight);
                }

                // Clear the column of air above the desired crater ground
                int topClearLimit = Math.max(colSurfaceY, targetY) + 5;
                for (int y = topClearLimit; y > targetY; y--) {
                    BlockPos pos = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
                    if (!level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }

                // Fill the ground with appropriate surface block
                BlockState surfaceBlock = getSurfaceBlock(level, origin.getX() + x, origin.getZ() + z, colSurfaceY);
                int solidDepth = (targetY > colSurfaceY) ? (targetY - colSurfaceY + 2) : 3;

                for (int y = targetY; y >= targetY - solidDepth; y--) {
                    BlockPos pos = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
                    level.setBlock(pos, surfaceBlock, 2);
                }
            }
        }
        return true;
    }
}
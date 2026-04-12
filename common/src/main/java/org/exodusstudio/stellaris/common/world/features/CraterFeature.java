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

import java.util.function.Supplier;

public class CraterFeature extends Feature<NoneFeatureConfiguration> {

    private final int minRadius;
    private final int maxRadius;
    private final Supplier<BlockState> fluidState;
    private final Supplier<BlockState> groundState;
    private final boolean isSurface;

    public CraterFeature(Codec<NoneFeatureConfiguration> codec, int minRadius, int maxRadius, Supplier<BlockState> fluidState, Supplier<BlockState> groundState, boolean isSurface) {
        super(codec);
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.fluidState = fluidState;
        this.groundState = groundState;
        this.isSurface = isSurface;
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

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos rawOrigin = context.origin();
        RandomSource random = context.random();

        int r = this.minRadius == this.maxRadius ? this.minRadius : this.minRadius + random.nextInt(this.maxRadius - this.minRadius + 1);

        // Force the origin to the center of its chunk to avoid crossing into far chunks (prevents setBlock out of bounds error)
        BlockPos origin = new BlockPos((rawOrigin.getX() & ~15) + 8, rawOrigin.getY(), (rawOrigin.getZ() & ~15) + 8);

        if (this.isSurface && !isValidArea(level, origin.getX(), origin.getZ(), r)) return false;

        long seed = origin.asLong() ^ random.nextLong();
        int centerSurfaceY = (this.isSurface ? level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX(), origin.getZ()) : origin.getY()) - 1;

        long s1 = seed % 1000L;
        long s2 = (seed >>> 32) % 1000L;

        double depthLimit = r * 0.45;
        double rimHeight = r * 0.15;

        BlockState fillBlock = this.fluidState.get();
        int liquidLevel = centerSurfaceY - 1;

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                double xzDist = Math.sqrt(x * x + z * z);

                double angle = Math.atan2(z, x);
                double edgeWarp = Math.sin(angle * 4.0 + s1) * 0.05 + Math.cos(angle * 7.0 + s2) * 0.04;
                double effectiveR = r * (1.0 + edgeWarp);

                if (xzDist > effectiveR) continue;

                double normDist = xzDist / effectiveR;

                int colSurfaceY;
                if (this.isSurface) {
                    colSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX() + x, origin.getZ() + z) - 1;
                } else {
                    colSurfaceY = origin.getY() - 1;
                    while (colSurfaceY > origin.getY() - 10 && level.getBlockState(new BlockPos(origin.getX() + x, colSurfaceY, origin.getZ() + z)).isAir()) {
                        colSurfaceY--;
                    }
                }

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
                int topClearLimit;
                if (this.isSurface) {
                    topClearLimit = Math.max(colSurfaceY, targetY) + 5;
                } else {
                    if (normDist <= 0.7) {
                        double dt = normDist / 0.7;
                        double ceilCurve = (1.0 - dt * dt);
                        topClearLimit = centerSurfaceY + (int) Math.round(ceilCurve * r * 0.5) + 2;
                    } else {
                        double dt = (normDist - 0.7) / 0.3;
                        topClearLimit = targetY + (int) Math.round((1.0 - dt) * 3);
                    }
                }

                for (int y = topClearLimit; y > targetY; y--) {
                    BlockPos pos = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
                    if (normDist <= 0.68 && y <= liquidLevel && !fillBlock.isAir()) {
                        level.setBlock(pos, fillBlock, 2);
                    } else if (!level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }

                // Fill the ground with appropriate surface block avoiding any holes below
                BlockState surfaceBlock = this.groundState.get();
                int minDepth = this.isSurface ? 3 : 2;
                int currentY = targetY;
                while (currentY >= targetY - minDepth || (currentY >= targetY - 15 && level.getBlockState(new BlockPos(origin.getX() + x, currentY, origin.getZ() + z)).isAir())) {
                    level.setBlock(new BlockPos(origin.getX() + x, currentY, origin.getZ() + z), surfaceBlock, 2);
                    currentY--;
                }
            }
        }
        return true;
    }
}
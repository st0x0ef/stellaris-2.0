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
        this.radius = Math.min(radius, 15);
    }

    private double noise(int x, int z, long seed) {
        long h = seed ^ (x * 1619L) ^ (z * 31337L);
        h = h * 6364136223846793005L + 1442695040888963407L;
        h ^= h >>> 33;
        h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33;
        return (h & 0xFFFFL) / 32767.5 - 1.0;
    }

    private boolean isOccupied(int cx, int cz, int r) {
        int chunkR = (r / 16) + 2;
        for (int dcx = -chunkR; dcx <= chunkR; dcx++) {
            for (int dcz = -chunkR; dcz <= chunkR; dcz++) {
                int ncx = (cx >> 4) + dcx;
                int ncz = (cz >> 4) + dcz;
                long chunkSeed = (long) ncx * 341873128712L ^ (long) ncz * 132897987541L ^ 0x9A3B4C5D6E7F8L;
                chunkSeed ^= chunkSeed >>> 33;
                chunkSeed *= 0xff51afd7ed558ccdL;
                chunkSeed ^= chunkSeed >>> 33;
                if ((chunkSeed & 0xFL) == 0) {
                    int ocx = (ncx << 4) + (int) ((chunkSeed >>> 4) & 0xFL);
                    int ocz = (ncz << 4) + (int) ((chunkSeed >>> 8) & 0xFL);
                    double dist = Math.sqrt((double)(cx - ocx) * (cx - ocx) + (double)(cz - ocz) * (cz - ocz));
                    if (dist < r * 2.2) return true;
                }
            }
        }
        return false;
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
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        int r = this.radius;

        if (isOccupied(origin.getX(), origin.getZ(), r)) return false;

        long seed = origin.asLong() ^ random.nextLong();

        int centerSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX(), origin.getZ());

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                double xzDist = Math.sqrt(x * x + z * z);
                double edgeWarp = noise(origin.getX() + x, origin.getZ() + z, seed) * r * 0.12;
                double effectiveR = r + edgeWarp;

                if (xzDist > effectiveR) continue;

                double normDist = xzDist / effectiveR;

                int colSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,
                        origin.getX() + x, origin.getZ() + z);

                for (int y = -(r + 2); y <= 1; y++) {
                    double dy = y * 1.2;
                    double dist = Math.sqrt(x * x + dy * dy + z * z);

                    if (dist > effectiveR) continue;

                    BlockPos pos = new BlockPos(origin.getX() + x, colSurfaceY + y, origin.getZ() + z);
                    BlockState existing = level.getBlockState(pos);
                    if (existing.isAir()) continue;

                    boolean isFloor = y <= -r + (int) (r * 0.2);
                    boolean isRim   = xzDist > effectiveR * 0.82 && y >= -2;

                    if (isFloor) {
                        double floorNoise = noise(origin.getX() + x, origin.getZ() + z, seed + 1L);
                        boolean raisedRock = floorNoise > 0.65 && y == -r + (int) (r * 0.2);
                        if (raisedRock) continue;

                        if (random.nextFloat() > 0.12f) {
                            level.setBlock(pos, BlocksRegistry.MOON_SAND.block().get().defaultBlockState(), 2);
                        } else {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        }
                    } else if (isRim) {
                        double rimNoise = noise(origin.getX() + x, origin.getZ() + z, seed + 2L);
                        if (rimNoise > 0.5) continue;

                        if (random.nextFloat() < 0.06f) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        }
                    } else {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }

                if (normDist > 0.65) {
                    double rimT = Math.max(0, 1.0 - Math.abs(normDist - 0.82) / 0.22);
                    int rimExtra = (int) Math.round(rimT * (r * 0.18));
                    if (rimExtra > 0) {
                        int actualTop = findTopSolid(level, origin.getX() + x, origin.getZ() + z, colSurfaceY);
                        BlockState rimBlock = getSurfaceBlock(level, origin.getX() + x, origin.getZ() + z, actualTop);
                        for (int y = actualTop + 1; y <= actualTop + rimExtra; y++) {
                            level.setBlock(new BlockPos(origin.getX() + x, y, origin.getZ() + z), rimBlock, 2);
                        }
                    }
                }
            }
        }
        return true;
    }
}
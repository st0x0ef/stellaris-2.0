package org.exodusstudio.stellaris.common.world.feature;

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
import net.minecraft.world.level.ChunkPos;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;


public class CraterFeature extends Feature<NoneFeatureConfiguration> {

    private final int radius;

    public CraterFeature(Codec<NoneFeatureConfiguration> codec, int radius) {
        super(codec);
        this.radius = Math.min(radius, 20);
    }


    private double noise(int x, int z, long seed) {
        long h = seed ^ (x * 1619L) ^ (z * 31337L);
        h = h * 6364136223846793005L + 1442695040888963407L;
        h ^= h >>> 33;
        h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33;
        return (h & 0xFFFFL) / 32767.5 - 1.0;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        int r = this.radius;

        long seed = origin.asLong() ^ random.nextLong();

        ChunkPos originChunk = new ChunkPos(origin);
        int centralX = originChunk.getMiddleBlockX();
        int centralZ = originChunk.getMiddleBlockZ();

        int centerSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX(), origin.getZ());

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                BlockPos posXZ = new BlockPos(origin.getX() + x, 0, origin.getZ() + z);
                if (Math.abs(posXZ.getX() - centralX) > 23 || Math.abs(posXZ.getZ() - centralZ) > 23) continue;

                double xzDist = Math.sqrt(x * x + z * z);
                double edgeWarp = noise(origin.getX() + x, origin.getZ() + z, seed) * r * 0.12;
                double effectiveR = r + edgeWarp;

                if (xzDist > effectiveR) continue;

                int colSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,
                        origin.getX() + x, origin.getZ() + z);

                double normalizedDist = Math.min(1.0, xzDist / effectiveR);
                double depthMultiplier = 1.0 - (normalizedDist * normalizedDist);
                double bowlDepth = (r / 2.0) * depthMultiplier;

                int newSurfaceY = colSurfaceY - (int) Math.round(bowlDepth);

                for (int y = colSurfaceY + 2; y > newSurfaceY; y--) {
                    BlockPos pos = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
                    BlockState existing = level.getBlockState(pos);
                    if (!existing.isAir() && existing.getBlock() != Blocks.BEDROCK) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }

                BlockPos floorPos = new BlockPos(origin.getX() + x, newSurfaceY, origin.getZ() + z);
                BlockState floorState = level.getBlockState(floorPos);
                if (!floorState.isAir() && floorState.getBlock() != Blocks.BEDROCK) {
                    if (random.nextFloat() < 0.2f + 0.4f * depthMultiplier) {
                        level.setBlock(floorPos, BlocksRegistry.MOON_SAND.block().get().defaultBlockState(), 2);
                    }
                }
            }
        }
        return true;
    }
}
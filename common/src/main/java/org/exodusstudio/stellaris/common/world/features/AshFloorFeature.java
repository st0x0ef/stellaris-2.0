package org.exodusstudio.stellaris.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

public class AshFloorFeature extends Feature<NoneFeatureConfiguration> {

    public AshFloorFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    private float edgeNoise(int x, int z, long seed) {
        long h = seed ^ (x * 1619L) ^ (z * 31337L);
        h = h * 6364136223846793005L + 1442695040888963407L;
        h ^= h >> 32;
        return (float)(h & 0xFFFFL) / 65535.0f;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        long seed = random.nextLong();

        int baseRadius = 16;
        boolean placedAny = false;

        for (int x = -baseRadius; x <= baseRadius; x++) {
            for (int z = -baseRadius; z <= baseRadius; z++) {

                float noise = edgeNoise(origin.getX() + x, origin.getZ() + z, seed);
                float dist = (float) Math.sqrt(x * x + z * z);
                float effectiveRadius = baseRadius * (0.75f + noise * 0.5f);

                float normalized = dist / effectiveRadius;
                if (normalized > 1.0f) continue;
                if (normalized > 0.7f && random.nextFloat() > (1.0f - normalized) * 3.3f) continue;

                int sx = origin.getX() + x;
                int sz = origin.getZ() + z;
                int sy = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, sx, sz);

                BlockPos groundPos = new BlockPos(sx, sy - 1, sz);
                BlockPos surfacePos = new BlockPos(sx, sy, sz);

                BlockState ground = level.getBlockState(groundPos);

                if (!ground.isAir()) {
                    level.setBlock(groundPos,
                            BlocksRegistry.ASH_STONE.block().get().defaultBlockState(), 2);
                    placedAny = true;
                }

                if (level.isEmptyBlock(surfacePos) && !level.getBlockState(groundPos).isAir()
                        && !ground.is(BlocksRegistry.LUNAR_STONED_WOOD_LOG.block().get())) {
                    int layers = 1 + random.nextInt(4);
                    level.setBlock(surfacePos,
                            BlocksRegistry.ASH_LAYER.block().get().defaultBlockState()
                                    .setValue(BlockStateProperties.LAYERS, layers), 2);
                    placedAny = true;
                }
            }
        }
        return placedAny;
    }
}

package org.exodusstudio.stellaris.common.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

public class BurntForestTreeFeature extends Feature<NoneFeatureConfiguration> {

    public BurntForestTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    private void setLog(WorldGenLevel level, BlockPos pos, Direction.Axis axis) {
        if (!level.isOutsideBuildHeight(pos) && level.getBlockState(pos).canBeReplaced()) {
            BlockState state = BlocksRegistry.LUNAR_STONED_WOOD_LOG.block().get().defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, axis);
            level.setBlock(pos, state, 2);
        }
    }

    private void placeTrunk(WorldGenLevel level, BlockPos base, int height, RandomSource random) {
        float ruggedness = random.nextFloat();
        int taperPoint = height / 2 + random.nextInt(height / 3);

        for (int y = 0; y < height; y++) {
            BlockPos center = base.above(y);

            if (y == 0) {
                setLog(level, center, Direction.Axis.Y);
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    setLog(level, center.relative(dir), Direction.Axis.Y);
                    if (ruggedness > 0.4f && random.nextFloat() > 0.5f) {
                        setLog(level, center.relative(dir, 2), Direction.Axis.Y);
                    }
                }
            } else if (y < taperPoint) {
                setLog(level, center, Direction.Axis.Y);
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    if (random.nextFloat() > 0.3f) setLog(level, center.relative(dir), Direction.Axis.Y);
                }
            } else if (y < height - 1) {
                setLog(level, center, Direction.Axis.Y);
                if (random.nextFloat() > 0.92f) {
                    Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    setLog(level, center.relative(d), d.getAxis());
                }
            } else {
                if (random.nextFloat() > 0.35f) {
                    setLog(level, center, Direction.Axis.Y);
                    if (random.nextBoolean()) setLog(level, center.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random)), Direction.Axis.Y);
                }
            }
        }
    }

    private void placeFallenLog(WorldGenLevel level, BlockPos base, RandomSource random) {
        Direction primaryDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        Direction secondaryDir = random.nextBoolean() ? primaryDir.getClockWise() : primaryDir.getCounterClockWise();
        int length = 6 + random.nextInt(12);
        BlockPos currentPos = base;
        float diagonalBias = 0.2f + (random.nextFloat() * 0.4f);

        for (int i = 0; i < length; i++) {
            currentPos = currentPos.relative(primaryDir);
            if (random.nextFloat() < diagonalBias) {
                int fillY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, currentPos.getX(), currentPos.getZ());
                setLog(level, new BlockPos(currentPos.getX(), fillY, currentPos.getZ()), primaryDir.getAxis());
                currentPos = currentPos.relative(secondaryDir);
            }

            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, currentPos.getX(), currentPos.getZ());
            BlockPos logPos = new BlockPos(currentPos.getX(), y, currentPos.getZ());
            if (level.getBlockState(logPos).is(BlocksRegistry.LUNAR_STONED_WOOD_LOG.block().get())) {
                logPos = logPos.above();
            }

            setLog(level, logPos, (random.nextBoolean() ? primaryDir.getAxis() : secondaryDir.getAxis()));
        }
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int attempts = 12 + random.nextInt(10);
        boolean placedAny = false;

        for (int i = 0; i < attempts; i++) {
            int x = origin.getX() + random.nextInt(24) - 12;
            int z = origin.getZ() + random.nextInt(24) - 12;
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            if (!level.getBlockState(pos.below()).isAir() && level.getBlockState(pos).canBeReplaced()) {
                float roll = random.nextFloat();
                if (roll < 0.65f) {
                    int h = (random.nextFloat() > 0.8f) ? 20 + random.nextInt(8) : 8 + random.nextInt(14);
                    placeTrunk(level, pos, h, random);
                    placedAny = true;
                } else {
                    placeFallenLog(level, pos, random);
                    placedAny = true;
                }
            }
        }
        return placedAny;
    }
}
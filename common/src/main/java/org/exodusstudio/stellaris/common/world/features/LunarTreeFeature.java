package org.exodusstudio.stellaris.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

public class LunarTreeFeature extends Feature<NoneFeatureConfiguration> {

    public LunarTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    private BlockState logState(Direction.Axis axis) {
        return BlocksRegistry.LUNAR_LOG.block().get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
    }

    private BlockState leafState() {
        return BlocksRegistry.LUNAR_LEAVES.block().get().defaultBlockState();
    }

    private void setIfReplaceable(WorldGenLevel level, BlockPos pos, BlockState state) {
        if (!level.isOutsideBuildHeight(pos) && level.getBlockState(pos).canBeReplaced()) {
            level.setBlock(pos, state, 2);
        }
    }

    private void placeLog(WorldGenLevel level, BlockPos pos, Direction.Axis axis) {
        setIfReplaceable(level, pos, logState(axis));
    }

    private void placeLeaf(WorldGenLevel level, BlockPos pos) {
        setIfReplaceable(level, pos, leafState());
    }

    private void placeLeafDisk(WorldGenLevel level, BlockPos center, int radius, RandomSource random) {
        int r2 = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int dist = dx * dx + dz * dz;
                if (dist > r2 + random.nextInt(2)) continue;
                if (radius > 2 && Math.abs(dx) + Math.abs(dz) > radius + 1) continue;
                if (random.nextFloat() < 0.08f && dist > 1) continue;
                placeLeaf(level, center.offset(dx, 0, dz));
            }
        }
    }

    private void placeDroop(WorldGenLevel level, BlockPos start, RandomSource random) {
        int length = 1 + random.nextInt(3);
        BlockPos pos = start;
        for (int i = 0; i < length; i++) {
            pos = pos.below();
            placeLeaf(level, pos);
        }
    }

    private void placeBranch(WorldGenLevel level, BlockPos start, Direction direction, int length, RandomSource random) {
        BlockPos pos = start;
        for (int i = 0; i < length; i++) {
            pos = pos.relative(direction);
            if (i > 0 && random.nextFloat() < 0.4f) {
                pos = pos.above();
            }
            placeLog(level, pos, direction.getAxis());
            if (random.nextFloat() < 0.65f) {
                placeLeafDisk(level, pos.above(), 2, random);
            }
            if (i > 0 && random.nextFloat() < 0.55f) {
                placeLeafDisk(level, pos, 1, random);
            }
        }
        placeLeafDisk(level, pos.above(), 3, random);
        placeLeafDisk(level, pos, 2, random);
        if (random.nextFloat() < 0.55f) {
            placeDroop(level, pos.relative(direction.getClockWise()), random);
        }
        if (random.nextFloat() < 0.55f) {
            placeDroop(level, pos.relative(direction.getCounterClockWise()), random);
        }
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        if (!level.getBlockState(origin.below()).isFaceSturdy(level, origin.below(), Direction.UP)) {
            return false;
        }

        int height = 9 + random.nextInt(5);
        if (random.nextFloat() < 0.35f) {
            height += 3 + random.nextInt(5);
        }

        Direction leanDirection = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos cursor = origin;
        BlockPos top = origin;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.6f) {
                placeLog(level, origin.relative(dir), dir.getAxis());
            }
            if (random.nextFloat() < 0.35f) {
                placeLog(level, origin.relative(dir).below(), dir.getAxis());
            }
        }

        for (int y = 0; y < height; y++) {
            if (y > 1 && y < height - 2 && random.nextFloat() < 0.28f) {
                cursor = cursor.relative(leanDirection);
            }
            if (y > 3 && y < height - 1 && random.nextFloat() < 0.12f) {
                Direction wobble = random.nextBoolean() ? leanDirection.getClockWise() : leanDirection.getCounterClockWise();
                cursor = cursor.relative(wobble);
            }

            BlockPos trunkPos = cursor.above(y);
            placeLog(level, trunkPos, Direction.Axis.Y);
            top = trunkPos;

            if (y == 0 || y == 1) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    if (random.nextFloat() < 0.35f) {
                        placeLog(level, trunkPos.relative(dir), dir.getAxis());
                    }
                }
            }

            if (y == height / 3 || y == (height * 2) / 3) {
                Direction branchDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                placeBranch(level, trunkPos, branchDir, 2 + random.nextInt(3), random);
            }
        }

        placeLeafDisk(level, top.above(3), 2, random);
        placeLeafDisk(level, top.above(2), 4, random);
        placeLeafDisk(level, top.above(), 5, random);
        placeLeafDisk(level, top, 4, random);
        placeLeafDisk(level, top.below(), 4, random);
        placeLeafDisk(level, top.below(2), 3, random);
        placeLeafDisk(level, top.below(3), 2, random);

        placeLeafDisk(level, top.above().relative(leanDirection), 3, random);
        placeLeafDisk(level, top.above().relative(leanDirection.getOpposite()), 3, random);
        placeLeafDisk(level, top.relative(leanDirection), 3, random);
        placeLeafDisk(level, top.relative(leanDirection.getOpposite()), 3, random);
        placeLeafDisk(level, top.below().relative(leanDirection.getClockWise()), 2, random);
        placeLeafDisk(level, top.below().relative(leanDirection.getCounterClockWise()), 2, random);

        Direction oppositeLean = leanDirection.getOpposite();
        placeBranch(level, top.below(1), leanDirection, 3 + random.nextInt(2), random);
        if (random.nextFloat() < 0.65f) {
            placeBranch(level, top.below(1), oppositeLean, 2 + random.nextInt(3), random);
        }
        if (random.nextFloat() < 0.45f) {
            placeBranch(level, top.below(2), leanDirection.getClockWise(), 2 + random.nextInt(2), random);
        }
        if (random.nextFloat() < 0.45f) {
            placeBranch(level, top.below(2), leanDirection.getCounterClockWise(), 2 + random.nextInt(2), random);
        }

        return true;
    }
}



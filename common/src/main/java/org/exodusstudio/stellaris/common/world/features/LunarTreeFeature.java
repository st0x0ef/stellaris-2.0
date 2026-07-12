package org.exodusstudio.stellaris.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

import java.util.HashSet;
import java.util.Set;

public class LunarTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MAX_LEAF_DISTANCE = 6;

    public LunarTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    private BlockState logState(Direction.Axis axis) {
        return BlocksRegistry.LUNAR_LOG.block().get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
    }

    private BlockState leafState(int distance) {
        int clamped = Math.clamp(distance, 1, 7);
        return BlocksRegistry.LUNAR_LEAVES.block().get().defaultBlockState()
                .setValue(LeavesBlock.DISTANCE, clamped);
    }

    private boolean setIfReplaceable(WorldGenLevel level, BlockPos pos, BlockState state) {
        if (!level.isOutsideBuildHeight(pos) && level.getBlockState(pos).canBeReplaced()) {
            level.setBlock(pos, state, 3);
            return true;
        }
        return false;
    }

    /**
     * Chebyshev distance from pos to the nearest recorded log.
     * Returns Integer.MAX_VALUE if there are no logs yet.
     */
    private int distanceToNearestLog(BlockPos pos, Set<BlockPos> logs) {
        int best = Integer.MAX_VALUE;
        for (BlockPos log : logs) {
            int dx = Math.abs(pos.getX() - log.getX());
            int dy = Math.abs(pos.getY() - log.getY());
            int dz = Math.abs(pos.getZ() - log.getZ());
            int dist = Math.max(dx, Math.max(dy, dz));
            if (dist < best) {
                best = dist;
                if (best <= 1) break; // can't get closer than adjacent
            }
        }
        return best;
    }

    private void placeLog(WorldGenLevel level, BlockPos pos, Direction.Axis axis, Set<BlockPos> logs) {
        if (setIfReplaceable(level, pos, logState(axis))) {
            logs.add(pos.immutable());
        }
    }

    private void placeLeaf(WorldGenLevel level, BlockPos pos, Set<BlockPos> logs) {
        int distance = distanceToNearestLog(pos, logs);
        if (distance > MAX_LEAF_DISTANCE) {
            return; // too far from any log, skip entirely rather than place a doomed leaf
        }

        setIfReplaceable(level, pos, leafState(distance));
    }

    private void placeLeafDisk(WorldGenLevel level, BlockPos center, int radius, RandomSource random, Set<BlockPos> logs) {
        int r2 = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int dist = dx * dx + dz * dz;
                if (dist > r2 + random.nextInt(2)) continue;
                if (radius > 2 && Math.abs(dx) + Math.abs(dz) > radius + 1) continue;
                if (random.nextFloat() < 0.08f && dist > 1) continue;
                placeLeaf(level, center.offset(dx, 0, dz), logs);
            }
        }
    }

    private void placeDroop(WorldGenLevel level, BlockPos start, RandomSource random, Set<BlockPos> logs) {
        int length = 1 + random.nextInt(3);
        BlockPos pos = start;
        for (int i = 0; i < length; i++) {
            pos = pos.below();
            placeLeaf(level, pos, logs);
        }
    }

    private void placeBranch(WorldGenLevel level, BlockPos start, Direction direction, int length, RandomSource random, Set<BlockPos> logs) {
        BlockPos pos = start;
        for (int i = 0; i < length; i++) {
            pos = pos.relative(direction);
            if (i > 0 && random.nextFloat() < 0.4f) {
                pos = pos.above();
            }
            placeLog(level, pos, direction.getAxis(), logs);
            if (random.nextFloat() < 0.65f) {
                placeLeafDisk(level, pos.above(), 2, random, logs);
            }
            if (i > 0 && random.nextFloat() < 0.55f) {
                placeLeafDisk(level, pos, 1, random, logs);
            }
        }
        placeLeafDisk(level, pos.above(), 3, random, logs);
        placeLeafDisk(level, pos, 2, random, logs);
        if (random.nextFloat() < 0.55f) {
            placeDroop(level, pos.relative(direction.getClockWise()), random, logs);
        }
        if (random.nextFloat() < 0.55f) {
            placeDroop(level, pos.relative(direction.getCounterClockWise()), random, logs);
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

        Set<BlockPos> logs = new HashSet<>();

        int height = 9 + random.nextInt(5);
        if (random.nextFloat() < 0.35f) {
            height += 3 + random.nextInt(5);
        }

        Direction leanDirection = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos cursor = origin;
        BlockPos top = origin;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.6f) {
                placeLog(level, origin.relative(dir), dir.getAxis(), logs);
            }
            if (random.nextFloat() < 0.35f) {
                placeLog(level, origin.relative(dir).below(), dir.getAxis(), logs);
            }
        }

        final int maxDrift = 3;

        for (int y = 0; y < height; y++) {
            int driftFromOrigin = Math.max(Math.abs(cursor.getX() - origin.getX()), Math.abs(cursor.getZ() - origin.getZ()));

            if (y > 1 && y < height - 2 && driftFromOrigin < maxDrift && random.nextFloat() < 0.28f) {
                cursor = cursor.relative(leanDirection);
            }
            if (y > 3 && y < height - 1 && driftFromOrigin < maxDrift && random.nextFloat() < 0.12f) {
                Direction wobble = random.nextBoolean() ? leanDirection.getClockWise() : leanDirection.getCounterClockWise();
                cursor = cursor.relative(wobble);
            }

            BlockPos trunkPos = cursor.above(y);
            placeLog(level, trunkPos, Direction.Axis.Y, logs);
            top = trunkPos;

            if (y == 0 || y == 1) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    if (random.nextFloat() < 0.35f) {
                        placeLog(level, trunkPos.relative(dir), dir.getAxis(), logs);
                    }
                }
            }

            if (y == height / 3 || y == (height * 2) / 3) {
                Direction branchDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                placeBranch(level, trunkPos, branchDir, 2 + random.nextInt(3), random, logs);
            }
        }

        placeLeafDisk(level, top.above(3), 1, random, logs);
        placeLeafDisk(level, top.above(2), 3, random, logs);
        placeLeafDisk(level, top.above(), 4, random, logs);
        placeLeafDisk(level, top, 3, random, logs);
        placeLeafDisk(level, top.below(), 3, random, logs);
        placeLeafDisk(level, top.below(2), 2, random, logs);
        placeLeafDisk(level, top.below(3), 1, random, logs);

        placeLeafDisk(level, top.above().relative(leanDirection), 2, random, logs);
        placeLeafDisk(level, top.above().relative(leanDirection.getOpposite()), 2, random, logs);
        placeLeafDisk(level, top.relative(leanDirection), 2, random, logs);
        placeLeafDisk(level, top.relative(leanDirection.getOpposite()), 2, random, logs);
        placeLeafDisk(level, top.below().relative(leanDirection.getClockWise()), 1, random, logs);
        placeLeafDisk(level, top.below().relative(leanDirection.getCounterClockWise()), 1, random, logs);

        Direction oppositeLean = leanDirection.getOpposite();
        placeBranch(level, top.below(1), leanDirection, 3 + random.nextInt(2), random, logs);
        if (random.nextFloat() < 0.65f) {
            placeBranch(level, top.below(1), oppositeLean, 2 + random.nextInt(3), random, logs);
        }
        if (random.nextFloat() < 0.45f) {
            placeBranch(level, top.below(2), leanDirection.getClockWise(), 2 + random.nextInt(2), random, logs);
        }
        if (random.nextFloat() < 0.45f) {
            placeBranch(level, top.below(2), leanDirection.getCounterClockWise(), 2 + random.nextInt(2), random, logs);
        }

        return true;
    }
}
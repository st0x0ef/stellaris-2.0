package org.exodusstudio.stellaris.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.exodusstudio.stellaris.common.world.configuration.SpeleothemClusterConfiguration;
import org.exodusstudio.stellaris.common.world.features.utils.SpeleothemUtils;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class SpeleothemClusterFeature extends Feature<SpeleothemClusterConfiguration> {
    @SuppressWarnings("unused")
    protected SpeleothemClusterFeature(Codec<SpeleothemClusterConfiguration> codec) {
        super(codec);
    }

    public SpeleothemClusterFeature() {
        super(SpeleothemClusterConfiguration.CODEC);
    }

    private boolean canPlaceAt(final BlockState state, final boolean allowWaterPlacement) {
        return allowWaterPlacement ? SpeleothemUtils.isEmptyOrWater(state) : state.isAir();
    }

    private int getAvailableSpeleothemHeight(
            final WorldGenLevel level, final BlockPos basePos, final Direction direction, final int desiredHeight, final Predicate<BlockState> canPlace
    ) {
        BlockPos pos = basePos;
        int availableHeight = 0;

        while (availableHeight < desiredHeight && canPlace.test(level.getBlockState(pos))) {
            availableHeight++;
            pos = pos.relative(direction);
        }

        return availableHeight;
    }

    @Override
    public boolean place(FeaturePlaceContext<SpeleothemClusterConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        SpeleothemClusterConfiguration config = context.config();
        RandomSource random = context.random();
        if (!SpeleothemUtils.isEmptyOrWater(level, origin)) {
            return false;
        }

        int height = config.height().sample(random);
        float wetness = config.wetness().sample(random);
        float density = config.density().sample(random);
        int xRadius = config.radius().sample(random);
        int zRadius = config.radius().sample(random);

        for (int dx = -xRadius; dx <= xRadius; dx++) {
            for (int dz = -zRadius; dz <= zRadius; dz++) {
                double chanceOfStalagmiteOrStalactite = this.getChanceOfStalagmiteOrStalactite(xRadius, zRadius, dx, dz, config);
                BlockPos pos = origin.offset(dx, 0, dz);
                this.placeColumn(level, random, pos, dx, dz, wetness, chanceOfStalagmiteOrStalactite, height, density, config);
            }
        }

        return true;
    }

    private void placeColumn(
            final WorldGenLevel level,
            final RandomSource random,
            final BlockPos pos,
            final int dx,
            final int dz,
            final float chanceOfWater,
            final double chanceOfStalagmiteOrStalactite,
            final int clusterHeight,
            final float density,
            final SpeleothemClusterConfiguration config
    ) {
        Predicate<BlockState> canPlace = state -> this.canPlaceAt(state, config.placementOptions().allowWaterPlacement());
        Optional<Column> baseColumn = Column.scan(level, pos, config.floorToCeilingSearchRange(), canPlace, SpeleothemUtils::isNeitherEmptyNorWater);
        if (!baseColumn.isEmpty()) {
            Column scannedColumn = baseColumn.get();
            Column placementColumn = this.filterPlacementColumn(scannedColumn, config);
            OptionalInt ceiling = placementColumn.getCeiling();
            OptionalInt baseFloor = placementColumn.getFloor();
            if (!ceiling.isEmpty() || !baseFloor.isEmpty()) {
                boolean wantPool = random.nextFloat() < chanceOfWater;
                Column column;
                if (wantPool && baseFloor.isPresent() && this.canPlacePool(level, pos.atY(baseFloor.getAsInt()), config)) {
                    int baseFloorY = baseFloor.getAsInt();
                    column = placementColumn.withFloor(OptionalInt.of(baseFloorY - 1));
                    level.setBlock(pos.atY(baseFloorY), Blocks.WATER.defaultBlockState(), 2);
                } else {
                    column = placementColumn;
                }

                OptionalInt floor = column.getFloor();
                boolean wantStalactite = random.nextDouble() < chanceOfStalagmiteOrStalactite;
                int stalactiteHeight;
                if (ceiling.isPresent() && wantStalactite && !this.isLava(level, pos.atY(ceiling.getAsInt()))) {
                    int ceilingThickness = config.speleothemBlockLayerThickness().sample(random);
                    this.replaceBlocksWithBaseBlocks(level, pos.atY(ceiling.getAsInt()), ceilingThickness, Direction.UP, config);
                    int maxHeightForThisColumn = this.getMaxHeightForThisColumn(scannedColumn, clusterHeight, config);
                    if (config.placementOptions().placementMode() == SpeleothemClusterConfiguration.PlacementMode.FLOOR_AND_CEILING && floor.isPresent()) {
                        maxHeightForThisColumn = Math.min(clusterHeight, ceiling.getAsInt() - floor.getAsInt());
                    }

                    stalactiteHeight = this.getSpeleothemHeight(random, dx, dz, density, maxHeightForThisColumn, config);
                } else {
                    stalactiteHeight = 0;
                }

                boolean wantStalagmite = random.nextDouble() < chanceOfStalagmiteOrStalactite;
                int stalagmiteHeight;
                if (floor.isPresent() && wantStalagmite && !this.isLava(level, pos.atY(floor.getAsInt()))) {
                    int floorThickness = config.speleothemBlockLayerThickness().sample(random);
                    this.replaceBlocksWithBaseBlocks(level, pos.atY(floor.getAsInt()), floorThickness, Direction.DOWN, config);
                    if (ceiling.isPresent()) {
                        stalagmiteHeight = Math.max(
                                0, stalactiteHeight + Mth.randomBetweenInclusive(random, -config.maxStalagmiteStalactiteHeightDiff(), config.maxStalagmiteStalactiteHeightDiff())
                        );
                    } else {
                        int maxHeightForThisColumn = this.getMaxHeightForThisColumn(scannedColumn, clusterHeight, config);
                        stalagmiteHeight = this.getSpeleothemHeight(random, dx, dz, density, maxHeightForThisColumn, config);
                    }
                } else {
                    stalagmiteHeight = 0;
                }

                int actualStalactiteHeight;
                int actualStalagmiteHeight;
                if (ceiling.isPresent() && floor.isPresent() && ceiling.getAsInt() - stalactiteHeight <= floor.getAsInt() + stalagmiteHeight) {
                    int floorY = floor.getAsInt();
                    int ceilingY = ceiling.getAsInt();
                    int lowestStalactiteBottom = Math.max(ceilingY - stalactiteHeight, floorY + 1);
                    int highestStalagmiteTop = Math.min(floorY + stalagmiteHeight, ceilingY - 1);
                    int actualStalactiteBottom = Mth.randomBetweenInclusive(random, lowestStalactiteBottom, highestStalagmiteTop + 1);
                    int actualStalagmiteTop = actualStalactiteBottom - 1;
                    actualStalactiteHeight = ceilingY - actualStalactiteBottom;
                    actualStalagmiteHeight = actualStalagmiteTop - floorY;
                } else {
                    actualStalactiteHeight = stalactiteHeight;
                    actualStalagmiteHeight = stalagmiteHeight;
                }

                if (ceiling.isPresent()) {
                    actualStalactiteHeight = this.getAvailableSpeleothemHeight(
                            level, pos.atY(ceiling.getAsInt() - 1), Direction.DOWN, actualStalactiteHeight, canPlace
                    );
                }

                if (floor.isPresent()) {
                    actualStalagmiteHeight = this.getAvailableSpeleothemHeight(level, pos.atY(floor.getAsInt() + 1), Direction.UP, actualStalagmiteHeight, canPlace);
                }

                boolean mergeTips = random.nextBoolean()
                        && actualStalactiteHeight > 0
                        && actualStalagmiteHeight > 0
                        && column.getHeight().isPresent()
                        && actualStalactiteHeight + actualStalagmiteHeight == column.getHeight().getAsInt();
                if (ceiling.isPresent()) {
                    SpeleothemUtils.growSpeleothem(
                            level,
                            pos.atY(ceiling.getAsInt() - 1),
                            Direction.DOWN,
                            actualStalactiteHeight,
                            mergeTips,
                            config.baseBlock().getBlock(),
                            config.pointedBlock().getBlock(),
                            config.replaceableBlocks(),
                            config.placementOptions().baseBlockTransformer()
                    );
                }

                if (floor.isPresent()) {
                    SpeleothemUtils.growSpeleothem(
                            level,
                            pos.atY(floor.getAsInt() + 1),
                            Direction.UP,
                            actualStalagmiteHeight,
                            mergeTips,
                            config.baseBlock().getBlock(),
                            config.pointedBlock().getBlock(),
                            config.replaceableBlocks(),
                            config.placementOptions().baseBlockTransformer()
                    );
                }
            }
        }
    }

    Column filterPlacementColumn(final Column scannedColumn, SpeleothemClusterConfiguration config) {
        OptionalInt floor = config.placementOptions().placementMode() != SpeleothemClusterConfiguration.PlacementMode.CEILING_ONLY
                ? scannedColumn.getFloor()
                : OptionalInt.empty();
        OptionalInt ceiling = config.placementOptions().placementMode() != SpeleothemClusterConfiguration.PlacementMode.FLOOR_ONLY
                ? scannedColumn.getCeiling()
                : OptionalInt.empty();
        return Column.create(floor, ceiling);
    }

    int getMaxHeightForThisColumn(final Column scannedColumn, final int clusterHeight, SpeleothemClusterConfiguration config) {
        return config.placementOptions().placementMode() != SpeleothemClusterConfiguration.PlacementMode.FLOOR_AND_CEILING && scannedColumn.getHeight().isPresent()
                ? Math.min(clusterHeight, scannedColumn.getHeight().getAsInt())
                : clusterHeight;
    }

    private boolean isLava(final LevelReader level, final BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.LAVA);
    }

    private int getSpeleothemHeight(final RandomSource random, final int dx, final int dz, final float density, final int maxHeight, SpeleothemClusterConfiguration config) {
        if (random.nextFloat() > density) {
            return 0;
        }

        int distanceFromCenter = Math.abs(dx) + Math.abs(dz);
        float heightMean = (float)Mth.clampedMap(distanceFromCenter, 0.0, config.maxDistanceFromCenterAffectingHeightBias(), maxHeight / 2.0, 0.0);
        return (int)randomBetweenBiased(random, 0.0F, maxHeight, heightMean, config.heightDeviation());
    }

    private boolean canPlacePool(final WorldGenLevel level, final BlockPos pos, SpeleothemClusterConfiguration config) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.WATER) && !state.is(config.baseBlock().getBlock()) && !state.is(config.pointedBlock().getBlock())) {
            if (level.getBlockState(pos.above()).getFluidState().is(FluidTags.WATER)) {
                return false;
            }

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (!this.canBeAdjacentToWater(level, pos.relative(direction))) {
                    return false;
                }
            }

            return this.canBeAdjacentToWater(level, pos.below());
        } else {
            return false;
        }
    }

    private boolean canBeAdjacentToWater(final WorldGenLevel level, final BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getFluidState().is(FluidTags.WATER);
    }

    private void replaceBlocksWithBaseBlocks(final WorldGenLevel level, final BlockPos firstPos, final int maxCount, final Direction direction, SpeleothemClusterConfiguration config) {
        BlockPos.MutableBlockPos pos = firstPos.mutable();

        for (int i = 0; i < maxCount; i++) {
            if (!SpeleothemUtils.placeBaseBlockIfPossible(level, pos, config.baseBlock().getBlock(), config.replaceableBlocks())) {
                return;
            }

            pos.move(direction);
        }
    }

    private double getChanceOfStalagmiteOrStalactite(final int xRadius, final int zRadius, final int dx, final int dz, SpeleothemClusterConfiguration config) {
        int xDistanceFromEdge = xRadius - Math.abs(dx);
        int zDistanceFromEdge = zRadius - Math.abs(dz);
        int distanceFromEdge = Math.min(xDistanceFromEdge, zDistanceFromEdge);
        return Mth.clampedMap(distanceFromEdge, 0.0F, config.maxDistanceFromEdgeAffectingChanceOfSpeleothem(), config.chanceOfSpeleothemAtMaxDistanceFromCenter(), 1.0F);
    }

    private static float randomBetweenBiased(final RandomSource random, final float min, final float maxExclusive, final float mean, final float deviation) {
        return ClampedNormalFloat.sample(random, mean, deviation, min, maxExclusive);
    }
}

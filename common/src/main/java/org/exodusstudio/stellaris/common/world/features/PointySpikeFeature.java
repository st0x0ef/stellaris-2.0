package org.exodusstudio.stellaris.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.world.configuration.PointySpikeFeatureConfiguration;

import java.util.List;

public class PointySpikeFeature extends Feature<PointySpikeFeatureConfiguration> {

    public PointySpikeFeature(Codec<PointySpikeFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<PointySpikeFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        var config = context.config();

        // Verify the placement context (e.g., must be placed in stone/cave walls)
        if (!level.getBlockState(origin.below()).isSolidRender()) {
            return false;
        }

        // Define our crystal variables
        Vec3 originBottomCorner = new Vec3(origin.getX(), origin.getY(), origin.getZ());
        BlockState baseBlock = config.blockState();
        int numberOfSpikes = config.branchCount().sample(random);
        List<? extends IntProvider> angles = List.of(
          UniformInt.of(90, 180),
            UniformInt.of(270,360)
        );
        int tau = angles.get(random.nextInt(angles.toArray().length)).sample(random);
        int baseAngle = random.nextInt(tau);

        for (int i = 0; i < numberOfSpikes; i++) {
            double angle = Math.toRadians(baseAngle + i * ((double) tau / numberOfSpikes));
            int coreRadius = config.baseRadius().sample(random);
            int spread = config.spread().sample(random);
            int height = config.height().sample(random);

            Vec3 offset = new Vec3(Math.cos(angle + Math.toRadians(random.nextInt(5))) * spread, height, Math.sin(angle + Math.toRadians(random.nextInt(5))) * spread);
            Vec3 tip = origin.getCenter().add(offset);
            Vec3 ray = tip.subtract(originBottomCorner);
            int minX = Math.min(origin.getX() - coreRadius, (int) tip.x);
            int minY = Math.min(origin.getY() - coreRadius, (int) tip.y);
            int minZ = Math.min(origin.getZ() - coreRadius, (int) tip.z);
            int maxX = Math.max(origin.getX() + coreRadius, (int) tip.x);
            int maxY = Math.max(origin.getY() + coreRadius, (int) tip.y);
            int maxZ = Math.max(origin.getZ() + coreRadius, (int) tip.z);

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Vec3 blockPos = new Vec3(x, y, z);
                        if(!level.getBlockState(convertVec3toBlockPos(blockPos)).isAir()) {
                            continue;
                        }
                        Vec3 relativePos = blockPos.subtract(originBottomCorner);
                        double fraction = Mth.clamp(relativePos.dot(ray) / ray.dot(ray), 0, 1);
                        Vec3 projectedPos = ray.scale(fraction);
                        double projectionDistance = relativePos.distanceTo(projectedPos);
                        double radius = (1 - fraction) * coreRadius;
                        if (projectionDistance < radius) {
                            setBlock(level, convertVec3toBlockPos(blockPos), baseBlock);
                        }
                    }
                }


            }

        }
        return true;
    }

    private BlockPos convertVec3toBlockPos(Vec3 blockPos) {
        return new BlockPos((int) blockPos.x, (int) blockPos.y, (int) blockPos.z);
    }
}
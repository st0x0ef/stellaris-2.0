package org.exodusstudio.stellaris.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

import java.util.List;

public class RocketLaunchPadBlockEntity extends BlockEntity {
    private static final float MAX_BAR_ANGLE = (float) (Math.PI / 2.0);
    private static final float BAR_STEP = MAX_BAR_ANGLE / 200.0F;

    private float barAngle = 0F;

    public RocketLaunchPadBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
    }

    public  RocketLaunchPadBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(BlockEntitiesRegistry.ROCKET_LAUNCH_PAD.get(), blockPos, blockState);
    }

    public float getBarAngle() {
        return barAngle;
    }

    /** Client-side: eases the service-bar angle toward its target so it opens/closes smoothly. */
    public void clientTick() {
        if (this.level == null) {
            return;
        }

        float target = targetBarAngle();

        if (this.barAngle < target) {
            this.barAngle = Math.min(this.barAngle + BAR_STEP, target);
        } else if (this.barAngle > target) {
            this.barAngle = Math.max(this.barAngle - BAR_STEP, target);
        }
    }

    private float targetBarAngle() {
        AABB box = AABB.ofSize(Vec3.atCenterOf(this.worldPosition.above(2)), 3.0, 8.0, 3.0);
        List<RocketEntity> rockets = this.level.getEntitiesOfClass(RocketEntity.class, box, RocketEntity::isAlive);

        if (rockets.isEmpty()) {
            return 0F;
        }

        float progress = Mth.clamp(rockets.getFirst().getTimer() / 200.0F, 0F, 1F);
        return progress * MAX_BAR_ANGLE;
    }
}

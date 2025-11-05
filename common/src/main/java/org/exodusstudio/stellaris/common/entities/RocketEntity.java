package org.exodusstudio.stellaris.common.entities;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RocketEntity extends VehicleEntity {

    public static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);;

    public RocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FUEL, 0);
    }

    @Override
    public int getFuel() {
        return this.entityData.get(FUEL);
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity entity) {
        return super.getPassengerRidingPosition(entity).subtract(0, 3f, 0);
    }

    @Override
    public Pose getRiderPose() {
        return Pose.STANDING;
    }
}

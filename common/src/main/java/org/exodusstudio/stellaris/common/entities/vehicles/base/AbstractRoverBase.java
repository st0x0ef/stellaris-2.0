package org.exodusstudio.stellaris.common.entities.vehicles.base;


import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.utils.GravityUtils;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractRoverBase extends IVehicleEntity {

    private final InterpolationHandler interpolation = new InterpolationHandler(this);

    protected float deltaRotation;

    private static final float STEERING_SMOOTHING = 0.2F;

    private float wheelRotation;

    private static final float FULL_STEERING_SPEED = 0.2F;
    private static final float AIR_RESISTANCE = 0.002F;
    private static final float FLUID_SPEED_DRAG = 0.85F;
    private static final double AIR_VERTICAL_DRAG = 0.98D;
    private static final double FLUID_VERTICAL_DRAG = 0.8D;

    private float speed;

    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(AbstractRoverBase.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FORWARD = SynchedEntityData.defineId(AbstractRoverBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BACKWARD = SynchedEntityData.defineId(AbstractRoverBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEFT = SynchedEntityData.defineId(AbstractRoverBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RIGHT = SynchedEntityData.defineId(AbstractRoverBase.class, EntityDataSerializers.BOOLEAN);

    private static final float BLOCKS_PER_FUEL_UNIT = 25F;

    private float distanceSinceFuelConsumption = 0F;

    public AbstractRoverBase(EntityType type, Level worldIn) {
        super(type, worldIn);

        this.blocksBuilding = true;
        recalculateBoundingBox();
    }

    public abstract float getMaxSpeed();

    public abstract float getMaxReverseSpeed();

    public abstract float getAcceleration();

    public abstract float getBrakingForce();

    public abstract float getRollResistance();

    public abstract float getMaxRotationSpeed();

    public abstract float getPitch();

    @Override
    public void tick() {
        super.tick();

        Runnable task;
        while ((task = tasks.poll()) != null) {
            task.run();
        }

        this.interpolation.interpolate();

        if (simulatesMovement()) {
            applyRoverGravity();
            controlRover();
            checkPush();

            Vec3 positionBeforeMove = position();
            move(MoverType.SELF, getDeltaMovement());
            loseSpeedOnImpact(positionBeforeMove);
            trackFuelConsumption(positionBeforeMove);

            if (!level().isClientSide()) {
                this.xo = getX();
                this.yo = getY();
                this.zo = getZ();
            }
        }
        else {
            deltaRotation = Mth.wrapDegrees(getYRot() - yRotO);
        }

        updateWheelRotation();
    }

    private boolean simulatesMovement() {
        return !level().isClientSide() || isLocalInstanceAuthoritative();
    }

    @Override
    public InterpolationHandler getInterpolation() {
        return this.interpolation;
    }

    public void centerCar() {
        Direction facing = getDirection();
        switch (facing) {
            case SOUTH:
                setYRot(0F);
                break;
            case NORTH:
                setYRot(180F);
                break;
            case EAST:
                setYRot(-90F);
                break;
            case WEST:
                setYRot(90F);
                break;
        }
    }

    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    @Override
    public boolean canCollideWith(Entity entityIn) {
        if (!level().isClientSide() && entityIn instanceof LivingEntity && !getPassengers().contains(entityIn)) {
            if (entityIn.getBoundingBox().intersects(getBoundingBox())) {
                float speed = getSpeed();
                if (speed > 0.35F) {
                    float damage = speed * 10;
                    tasks.add(() -> {
                        ServerLevel serverLevel = (ServerLevel) level();
                        Optional<Holder.Reference<DamageType>> holder = serverLevel.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).get(DamageTypes.DROWN);
                        holder.ifPresent(damageTypeReference -> entityIn.hurt(new DamageSource(damageTypeReference, this), damage));
                    });
                }
            }
        }

        return (entityIn.canBeCollidedWith(this) || entityIn.isPushable()) && !isPassengerOfSameVehicle(entityIn);
    }


    public void checkPush() {
        List<Player> list = level().getEntitiesOfClass(Player.class, getBoundingBox().expandTowards(0.2, 0, 0.2).expandTowards(-0.2, 0, -0.2));

        for (Player player : list) {
            if (!player.hasPassenger(this) && player.isShiftKeyDown()) {
                double motX = calculateMotionX(0.05F, player.getYRot());
                double motZ = calculateMotionZ(0.05F, player.getYRot());
                move(MoverType.PLAYER, new Vec3(motX, 0, motZ));
                return;
            }
        }
    }

    public void controlRover() {
        if (!isVehicle()) {
            setForward(false);
            setBackward(false);
            setLeft(false);
            setRight(false);
        }

        boolean grounded = onGround();
        float speed = computeSpeed(grounded);
        setSpeed(speed);

        steer(grounded ? speed : 0F);

        setDeltaMovement(calculateMotionX(speed, getYRot()), getDeltaMovement().y, calculateMotionZ(speed, getYRot()));
    }

    private void steer(float speed) {
        this.yRotO = this.getYRot();

        float grip = Mth.clamp(Math.abs(speed) / FULL_STEERING_SPEED, 0F, 1F);
        float rotationSpeed = getMaxRotationSpeed() * grip * Math.signum(speed);

        float targetRotation = 0;
        if (isLeft()) {
            targetRotation -= rotationSpeed;
        }
        if (isRight()) {
            targetRotation += rotationSpeed;
        }

        deltaRotation += (targetRotation - deltaRotation) * STEERING_SMOOTHING;

        setYRot(getYRot() + deltaRotation);
        float delta = Math.abs(getYRot() - yRotO);

        while (getYRot() > 180F) {
            setYRot(getYRot() - 360F);
            yRotO = getYRot() - delta;
        }
        while (getYRot() <= -180F) {
            setYRot(getYRot() + 360F);
            yRotO = delta + getYRot();
        }
    }

    private float computeSpeed(boolean grounded) {
        float speed = getSpeed();

        boolean throttle = grounded && hasFuel();
        boolean forward = throttle && isForward();
        boolean backward = throttle && isBackward();

        if (forward && !backward) {
            speed = speed < 0 ? Math.min(speed + getBrakingForce(), 0F) : accelerate(speed, getMaxSpeed());
        }
        else if (backward && !forward) {
            speed = speed > 0 ? Math.max(speed - getBrakingForce(), 0F) : -accelerate(-speed, getMaxReverseSpeed());
        }
        else {
            speed = towardsZero(speed, grounded ? getRollResistance() : AIR_RESISTANCE);
        }

        if (isInWater() || isInLava()) {
            speed *= FLUID_SPEED_DRAG;
        }

        return speed;
    }

    private float accelerate(float speed, float max) {
        return speed > max ? Math.max(speed - getRollResistance(), max) : Math.min(speed + getAcceleration(), max);
    }

    private static float towardsZero(float value, float amount) {
        return value > 0 ? Math.max(value - amount, 0F) : Math.min(value + amount, 0F);
    }

    private void loseSpeedOnImpact(Vec3 positionBeforeMove) {
        if (!horizontalCollision) {
            return;
        }

        double movedX = getX() - positionBeforeMove.x;
        double movedZ = getZ() - positionBeforeMove.z;
        float moved = (float) Math.sqrt(movedX * movedX + movedZ * movedZ);

        float speed = getSpeed();
        if (Math.abs(speed) > moved) {
            setSpeed(Math.copySign(moved, speed));
        }
    }

    private void trackFuelConsumption(Vec3 positionBeforeMove) {
        if (level().isClientSide() || !(isForward() || isBackward())) {
            return;
        }

        double movedX = getX() - positionBeforeMove.x;
        double movedZ = getZ() - positionBeforeMove.z;
        distanceSinceFuelConsumption += (float) Math.sqrt(movedX * movedX + movedZ * movedZ);

        int units = (int) (distanceSinceFuelConsumption / BLOCKS_PER_FUEL_UNIT);
        if (units > 0) {
            distanceSinceFuelConsumption -= units * BLOCKS_PER_FUEL_UNIT;
            consumeFuel(units);
        }
    }

    /** Whether there is any fuel left to drive on. */
    protected abstract boolean hasFuel();

    /** Removes {@code amount} units (mB) of fuel from the tank. */
    protected abstract void consumeFuel(int amount);

    public boolean canPlayerDriveCar(Player player) {
        if (player.equals(getDriver())) {
            return true;
        }
        else if (isInWater() || isInLava()) {
            return false;
        }
        else {
            return false;
        }
    }

    private void applyRoverGravity() {
        Vec3 motion = getDeltaMovement();
        if (isNoGravity()) {
            setDeltaMovement(motion.x, 0D, motion.z);
            return;
        }

        double gravity = GravityUtils.getEntityGravity(GravityUtils.GRAVITY_LIVING_CONVERSION_RATE, this);
        double drag = isInWater() || isInLava() ? FLUID_VERTICAL_DRAG : AIR_VERTICAL_DRAG;
        setDeltaMovement(motion.x, (motion.y - gravity) * drag, motion.z);
    }

    public void updateControls(boolean forward, boolean backward, boolean left, boolean right) {
        boolean needsUpdate = false;

        if (isForward() != forward) {
            setForward(forward);
            needsUpdate = true;
        }

        if (isBackward() != backward) {
            setBackward(backward);
            needsUpdate = true;
        }

        if (isLeft() != left) {
            setLeft(left);
            needsUpdate = true;
        }

        if (isRight() != right) {
            setRight(right);
            needsUpdate = true;
        }
        if (level().isClientSide() && needsUpdate) {
            NetworkManager.sendToServer(new org.exodusstudio.stellaris.common.network.packets.SyncRoverPacket(forward, backward, left, right, this.getUUID()));
        }
    }

    public abstract double getPlayerYOffset();

    public boolean canPlayerEnterCar(Player player) {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 vec3) {
        if (!player.isShiftKeyDown()) {
            if (player.getVehicle() != this) {
                if (!level().isClientSide()) {
                    player.startRiding(this);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!canPlayerEnterCar(player)) {
            return InteractionResult.FAIL;
        }
        return super.interact(player, hand, vec3);
    }

    public float getKilometerPerHour() {
        return (getSpeed() * 20 * 60 * 60) / 1000;
    }

    public float getWheelRotationAmount() {
        return 120F * getSpeed();
    }

    public void updateWheelRotation() {
        wheelRotation += getWheelRotationAmount();
    }

    public float getWheelRotation(float partialTicks) {
        return wheelRotation + getWheelRotationAmount() * partialTicks;
    }

    public boolean isAccelerating() {
        return (isForward() || isBackward()) && !horizontalCollision;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SPEED, 0F);
        builder.define(FORWARD, false);
        builder.define(BACKWARD, false);
        builder.define(LEFT, false);
        builder.define(RIGHT, false);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        if (!level().isClientSide()) {
            this.entityData.set(SPEED, speed);
        }
    }

    public float getSpeed() {
        return simulatesMovement() ? this.speed : this.entityData.get(SPEED);
    }

    public void setForward(boolean forward) {
        entityData.set(FORWARD, forward);
    }

    public boolean isForward() {
        if (getDriver() == null || !canPlayerDriveCar(getDriver())) {
            return false;
        }
        return entityData.get(FORWARD);
    }

    public void setBackward(boolean backward) {
        entityData.set(BACKWARD, backward);
    }

    public boolean isBackward() {
        if (getDriver() == null || !canPlayerDriveCar(getDriver())) {
            return false;
        }
        return entityData.get(BACKWARD);
    }

    public void setLeft(boolean left) {
        entityData.set(LEFT, left);
    }

    public boolean isLeft() {
        return entityData.get(LEFT);
    }

    public void setRight(boolean right) {
        entityData.set(RIGHT, right);
    }

    public boolean isRight() {
        return entityData.get(RIGHT);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }

    @Override
    public float maxUpStep() {
        return 1f;
    }

    public void recalculateBoundingBox() {
        double width = getCarWidth();
        double height = getCarHeight();
        setBoundingBox(new AABB(getX() - width / 2D, getY(), getZ() - width / 2D, getX() + width / 2D, getY() + height, getZ() + width / 2D));
    }

    public double getCarWidth() {
        return 1.3D;
    }

    public double getCarHeight() {
        return 1.6D;
    }

    //I Drive
    public Player getDriver() {
        List<Entity> passengers = getPassengers();
        if (passengers.isEmpty()) {
            return null;
        }

        if (passengers.getFirst() instanceof Player) {
            return (Player) passengers.getFirst();
        }

        return null;
    }

    public abstract int getPassengerSize();

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < getPassengerSize();
    }

    protected void applyYawToEntity(Entity entityToUpdate) {
        entityToUpdate.setYBodyRot(getYRot());
        float f = Mth.wrapDegrees(entityToUpdate.getYRot() - getYRot());
        float f1 = Mth.clamp(f, -130.0F, 130.0F);
        entityToUpdate.yRotO += f1 - f;
        entityToUpdate.setYRot(entityToUpdate.getYRot() + f1 - f);
        entityToUpdate.setYHeadRot(entityToUpdate.getYRot());
    }

    @Override
    public void onPassengerTurned(Entity entityToUpdate) {
        this.applyYawToEntity(entityToUpdate);
    }

    public abstract Vector3d[] getPlayerOffsets();

    @Override
    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (!hasPassenger(passenger)) {
            return;
        }

        double front = 0.0F;
        double side = 0.0F;
        double height = 0.0F;

        List<Entity> passengers = getPassengers();

        if (!passengers.isEmpty()) {
            int i = passengers.indexOf(passenger);

            Vector3d offset = getPlayerOffsets()[i];
            front = offset.x;
            side = offset.z;
            height = offset.y;
        }

        Vec3 vec3d = (new Vec3(front, height, side)).yRot(-getYRot() * 0.017453292F - ((float) Math.PI / 2F));
        moveFunction.accept(passenger, getX() + vec3d.x, getY() + vec3d.y + 0.15D, getZ() + vec3d.z);
        passenger.setYRot(passenger.getYRot() + deltaRotation);
        passenger.setYHeadRot(passenger.getYHeadRot() + this.deltaRotation);
        applyYawToEntity(passenger);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return getDriver();
    }

    public boolean displayFireAnimation() {
        return false;
    }

    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return isAlive();
    }

    public static double calculateMotionX(float speed, float rotationYaw) {
        return Mth.sin(-rotationYaw * 0.017453292F) * speed;
    }

    public static double calculateMotionZ(float speed, float rotationYaw) {
        return Mth.cos(rotationYaw * 0.017453292F) * speed;
    }

    public abstract boolean doesEnterThirdPerson();

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        Direction direction = getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(entity);
        }
        int[][] offsets = DismountHelper.offsetsForDirection(direction);
        AABB bb = entity.getLocalBoundsForPose(Pose.STANDING);
        AABB carBB = getBoundingBox();
        for (int[] offset : offsets) {
            Vec3 dismountPos = new Vec3(getX() + (double) offset[0] * (carBB.getXsize() / 2D + bb.getXsize() / 2D + 1D / 16D), getY(), getZ() + (double) offset[1] * (carBB.getXsize() / 2D + bb.getXsize() / 2D + 1D / 16D));
            double y = level().getBlockFloorHeight(new BlockPos((int) dismountPos.x, (int) dismountPos.y, (int) dismountPos.z));
            if (DismountHelper.isBlockFloorValid(y)) {
                if (DismountHelper.canDismountTo(level(), entity, bb.move(dismountPos))) {
                    return dismountPos;
                }
            }
        }
        return super.getDismountLocationForPassenger(entity);
    }
}

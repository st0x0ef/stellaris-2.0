package org.exodusstudio.stellaris.common.entities.mobs;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.network.packets.ParasiteCameraShakePacket;
import org.exodusstudio.stellaris.common.registries.AdvancementTriggerRegistry;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;

import java.util.EnumSet;

// Holy crap this sucker took FOREVER

public class LunarParasiteEntity extends Monster {
    private static final EntityDataAccessor<Boolean> ATTACHED = SynchedEntityData.defineId(LunarParasiteEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HOST_ID = SynchedEntityData.defineId(LunarParasiteEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> INFECTING = SynchedEntityData.defineId(LunarParasiteEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(LunarParasiteEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int MAX_ATTACH_TICKS = 20 * 38;
    private static final int VILLAGER_CONVERSION_TICKS = 20 * 12;
    private static final int INFECT_WINDUP_TICKS = 12;
    private static final int FAILED_ATTACH_COOLDOWN_TICKS = 16;
    private static final int POST_CONVERSION_COOLDOWN_TICKS = 40;
    private static final int DROP_REATTACH_COOLDOWN_TICKS = 36;
    private static final int CHARGE_WINDUP_TICKS = 14;
    private static final int CHARGE_TICKS = 16;
    private static final int CHARGE_COOLDOWN_TICKS = 22;

    private static final int INFECT_ANIMATION_TICKS = 10;
    private static final int MOVE_ANIMATION_LINGER_TICKS = 2;

    private static final double ATTACHED_PLAYER_FORWARD_OFFSET = 0.56D;
    private static final double ATTACHED_MOB_FORWARD_OFFSET = 0.52D;

    private static final double ATTACHED_PLAYER_HEIGHT_FRACTION = 0.64D;
    private static final double ATTACHED_MOB_HEIGHT_FRACTION = 0.70D;

    private static final double ATTACHED_PLAYER_CAMERA_CLEARANCE = 0.46D;

    private static final double INFECT_CANCEL_DISTANCE_SQR = 6.25D;
    private static final double CHARGE_START_DISTANCE_SQR = 7.5D * 7.5D;
    private static final double CHARGE_ABORT_DISTANCE_SQR = 11.0D * 11.0D;
    private static final double CHARGE_ATTACH_DISTANCE_SQR = 0.82D * 0.82D;
    private static final double CHARGE_SPEED = 0.78D;
    private static final double CHARGE_TARGET_HEIGHT_FRACTION = 0.58D;
    private static final double CHARGE_CONTACT_INFLATE = 0.03D;
    private static final double DROP_HORIZONTAL_SPEED = 0.18D;
    private static final double DROP_VERTICAL_SPEED = -0.12D;

    private static final double ATTACHED_SIDE_BASE_OFFSET = 0.12D;
    private static final double ATTACHED_SIDE_WOBBLE_AMOUNT = 0.012D;
    private static final double ATTACHED_VERTICAL_BOB_AMOUNT = 0.006D;

    private static final double ATTACHED_HITBOX_HALF_WIDTH = 0.22D;
    private static final double ATTACHED_HITBOX_DOWN = 0.18D;
    private static final double ATTACHED_HITBOX_UP = 0.30D;

    private static final double DEATH_DROP_VELOCITY = -0.18D;
    private static final double DEATH_HOST_VELOCITY_CARRY = 0.12D;

    private static final double MAX_SAFE_HOST_HORIZONTAL_SPEED_SQR = 0.42D * 0.42D;
    private static final double MAX_SAFE_HOST_VERTICAL_SPEED = 0.32D;

    private static final double MOVE_POSITION_DELTA_THRESHOLD_SQR = 0.0000012D;
    private static final double MOVE_VELOCITY_THRESHOLD_SQR = 0.0000012D;
    private static final double WALK_ANIMATION_SPEED_THRESHOLD = 0.001D;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState moveAnimationState = new AnimationState();
    public final AnimationState infectAnimationState = new AnimationState();
    public final AnimationState attachedAnimationState = new AnimationState();

    private int attachedTicks;

    private int infectAnimationTicks = 0;
    private int moveAnimationLingerTicks = 0;

    private int infectWindupTicks = 0;
    private int pendingInfectHostId = -1;
    private int failedAttachCooldown = 0;
    private int postConversionCooldown = 0;
    private int chargeWindupTicks = 0;
    private int chargeTicks = 0;
    private int chargeCooldownTicks = 0;
    private int chargeTargetId = -1;

    private boolean wasAttachedClient = false;
    private boolean wasInfectingClient = false;

    private boolean deathPhysicsReleased = false;

    public LunarParasiteEntity(EntityType<? extends LunarParasiteEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.ATTACK_DAMAGE, 1.5)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25);
    }

    public boolean isAttached() {
        return this.entityData.get(ATTACHED);
    }

    public boolean isInfecting() {
        return this.entityData.get(INFECTING);
    }

    public static boolean checkLunarParasiteSpawnRules(
            EntityType<LunarParasiteEntity> entityType,
            ServerLevelAccessor level,
            EntitySpawnReason spawnReason,
            BlockPos pos,
            RandomSource random
    ) {
        if (!Monster.checkAnyLightMonsterSpawnRules(entityType, level, spawnReason, pos, random)) {
            return false;
        }

        if (pos.getY() <= 48) {
            return true;
        }

        return random.nextFloat() < 0.25F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteChargeAttachGoal(this, 1.02D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.65));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACHED, false);
        builder.define(HOST_ID, -1);
        builder.define(INFECTING, false);
        builder.define(MOVING, false);
    }

    @Override
    public void tick() {
        if (this.isAlive() && this.deathTime <= 0 && this.isAttached()) {
            Entity entity = this.level().getEntity(this.entityData.get(HOST_ID));

            if (entity instanceof LivingEntity host && host.isAlive()) {
                this.updateAttachedTransform(host);
            }
        }

        super.tick();

        if (this.deathTime > 0 || !this.isAlive()) {
            this.releaseDeathPhysics();

            if (this.level().isClientSide()) {
                this.updateAnimationStates();
            }

            return;
        }

        if (this.postConversionCooldown > 0) {
            this.postConversionCooldown--;
        }

        if (this.failedAttachCooldown > 0) {
            this.failedAttachCooldown--;
        }

        if (this.chargeCooldownTicks > 0) {
            this.chargeCooldownTicks--;
        }

        if (this.isAttached()) {
            this.tickAttached();
        } else if (this.level() instanceof ServerLevel serverLevel) {
            if (this.chargeWindupTicks > 0) {
                this.tickChargeWindup();
            } else if (this.chargeTicks > 0) {
                this.tickCharge();
            } else if (this.infectWindupTicks > 0) {
                this.tickInfectWindup();
            } else {
                if (this.tickCount % 8 == 0) {
                    serverLevel.sendParticles(
                            ParticleTypes.SCULK_CHARGE_POP,
                            this.getX(),
                            this.getY() + 0.15,
                            this.getZ(),
                            1,
                            0.1,
                            0.05,
                            0.1,
                            0.01
                    );
                }
            }

            this.updateMovingData();
        }

        if (this.level().isClientSide()) {
            this.updateAnimationStates();
        }
    }

    private void releaseDeathPhysics() {
        if (this.deathPhysicsReleased) {
            return;
        }

        boolean wasControlled = this.isAttached() || this.isInfecting() || this.noPhysics || this.isNoGravity();

        if (!wasControlled) {
            return;
        }

        this.deathPhysicsReleased = true;

        Entity host = this.level().getEntity(this.entityData.get(HOST_ID));
        Vec3 hostVelocity = host != null ? host.getDeltaMovement() : Vec3.ZERO;

        this.entityData.set(ATTACHED, false);
        this.entityData.set(HOST_ID, -1);
        this.entityData.set(INFECTING, false);

        this.attachedTicks = 0;
        this.infectWindupTicks = 0;
        this.pendingInfectHostId = -1;
        this.failedAttachCooldown = 0;
        this.clearCharge();
        this.chargeCooldownTicks = 0;
        this.entityData.set(MOVING, false);

        this.noPhysics = false;
        this.setNoGravity(false);
        this.setTarget(null);
        this.getNavigation().stop();
        this.refreshDimensions();

        this.setDeltaMovement(
                hostVelocity.x * DEATH_HOST_VELOCITY_CARRY,
                Math.min(hostVelocity.y, 0.0D) + DEATH_DROP_VELOCITY,
                hostVelocity.z * DEATH_HOST_VELOCITY_CARRY
        );
    }

    private void updateAnimationStates() {
        if (this.deathTime > 0 || !this.isAlive()) {
            this.idleAnimationState.stop();
            this.moveAnimationState.stop();
            this.infectAnimationState.stop();
            this.attachedAnimationState.stop();

            this.infectAnimationTicks = 0;
            this.moveAnimationLingerTicks = 0;
            return;
        }

        boolean attachedNow = this.isAttached();

        if (attachedNow && !this.wasAttachedClient) {
            this.startInfectAnimation();
        }

        this.wasAttachedClient = attachedNow;

        boolean infectingNow = this.isInfecting();

        if (infectingNow && !this.wasInfectingClient) {
            this.startInfectAnimation();
        }

        this.wasInfectingClient = infectingNow;

        if (this.infectAnimationTicks > 0) {
            this.infectAnimationTicks--;
        }

        if (this.isInfecting() || this.infectAnimationTicks > 0) {
            this.idleAnimationState.stop();
            this.moveAnimationState.stop();
            this.attachedAnimationState.stop();
            this.infectAnimationState.startIfStopped(this.tickCount);

            this.moveAnimationLingerTicks = 0;
            return;
        }

        this.infectAnimationState.stop();

        if (this.isAttached()) {
            this.idleAnimationState.stop();
            this.moveAnimationState.stop();
            this.attachedAnimationState.startIfStopped(this.tickCount);

            this.moveAnimationLingerTicks = 0;
            return;
        }

        this.attachedAnimationState.stop();

        if (this.isMoveAnimationWanted()) {
            this.idleAnimationState.stop();
            this.moveAnimationState.startIfStopped(this.tickCount);
        } else {
            this.moveAnimationState.stop();
            this.idleAnimationState.startIfStopped(this.tickCount);
        }
    }

    private void startInfectAnimation() {
        this.infectAnimationTicks = INFECT_ANIMATION_TICKS;

        this.idleAnimationState.stop();
        this.moveAnimationState.stop();
        this.attachedAnimationState.stop();
        this.infectAnimationState.start(this.tickCount);

        this.moveAnimationLingerTicks = 0;
    }

    private boolean isMoveAnimationWanted() {
        double velocityX = this.getDeltaMovement().x;
        double velocityZ = this.getDeltaMovement().z;
        double velocitySqr = velocityX * velocityX + velocityZ * velocityZ;

        double positionDeltaX = this.getX() - this.xo;
        double positionDeltaZ = this.getZ() - this.zo;
        double positionDeltaSqr = positionDeltaX * positionDeltaX + positionDeltaZ * positionDeltaZ;

        boolean physicallyMoving = this.entityData.get(MOVING)
                || positionDeltaSqr > MOVE_POSITION_DELTA_THRESHOLD_SQR
                || velocitySqr > MOVE_VELOCITY_THRESHOLD_SQR
                || this.walkAnimation.speed() > WALK_ANIMATION_SPEED_THRESHOLD;

        if (physicallyMoving) {
            this.moveAnimationLingerTicks = MOVE_ANIMATION_LINGER_TICKS;
        } else if (this.moveAnimationLingerTicks > 0) {
            this.moveAnimationLingerTicks--;
        }

        return physicallyMoving || this.moveAnimationLingerTicks > 0;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        if (target instanceof LivingEntity livingEntity && this.canInfectHost(livingEntity)) {
            if (this.canStartCharge(livingEntity)) {
                this.startCharge(livingEntity);
            }

            return true;
        }

        return super.doHurtTarget(level, target);
    }

    private boolean canStartCharge(LivingEntity host) {
        return !this.isAttached()
                && !this.isInfecting()
                && this.chargeWindupTicks <= 0
                && this.chargeTicks <= 0
                && this.chargeCooldownTicks <= 0
                && this.failedAttachCooldown <= 0
                && this.postConversionCooldown <= 0
                && this.canInfectHost(host);
    }

    private void startCharge(LivingEntity host) {
        if (!this.canStartCharge(host)) {
            return;
        }

        this.clearInfectWindup();

        this.chargeTargetId = host.getId();
        this.chargeWindupTicks = CHARGE_WINDUP_TICKS;
        this.chargeTicks = 0;
        this.entityData.set(INFECTING, true);

        this.noPhysics = false;
        this.setNoGravity(false);
        this.getNavigation().stop();
        this.stopHorizontalAttachMovement();
        this.fallDistance = 0.0F;
        this.swing(InteractionHand.MAIN_HAND, true);
    }

    private void tickChargeWindup() {
        Entity entity = this.level().getEntity(this.chargeTargetId);

        if (!(entity instanceof LivingEntity host) || !host.isAlive() || !this.canInfectHost(host)) {
            this.failCharge();
            return;
        }

        if (this.distanceToSqr(host) > CHARGE_ABORT_DISTANCE_SQR || !this.hasLineOfSight(host)) {
            this.failCharge();
            return;
        }

        this.noPhysics = false;
        this.setNoGravity(false);
        this.getNavigation().stop();
        this.stopHorizontalAttachMovement();
        this.getLookControl().setLookAt(host, 45.0F, 45.0F);

        Vec3 direction = this.getChargeTargetPoint(host).subtract(this.getChargeOriginPoint());

        if (direction.horizontalDistanceSqr() > 0.0001D) {
            this.faceChargeVelocity(direction);
        }

        this.chargeWindupTicks--;

        if (this.chargeWindupTicks <= 0) {
            this.beginChargeFlight(host);
        }
    }

    private void beginChargeFlight(LivingEntity host) {
        if (!host.isAlive() || !this.canInfectHost(host)) {
            this.failCharge();
            return;
        }

        this.entityData.set(INFECTING, false);
        this.chargeTicks = CHARGE_TICKS;
        this.noPhysics = false;
        this.setNoGravity(true);
        this.getNavigation().stop();
        this.fallDistance = 0.0F;
    }

    private void tickCharge() {
        Entity entity = this.level().getEntity(this.chargeTargetId);

        if (!(entity instanceof LivingEntity host) || !host.isAlive() || !this.canInfectHost(host)) {
            this.failCharge();
            return;
        }

        if (this.distanceToSqr(host) > CHARGE_ABORT_DISTANCE_SQR) {
            this.failCharge();
            return;
        }

        this.getNavigation().stop();
        this.setNoGravity(true);
        this.noPhysics = false;
        this.fallDistance = 0.0F;

        if (this.isCloseEnoughToAttach(host)) {
            this.clearCharge();
            this.attachTo(host);
            return;
        }

        Vec3 direction = this.getChargeTargetPoint(host).subtract(this.getChargeOriginPoint());

        if (direction.lengthSqr() < 0.0001D) {
            this.clearCharge();
            this.attachTo(host);
            return;
        }

        Vec3 velocity = direction.normalize().scale(CHARGE_SPEED);
        this.setDeltaMovement(velocity);
        this.hurtMarked = true;
        this.faceChargeVelocity(velocity);

        this.chargeTicks--;

        if (this.chargeTicks <= 0) {
            if (this.isCloseEnoughToAttach(host)) {
                this.clearCharge();
                this.attachTo(host);
            } else {
                this.failCharge();
            }
        }
    }

    private void failCharge() {
        this.clearCharge();
        this.stopHorizontalAttachMovement();
        this.hurtMarked = true;
        this.chargeCooldownTicks = CHARGE_COOLDOWN_TICKS;
        this.failedAttachCooldown = Math.max(this.failedAttachCooldown, FAILED_ATTACH_COOLDOWN_TICKS);
    }

    private void clearCharge() {
        this.chargeWindupTicks = 0;
        this.chargeTicks = 0;
        this.chargeTargetId = -1;

        if (this.pendingInfectHostId == -1) {
            this.entityData.set(INFECTING, false);
        }

        if (!this.isAttached() && !this.isInfecting()) {
            this.setNoGravity(false);
        }
    }

    private boolean isCharging() {
        return this.chargeTicks > 0;
    }

    private boolean isChargingUp() {
        return this.chargeWindupTicks > 0;
    }

    private boolean isCloseEnoughToAttach(LivingEntity host) {
        return this.getChargeOriginPoint().distanceToSqr(this.getChargeTargetPoint(host)) <= CHARGE_ATTACH_DISTANCE_SQR
                || this.getBoundingBox().inflate(CHARGE_CONTACT_INFLATE).intersects(host.getBoundingBox().inflate(CHARGE_CONTACT_INFLATE));
    }

    private Vec3 getChargeOriginPoint() {
        return this.position().add(0.0D, this.getBbHeight() * 0.45D, 0.0D);
    }

    private Vec3 getChargeTargetPoint(LivingEntity host) {
        return host.position().add(0.0D, host.getBbHeight() * CHARGE_TARGET_HEIGHT_FRACTION, 0.0D);
    }

    private void faceChargeVelocity(Vec3 velocity) {
        float yaw = (float)(Mth.atan2(velocity.z, velocity.x) * Mth.RAD_TO_DEG) - 90.0F;

        this.setYRot(yaw);
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
    }

    private void updateMovingData() {
        double positionDeltaX = this.getX() - this.xo;
        double positionDeltaZ = this.getZ() - this.zo;
        double positionDeltaSqr = positionDeltaX * positionDeltaX + positionDeltaZ * positionDeltaZ;
        double velocitySqr = this.getDeltaMovement().horizontalDistanceSqr();

        boolean moving = !this.isAttached()
                && !this.isInfecting()
                && (this.isCharging()
                || positionDeltaSqr > MOVE_POSITION_DELTA_THRESHOLD_SQR
                || velocitySqr > MOVE_VELOCITY_THRESHOLD_SQR);

        if (this.entityData.get(MOVING) != moving) {
            this.entityData.set(MOVING, moving);
        }
    }

    private boolean beginInfectWindup(LivingEntity host) {
        if (this.isAttached() || this.isInfecting() || this.failedAttachCooldown > 0 || this.postConversionCooldown > 0) {
            return false;
        }

        if (!this.canInfectHost(host)) {
            return false;
        }

        this.infectWindupTicks = INFECT_WINDUP_TICKS;
        this.pendingInfectHostId = host.getId();
        this.entityData.set(INFECTING, true);

        this.getNavigation().stop();
        this.stopHorizontalAttachMovement();

        this.swing(InteractionHand.MAIN_HAND, true);
        return true;
    }

    private void tickInfectWindup() {
        Entity entity = this.level().getEntity(this.pendingInfectHostId);

        if (!(entity instanceof LivingEntity host) || !host.isAlive() || !this.canInfectHost(host)) {
            this.clearInfectWindup(FAILED_ATTACH_COOLDOWN_TICKS);
            return;
        }

        this.getNavigation().stop();
        this.stopHorizontalAttachMovement();

        double dx = host.getX() - this.getX();
        double dz = host.getZ() - this.getZ();

        if (dx * dx + dz * dz > INFECT_CANCEL_DISTANCE_SQR) {
            this.clearInfectWindup(FAILED_ATTACH_COOLDOWN_TICKS);
            return;
        }

        this.getLookControl().setLookAt(host, 45.0F, 45.0F);

        this.infectWindupTicks--;

        if (this.infectWindupTicks <= 0) {
            this.clearInfectWindup();
            this.attachTo(host);
        }
    }

    private void clearInfectWindup() {
        this.clearInfectWindup(0);
    }

    private void clearInfectWindup(int cooldownTicks) {
        this.infectWindupTicks = 0;
        this.pendingInfectHostId = -1;
        this.entityData.set(INFECTING, false);
        this.failedAttachCooldown = Math.max(this.failedAttachCooldown, cooldownTicks);

        if (!this.isAttached()) {
        this.noPhysics = false;
        this.setNoGravity(false);
        this.refreshDimensions();
    }
    }

    private void stopHorizontalAttachMovement() {
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(0.0D, Math.min(movement.y, 0.0D), 0.0D);
    }

    private boolean canInfectHost(LivingEntity host) {
        if (host.is(TagsRegistry.EntityTags.INFECTION_IMMUNE)) {
            return false;
        }

        return !(host instanceof Player player) || !MoonLoreUtils.isPlayerImmunisedToInfection(player);
    }

    @Override
    protected void actuallyHurt(ServerLevel level, DamageSource source, float amount) {
        boolean wasAttached = this.isAttached();

        super.actuallyHurt(level, source, amount);

        if (amount > 0.0F && wasAttached && this.isAlive()) {
            this.dropFromHostAfterHit(source);
        }
    }

    public void attachTo(LivingEntity host) {
        if (this.isAttached()) {
            return;
        }

        if (!this.canInfectHost(host)) {
            return;
        }

        this.clearInfectWindup();
        this.clearCharge();
        this.chargeCooldownTicks = CHARGE_COOLDOWN_TICKS;

        this.entityData.set(ATTACHED, true);
        this.entityData.set(HOST_ID, host.getId());
        this.attachedTicks = 0;

        this.noPhysics = true;
        this.setNoGravity(true);
        this.setTarget(null);
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
        this.entityData.set(MOVING, false);
        this.swing(InteractionHand.MAIN_HAND, true);

        this.updateAttachedTransform(host);

        host.addEffect(new MobEffectInstance(EffectsRegistry.getHolder(EffectsRegistry.INFECTED), 20 * 18, 0));

        if (host instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new ParasiteCameraShakePacket(26, 1.4F));
            AdvancementTriggerRegistry.PARASITE_ATTACHED.get().trigger(serverPlayer);
        }

        this.level().playSound(
                null,
                host.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.HOSTILE,
                0.8F,
                1.45F
        );
    }

    private void tickAttached() {
        Entity entity = this.level().getEntity(this.entityData.get(HOST_ID));

        if (!(entity instanceof LivingEntity host) || !host.isAlive()) {
            this.detachFromMissingHost();
            return;
        }

        this.attachedTicks++;

        this.updateAttachedTransform(host);

        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            if (this.attachedTicks % 32 == 0) {
                Vec3 hostVelocityBeforeDamage = host.getDeltaMovement();

                host.hurtServer(serverLevel, this.damageSources().mobAttack(this), 1.0F);
                host.setDeltaMovement(this.sanitizeHostVelocity(hostVelocityBeforeDamage));

                host.addEffect(new MobEffectInstance(EffectsRegistry.getHolder(EffectsRegistry.INFECTED), 20 * 20, 0));

                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        host.getX(),
                        host.getY() + host.getBbHeight() * 0.7,
                        host.getZ(),
                        8,
                        0.25,
                        0.25,
                        0.25,
                        0.02
                );

                if (host instanceof ServerPlayer serverPlayer) {
                    NetworkManager.sendToPlayer(serverPlayer, new ParasiteCameraShakePacket(10, 0.55F));
                }
            }

            if (host instanceof AbstractVillager villager && this.attachedTicks >= VILLAGER_CONVERSION_TICKS) {
                this.finishVillagerConversion(serverLevel, villager);
                return;
            }
        }

        if (this.attachedTicks >= MAX_ATTACH_TICKS) {
            this.detachFromHost();
        }
    }

    private void updateAttachedTransform(LivingEntity host) {
        this.setDeltaMovement(Vec3.ZERO);

        float hostBodyYaw = host.yBodyRot;
        float attachedYaw = Mth.wrapDegrees(hostBodyYaw + 180.0F);
        this.setYRot(attachedYaw);
        this.yRotO = attachedYaw;
        this.yBodyRot = attachedYaw;
        this.yBodyRotO = attachedYaw;
        this.yHeadRot = attachedYaw;
        this.yHeadRotO = attachedYaw;

        float yaw = (float) Math.toRadians(hostBodyYaw);

        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        double sideX = Math.cos(yaw);
        double sideZ = Math.sin(yaw);

        double forwardOffset = this.getAttachedForwardOffset(host);
        double heightOffset = this.getAttachedHeightOffset(host);

        double side = this.getAttachedBaseSideOffset()
                + Math.sin(this.attachedTicks * 0.23F) * ATTACHED_SIDE_WOBBLE_AMOUNT;

        double verticalBob = Math.sin(this.attachedTicks * 0.19F) * ATTACHED_VERTICAL_BOB_AMOUNT;

        double attachedX = host.getX() + forwardX * forwardOffset + sideX * side;
        double attachedY = host.getY() + heightOffset + verticalBob;
        double attachedZ = host.getZ() + forwardZ * forwardOffset + sideZ * side;

        this.setPos(attachedX, attachedY, attachedZ);
        this.setOldPosAndRot();

        this.noPhysics = true;
        this.setNoGravity(true);
        this.setDeltaMovement(Vec3.ZERO);
        this.updateAttachedHitbox();
    }

    private double getAttachedForwardOffset(LivingEntity host) {
        return host instanceof Player ? ATTACHED_PLAYER_FORWARD_OFFSET : ATTACHED_MOB_FORWARD_OFFSET;
    }

    private double getAttachedHeightOffset(LivingEntity host) {
        double hostHeight = host.getBbHeight();

        double wantedHeight = hostHeight * (host instanceof Player
                ? ATTACHED_PLAYER_HEIGHT_FRACTION
                : ATTACHED_MOB_HEIGHT_FRACTION);

        if (host instanceof Player) {
            wantedHeight = Math.min(wantedHeight, host.getEyeHeight() - ATTACHED_PLAYER_CAMERA_CLEARANCE);
        }

        double minHeight = hostHeight * 0.48D;
        double maxHeight = hostHeight * 0.74D;

        return Mth.clamp(wantedHeight, minHeight, maxHeight);
    }

    private double getAttachedBaseSideOffset() {
        return (this.getId() & 1) == 0 ? ATTACHED_SIDE_BASE_OFFSET : -ATTACHED_SIDE_BASE_OFFSET;
    }

    private void finishVillagerConversion(ServerLevel serverLevel, AbstractVillager villager) {
        float yaw = villager.getYRot();
        double radians = Math.toRadians(yaw);

        double detachX = villager.getX() - Math.sin(radians) * 0.75D;
        double detachY = villager.getY() + 0.05D;
        double detachZ = villager.getZ() + Math.cos(radians) * 0.75D;

        ParasiteAffectedVillagerEntity.convertFrom(serverLevel, villager, EntitySpawnReason.CONVERSION);

        this.entityData.set(ATTACHED, false);
        this.entityData.set(HOST_ID, -1);
        this.entityData.set(INFECTING, false);

        this.attachedTicks = 0;
        this.infectWindupTicks = 0;
        this.pendingInfectHostId = -1;
        this.failedAttachCooldown = 0;
        this.clearCharge();
        this.chargeCooldownTicks = CHARGE_COOLDOWN_TICKS;
        this.postConversionCooldown = POST_CONVERSION_COOLDOWN_TICKS;

        this.noPhysics = false;
        this.setNoGravity(false);
        this.setTarget(null);
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
        this.entityData.set(MOVING, false);
        this.setPos(detachX, detachY, detachZ);
        this.refreshDimensions();
    }

    private void detachFromHost() {
        this.entityData.set(ATTACHED, false);
        this.entityData.set(HOST_ID, -1);
        this.entityData.set(INFECTING, false);

        this.attachedTicks = 0;
        this.infectWindupTicks = 0;
        this.pendingInfectHostId = -1;
        this.failedAttachCooldown = 0;
        this.clearCharge();
        this.chargeCooldownTicks = CHARGE_COOLDOWN_TICKS;
        this.postConversionCooldown = 20;

        this.noPhysics = false;
        this.setNoGravity(false);
        this.setTarget(null);
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
        this.entityData.set(MOVING, false);
        this.refreshDimensions();
    }

    private void detachFromMissingHost() {
        this.detachFromHost();
    }

    private void dropFromHostAfterHit(DamageSource source) {
        Entity host = this.level().getEntity(this.entityData.get(HOST_ID));

        if (host instanceof LivingEntity livingHost && livingHost.isAlive()) {
            this.updateAttachedTransform(livingHost);
        }

        Entity attacker = source.getEntity() != null ? source.getEntity() : source.getDirectEntity();
        LivingEntity nextTarget = attacker instanceof LivingEntity livingAttacker && this.canInfectHost(livingAttacker)
                ? livingAttacker
                : null;

        this.entityData.set(ATTACHED, false);
        this.entityData.set(HOST_ID, -1);
        this.entityData.set(INFECTING, false);

        this.attachedTicks = 0;
        this.infectWindupTicks = 0;
        this.pendingInfectHostId = -1;
        this.clearCharge();
        this.failedAttachCooldown = DROP_REATTACH_COOLDOWN_TICKS;
        this.chargeCooldownTicks = DROP_REATTACH_COOLDOWN_TICKS;
        this.postConversionCooldown = DROP_REATTACH_COOLDOWN_TICKS;

        this.noPhysics = false;
        this.setNoGravity(false);
        this.getNavigation().stop();
        this.entityData.set(MOVING, false);
        this.refreshDimensions();
        this.setOldPosAndRot();

        this.setTarget(nextTarget);

        Vec3 dropDirection = Vec3.ZERO;

        if (attacker != null) {
            dropDirection = this.position().subtract(attacker.position()).multiply(1.0D, 0.0D, 1.0D);
        }

        if (dropDirection.lengthSqr() < 0.0001D && host != null) {
            dropDirection = this.position().subtract(host.position()).multiply(1.0D, 0.0D, 1.0D);
        }

        if (dropDirection.lengthSqr() < 0.0001D) {
            dropDirection = Vec3.directionFromRotation(0.0F, this.getYRot()).multiply(1.0D, 0.0D, 1.0D);
        }

        Vec3 dropVelocity = dropDirection.normalize().scale(DROP_HORIZONTAL_SPEED).add(0.0D, DROP_VERTICAL_SPEED, 0.0D);
        this.setDeltaMovement(dropVelocity);
        this.hurtMarked = true;

        this.level().playSound(
                null,
                this.blockPosition(),
                SoundEvents.SCULK_BLOCK_BREAK,
                SoundSource.HOSTILE,
                0.45F,
                1.55F
        );
    }

    private void updateAttachedHitbox() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        this.setBoundingBox(new AABB(
                x - ATTACHED_HITBOX_HALF_WIDTH,
                y - ATTACHED_HITBOX_DOWN,
                z - ATTACHED_HITBOX_HALF_WIDTH,
                x + ATTACHED_HITBOX_HALF_WIDTH,
                y + ATTACHED_HITBOX_UP,
                z + ATTACHED_HITBOX_HALF_WIDTH
        ));
    }

    private Vec3 sanitizeHostVelocity(Vec3 velocity) {
        double x = velocity.x;
        double y = Mth.clamp(velocity.y, -MAX_SAFE_HOST_VERTICAL_SPEED, MAX_SAFE_HOST_VERTICAL_SPEED);
        double z = velocity.z;

        double horizontalSpeedSqr = x * x + z * z;

        if (horizontalSpeedSqr > MAX_SAFE_HOST_HORIZONTAL_SPEED_SQR) {
            double scale = Math.sqrt(MAX_SAFE_HOST_HORIZONTAL_SPEED_SQR / horizontalSpeedSqr);
            x *= scale;
            z *= scale;
        }

        return new Vec3(x, y, z);
    }

    public void push(Entity entity) {
        if (this.isAttached() || this.isInfecting() || this.isCharging()) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity && this.canInfectHost(livingEntity)) {
            return;
        }

        super.push(entity);
    }

    public void push(double x, double y, double z) {
        if (this.isAttached() || this.isInfecting() || this.isCharging()) {
            return;
        }

        super.push(x * 0.25D, y * 0.25D, z * 0.25D);
    }

    public boolean canCollideWith(Entity entity) {
        return !this.isAttached() && !this.isInfecting() && !this.isCharging();
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    public float getPickRadius() {
        return this.isAttached() ? 0.45F : super.getPickRadius();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("attached_ticks", this.attachedTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.attachedTicks = input.getIntOr("attached_ticks", 0);
        this.infectWindupTicks = 0;
        this.pendingInfectHostId = -1;
        this.failedAttachCooldown = 0;
        this.postConversionCooldown = 0;
        this.clearCharge();
        this.entityData.set(MOVING, false);
        this.deathPhysicsReleased = false;
    }

    @Override
    public boolean isPushable() {
        return !this.isAttached() && !this.isInfecting() && !this.isCharging();
    }

    @Override
    public boolean canBeCollidedWith(Entity entity) {
        return !this.isAttached() && !this.isInfecting() && !this.isCharging() && super.canBeCollidedWith(entity);
    }

    @Override
    public boolean shouldDropExperience() {
        return !this.isAttached() && super.shouldDropExperience();
    }

    private static final class ParasiteChargeAttachGoal extends Goal {
        private final LunarParasiteEntity parasite;
        private final double speedModifier;
        private int pathUpdateTicks;

        private ParasiteChargeAttachGoal(LunarParasiteEntity parasite, double speedModifier) {
            this.parasite = parasite;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.parasite.getTarget();
            return target != null && target.isAlive() && this.parasite.canInfectHost(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.parasite.getTarget();
            return target != null && target.isAlive() && !this.parasite.isAttached() && this.parasite.canInfectHost(target);
        }

        @Override
        public void start() {
            this.pathUpdateTicks = 0;
        }

        @Override
        public void stop() {
            this.pathUpdateTicks = 0;
            this.parasite.getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = this.parasite.getTarget();

            if (target == null || !target.isAlive()) {
                return;
            }

            this.parasite.getLookControl().setLookAt(target, 45.0F, 45.0F);

            if (this.parasite.isCharging() || this.parasite.isChargingUp() || this.parasite.isInfecting()) {
                this.parasite.getNavigation().stop();
                return;
            }

            if (this.parasite.isCloseEnoughToAttach(target) && this.parasite.canStartCharge(target)) {
                this.parasite.startCharge(target);
                return;
            }

            double distanceSqr = this.parasite.distanceToSqr(target);

            if (distanceSqr <= CHARGE_START_DISTANCE_SQR && this.parasite.hasLineOfSight(target) && this.parasite.canStartCharge(target)) {
                this.parasite.startCharge(target);
                return;
            }

            if (--this.pathUpdateTicks <= 0) {
                this.pathUpdateTicks = 4 + this.parasite.getRandom().nextInt(4);
                this.parasite.getNavigation().moveTo(target, this.speedModifier);
            }
        }
    }
}

package org.exodusstudio.stellaris.common.entities.mobs.starcrawler;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

// I think it turned out pretty cool. Sorry that I didn't write any comments!
// -Fishguy (not used to working in a team)
public class StarCrawlerEntity extends Monster {
    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(StarCrawlerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> ATTACK_STATE_START_TIME =
            SynchedEntityData.defineId(StarCrawlerEntity.class, EntityDataSerializers.LONG);

    public static final int LEAP_WINDUP_TICKS = 12;
    public static final int WHIPLASH_TICKS = 8;
    public static final int RECOVERY_TICKS = 14;

    public static final float IDLE_SPIN_SPEED = 0.0F;
    public static final float PURSUIT_SPIN_SPEED = 5.0F;
    public static final float ATTACK_SPIN_SPEED = 28.0F;
    public static final float WHIPLASH_SPIN_SPEED = 12.0F;
    public static final float RECOVERY_SPIN_SPEED = 0.0F;
    public static final float CONTACT_DAMAGE_SPIN_THRESHOLD = 18.0F;
    public static final float LIMB_TUCK_SPIN_START_SPEED = 10.0F;
    private static final float SPIN_ACCELERATION = 1.4F;
    private static final float SPIN_DECELERATION = 2.8F;
    private static final float RECOVERY_SPIN_DECELERATION = 5.5F;
    private static final float CLIENT_MOVEMENT_ACCEL_BLEND = 0.42F;
    private static final float CLIENT_MOVEMENT_DECEL_BLEND = 0.30F;
    private static final float BODY_YAW_COMPENSATION_START_SPEED = 10.0F;
    private static final float BODY_YAW_COMPENSATION_FULL_SPEED = 22.0F;
    private static final float ORGANIC_SPIN_PULSE = 0.55F;
    private static final float NORMAL_SPIN_START_HORIZONTAL_SPEED = 0.065F;

    private static final int LEAP_DESTINATION_LOCK_TICK = 9;
    private static final int LEAP_MIN_AIRBORNE_TICKS = 3;
    private static final int LEAP_STEERING_TICKS = 4;
    private static final int LEAP_STUCK_TIMEOUT_TICKS = 5;
    private static final int LEAP_TIMEOUT_TICKS = 32;

    private static final int ATTACK_COOLDOWN_MIN_TICKS = 70;
    private static final int ATTACK_COOLDOWN_MAX_TICKS = 110;
    private static final int CONTACT_COOLDOWN_TICKS = 20;
    private static final int RETALIATION_DAMAGE_WINDOW_TICKS = 50;
    private static final int RETALIATION_REQUEST_TIMEOUT_TICKS = 80;
    private static final int RETALIATION_DELAY_MIN_TICKS = 3;
    private static final int RETALIATION_DELAY_MAX_TICKS = 9;
    private static final int ATTACK_DEFLECT_SOUND_COOLDOWN_TICKS = 4;
    private static final float RETALIATION_DAMAGE_THRESHOLD = 6.0F;

    private static final double LEAP_MIN_RANGE = 3.5D;
    private static final double LEAP_MAX_RANGE = 10.0D;
    private static final double LEAP_MAX_VERTICAL_DIFFERENCE = 3.0D;
    private static final double RETALIATION_LEAP_MIN_RANGE = 0.5D;
    private static final double LEAP_MIN_AIM_DISTANCE = 2.0D;
    private static final double LEAP_MIN_RANGE_SQR = LEAP_MIN_RANGE * LEAP_MIN_RANGE;
    private static final double LEAP_MAX_RANGE_SQR = LEAP_MAX_RANGE * LEAP_MAX_RANGE;
    private static final double RETALIATION_LEAP_MIN_RANGE_SQR =
            RETALIATION_LEAP_MIN_RANGE * RETALIATION_LEAP_MIN_RANGE;
    private static final double LEAP_PREDICTION_TICKS = 4.0D;
    private static final double LEAP_MAX_PREDICTION = 2.25D;
    private static final double LEAP_VERTICAL_VELOCITY = 0.62D;
    private static final double LEAP_MIN_HORIZONTAL_VELOCITY = 0.30D;
    private static final double LEAP_MAX_HORIZONTAL_VELOCITY = 0.68D;
    private static final double LEAP_STEERING_STRENGTH = 0.08D;
    private static final double LEAP_CONTACT_INFLATION = 0.35D;

    private static final float LEAP_CONTACT_DAMAGE = 6.5F;
    private static final int LEAP_POISON_TICKS = 4 * 20;
    private static final double LEAP_KNOCKBACK = 0.65D;

    private static final float WHIPLASH_DAMAGE = 4.5F;
    private static final double WHIPLASH_START_RADIUS = 0.5D;
    private static final double WHIPLASH_END_RADIUS = 4.5D;
    private static final double WHIPLASH_BAND_WIDTH = 0.75D;
    private static final double WHIPLASH_KNOCKBACK = 0.82D;
    private static final double WHIPLASH_UPWARD_VELOCITY = 0.22D;
    private static final int WHIPLASH_SLOWNESS_TICKS = 30;

    private static final int TOUCH_POISON_TICKS = 3 * 20;
    private static final float SPINNING_TOUCH_DAMAGE = 1.0F;
    private static final double SPINNING_TOUCH_KNOCKBACK = 0.24D;

    private static final double PATH_SPEED = 1.12D;
    private static final double PURSUIT_CURVE_OFFSET = 0.55D;
    public static final float FULL_SPIN_HORIZONTAL_SPEED = 0.18F;
    private static final double CLIENT_PARTICLE_DISTANCE = 40.0D;

    public final AnimationState attackAnimationState = new AnimationState();

    private final Set<UUID> leapHitTargets = new HashSet<>();
    private final Set<UUID> whiplashHitTargets = new HashSet<>();
    private final Map<UUID, Integer> contactCooldowns = new HashMap<>();

    private Vec3 leapDestination;
    private Vec3 lastLeapPosition = Vec3.ZERO;
    private Vec3 impactCenter;
    private LivingEntity retaliationTarget;
    private UUID attackTargetId;
    private UUID retaliationTargetId;
    private int leapStuckTicks;
    private int attackCooldown;
    private int spinSoundCooldown;
    private int damagePressureTicks;
    private int retaliationDelayTicks;
    private int retaliationRequestTicks;
    private int attackDeflectSoundCooldown;
    private float recentDamagePressure;
    private float serverSpinVelocity;

    private float previousSpinAngle;
    private float spinAngle;
    private float previousSpinVelocity;
    private float spinVelocity;
    private float previousSmoothedHorizontalSpeed;
    private float smoothedHorizontalSpeed;
    private float previousLeanX;
    private float leanX;
    private float previousLeanZ;
    private float leanZ;
    private float visualSpinDirection = 1.0F;
    private float visualSpinPhase;
    private float lastVisualBodyYaw;
    private boolean clientSpinInitialized;

    public StarCrawlerEntity(EntityType<? extends StarCrawlerEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 8;
        this.attackCooldown = this.randomAttackCooldown();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.15D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_STATE, AttackState.NORMAL.id);
        builder.define(ATTACK_STATE_START_TIME, -1L);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StarCrawlerCombatGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.72D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 9.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.tickClientVisuals();
            return;
        }

        if (!(this.level() instanceof ServerLevel serverLevel) || !this.isAlive()) {
            return;
        }

        this.updateServerSpinVelocity();
        this.tickRetaliationPressure();

        if (this.attackDeflectSoundCooldown > 0) {
            this.attackDeflectSoundCooldown--;
        }

        if (this.attackCooldown > 0 && this.getAttackState() == AttackState.NORMAL) {
            this.attackCooldown--;
        }

        if (this.tickCount % CONTACT_COOLDOWN_TICKS == 0 && !this.contactCooldowns.isEmpty()) {
            this.contactCooldowns.entrySet().removeIf(entry -> entry.getValue() <= this.tickCount);
        }

        AttackState state = this.getAttackState();
        if ((state == AttackState.LEAP_WINDUP || state == AttackState.LEAPING)
                && !this.hasValidTransientAttackTarget()) {
            this.resetAttackToNormal();
            return;
        }

        switch (state) {
            case NORMAL -> this.tickNormalSound(serverLevel);
            case LEAP_WINDUP -> this.tickLeapWindup(serverLevel);
            case LEAPING -> this.tickLeaping(serverLevel);
            case WHIPLASH -> this.tickWhiplash(serverLevel);
            case RECOVERY -> this.tickRecovery();
        }
    }

    public AttackState getAttackState() {
        return AttackState.byId(this.entityData.get(ATTACK_STATE));
    }

    public int getAttackStateTicks() {
        long startTime = this.entityData.get(ATTACK_STATE_START_TIME);
        return this.getAttackState() == AttackState.NORMAL || startTime < 0L
                ? 0
                : (int) Mth.clamp(this.level().getGameTime() - startTime, 0L, Integer.MAX_VALUE);
    }

    public float getSpinAngle(float partialTick) {
        return Mth.rotLerp(partialTick, this.previousSpinAngle, this.spinAngle);
    }

    public float getSpinVelocity(float partialTick) {
        return Mth.lerp(partialTick, this.previousSpinVelocity, this.spinVelocity)
                * this.visualSpinDirection;
    }

    public float getSmoothedHorizontalSpeed(float partialTick) {
        return Mth.lerp(partialTick, this.previousSmoothedHorizontalSpeed, this.smoothedHorizontalSpeed);
    }

    public float getHorizontalLeanX(float partialTick) {
        return Mth.lerp(partialTick, this.previousLeanX, this.leanX);
    }

    public float getHorizontalLeanZ(float partialTick) {
        return Mth.lerp(partialTick, this.previousLeanZ, this.leanZ);
    }

    private boolean canBeginLeap(LivingEntity target) {
        boolean retaliation = this.isRetaliationTarget(target);
        if (this.getAttackState() != AttackState.NORMAL
                || (!retaliation && this.attackCooldown > 0)
                || !this.onGround()
                || !this.isValidAttackTarget(target)
                || !this.hasLineOfSight(target)) {
            return false;
        }

        double horizontalDistanceSqr = this.horizontalDistanceSqr(target.position());
        double minimumRangeSqr = retaliation ? RETALIATION_LEAP_MIN_RANGE_SQR : LEAP_MIN_RANGE_SQR;
        return horizontalDistanceSqr >= minimumRangeSqr
                && horizontalDistanceSqr <= LEAP_MAX_RANGE_SQR
                && Math.abs(target.getY() - this.getY()) <= LEAP_MAX_VERTICAL_DIFFERENCE;
    }

    private void beginLeapWindup(LivingEntity target) {
        if (!this.canBeginLeap(target)) {
            return;
        }

        this.leapDestination = null;
        this.attackTargetId = target.getUUID();
        this.leapHitTargets.clear();
        this.whiplashHitTargets.clear();
        this.leapStuckTicks = 0;
        this.attackCooldown = this.randomAttackCooldown();
        this.clearRetaliationPressure();
        this.getNavigation().stop();
        this.stopHorizontalMovement();
        this.transitionTo(AttackState.LEAP_WINDUP);

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.WARDEN_SONIC_CHARGE,
                    SoundSource.HOSTILE,
                    0.9F,
                    1.35F
            );
        }
    }

    private void tickLeapWindup(ServerLevel serverLevel) {
        LivingEntity target = this.getTarget();
        if (!this.isValidTransientAttackTarget(target)) {
            this.resetAttackToNormal();
            return;
        }

        this.getNavigation().stop();
        this.stopHorizontalMovement();
        this.getLookControl().setLookAt(target, 45.0F, 45.0F);

        int stateTicks = this.getAttackStateTicks();
        if (stateTicks >= LEAP_DESTINATION_LOCK_TICK && this.leapDestination == null) {
            this.leapDestination = this.predictLeapDestination(target);
        }

        if (stateTicks >= LEAP_WINDUP_TICKS - 1) {
            this.launchLeap(serverLevel, target);
        }
    }

    private void launchLeap(ServerLevel serverLevel, LivingEntity target) {
        if (!this.isValidTransientAttackTarget(target) || !this.onGround() || !this.hasLineOfSight(target)) {
            this.resetAttackToNormal();
            return;
        }

        if (this.leapDestination == null) {
            this.leapDestination = this.predictLeapDestination(target);
        }

        Vec3 horizontalOffset = this.leapDestination.subtract(this.position()).multiply(1.0D, 0.0D, 1.0D);
        double horizontalDistance = horizontalOffset.horizontalDistance();
        if (horizontalDistance < LEAP_MIN_AIM_DISTANCE) {
            Vec3 direction = horizontalDistance > 0.001D
                    ? horizontalOffset.scale(1.0D / horizontalDistance)
                    : Vec3.directionFromRotation(0.0F, this.getYRot()).horizontal().normalize();
            horizontalOffset = direction.scale(LEAP_MIN_AIM_DISTANCE);
            horizontalDistance = LEAP_MIN_AIM_DISTANCE;
            this.leapDestination = this.position().add(horizontalOffset);
        }

        double horizontalSpeed = Mth.clamp(
                horizontalDistance * 0.065D,
                LEAP_MIN_HORIZONTAL_VELOCITY,
                LEAP_MAX_HORIZONTAL_VELOCITY
        );
        Vec3 horizontalVelocity = horizontalOffset.normalize().scale(horizontalSpeed);

        this.transitionTo(AttackState.LEAPING);
        this.lastLeapPosition = this.position();
        this.leapStuckTicks = 0;
        this.setOnGround(false);
        this.fallDistance = 0.0F;
        this.setDeltaMovement(horizontalVelocity.x, LEAP_VERTICAL_VELOCITY, horizontalVelocity.z);
        this.hurtMarked = true;

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.BREEZE_JUMP,
                SoundSource.HOSTILE,
                0.95F,
                0.75F
        );
        serverLevel.sendParticles(
                ParticleTypes.PORTAL,
                this.getX(),
                this.getY() + 0.35D,
                this.getZ(),
                10,
                0.45D,
                0.22D,
                0.45D,
                0.08D
        );
    }

    private void tickLeaping(ServerLevel serverLevel) {
        int stateTicks = this.getAttackStateTicks();
        this.getNavigation().stop();

        if (stateTicks < LEAP_STEERING_TICKS) {
            this.applyEarlyLeapSteering();
        }

        this.damageLeapContacts(serverLevel);

        boolean properlyAirborne = stateTicks >= LEAP_MIN_AIRBORNE_TICKS;
        if ((properlyAirborne && this.onGround()) || (stateTicks >= 2 && this.horizontalCollision)) {
            this.triggerImpact(serverLevel);
            return;
        }

        double movementSqr = this.position().distanceToSqr(this.lastLeapPosition);
        if (stateTicks >= 2 && movementSqr < 0.0025D) {
            this.leapStuckTicks++;
        } else {
            this.leapStuckTicks = 0;
        }
        this.lastLeapPosition = this.position();

        if (this.leapStuckTicks >= LEAP_STUCK_TIMEOUT_TICKS || stateTicks >= LEAP_TIMEOUT_TICKS) {
            this.triggerImpact(serverLevel);
        }
    }

    private void applyEarlyLeapSteering() {
        if (this.leapDestination == null) {
            return;
        }

        Vec3 currentVelocity = this.getDeltaMovement();
        Vec3 toDestination = this.leapDestination.subtract(this.position()).multiply(1.0D, 0.0D, 1.0D);
        if (toDestination.horizontalDistanceSqr() < 0.01D) {
            return;
        }

        double speed = Mth.clamp(
                currentVelocity.horizontalDistance(),
                LEAP_MIN_HORIZONTAL_VELOCITY,
                LEAP_MAX_HORIZONTAL_VELOCITY
        );
        Vec3 desired = toDestination.normalize().scale(speed);
        double x = Mth.lerp(LEAP_STEERING_STRENGTH, currentVelocity.x, desired.x);
        double z = Mth.lerp(LEAP_STEERING_STRENGTH, currentVelocity.z, desired.z);
        double horizontalSpeedSqr = x * x + z * z;

        if (horizontalSpeedSqr > LEAP_MAX_HORIZONTAL_VELOCITY * LEAP_MAX_HORIZONTAL_VELOCITY) {
            double scale = LEAP_MAX_HORIZONTAL_VELOCITY / Math.sqrt(horizontalSpeedSqr);
            x *= scale;
            z *= scale;
        }

        this.setDeltaMovement(x, currentVelocity.y, z);
        this.hurtMarked = true;
    }

    private void damageLeapContacts(ServerLevel serverLevel) {
        AABB contactBox = this.getBoundingBox().inflate(LEAP_CONTACT_INFLATION, 0.2D, LEAP_CONTACT_INFLATION);

        for (LivingEntity target : serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                contactBox,
                this::isValidDamageTarget
        )) {
            UUID targetId = target.getUUID();
            if (!this.leapHitTargets.add(targetId)) {
                continue;
            }

            boolean hurt = target.hurtServer(serverLevel, this.damageSources().mobAttack(this), LEAP_CONTACT_DAMAGE);
            if (!hurt) {
                continue;
            }

            target.addEffect(new MobEffectInstance(MobEffects.POISON, LEAP_POISON_TICKS, 0), this);

            Vec3 direction = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D);
            if (direction.horizontalDistanceSqr() < 0.0001D) {
                direction = target.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D);
            }
            if (direction.horizontalDistanceSqr() > 0.0001D) {
                direction = direction.normalize();
                target.push(direction.x * LEAP_KNOCKBACK, 0.25D, direction.z * LEAP_KNOCKBACK);
                target.hurtMarked = true;
            }
        }
    }

    private void triggerImpact(ServerLevel serverLevel) {
        if (this.getAttackState() != AttackState.LEAPING) {
            return;
        }

        this.impactCenter = this.position();
        this.getNavigation().stop();
        Vec3 velocity = this.getDeltaMovement();
        this.setDeltaMovement(velocity.x * 0.08D, Math.min(velocity.y, 0.0D), velocity.z * 0.08D);
        this.hurtMarked = true;
        this.whiplashHitTargets.clear();
        this.attackTargetId = null;
        this.transitionTo(AttackState.WHIPLASH);

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_ATTACK_IMPACT,
                SoundSource.HOSTILE,
                1.25F,
                0.72F
        );
        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.HOSTILE,
                0.75F,
                0.58F
        );

        serverLevel.sendParticles(
                ParticleTypes.PORTAL,
                this.impactCenter.x,
                this.impactCenter.y + 0.2D,
                this.impactCenter.z,
                24,
                1.05D,
                0.28D,
                1.05D,
                0.16D
        );
        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.impactCenter.x,
                this.impactCenter.y + 0.18D,
                this.impactCenter.z,
                16,
                0.85D,
                0.2D,
                0.85D,
                0.08D
        );
        serverLevel.sendParticles(
                ParticleTypes.SMALL_FLAME,
                this.impactCenter.x,
                this.impactCenter.y + 0.12D,
                this.impactCenter.z,
                6,
                0.65D,
                0.12D,
                0.65D,
                0.035D
        );

        this.damageWhiplashRing(serverLevel, 0);
    }

    private void tickWhiplash(ServerLevel serverLevel) {
        this.getNavigation().stop();
        this.stopHorizontalMovement();

        int stateTicks = this.getAttackStateTicks();
        if (stateTicks >= WHIPLASH_TICKS) {
            this.transitionTo(AttackState.RECOVERY);
            return;
        }

        this.damageWhiplashRing(serverLevel, stateTicks);
    }

    private void damageWhiplashRing(ServerLevel serverLevel, int waveTick) {
        if (this.impactCenter == null) {
            this.impactCenter = this.position();
        }

        float progress = WHIPLASH_TICKS <= 1
                ? 1.0F
                : Mth.clamp(waveTick / (float) (WHIPLASH_TICKS - 1), 0.0F, 1.0F);
        double radius = Mth.lerp(progress, WHIPLASH_START_RADIUS, WHIPLASH_END_RADIUS);
        double halfBand = WHIPLASH_BAND_WIDTH * 0.5D;
        double searchRadius = radius + halfBand;

        AABB searchBox = new AABB(
                this.impactCenter.x - searchRadius,
                this.impactCenter.y - 1.5D,
                this.impactCenter.z - searchRadius,
                this.impactCenter.x + searchRadius,
                this.impactCenter.y + 2.0D,
                this.impactCenter.z + searchRadius
        );

        for (LivingEntity target : serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                this::isValidDamageTarget
        )) {
            double dx = target.getX() - this.impactCenter.x;
            double dz = target.getZ() - this.impactCenter.z;
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (Math.abs(distance - radius) > halfBand || !this.whiplashHitTargets.add(target.getUUID())) {
                continue;
            }

            boolean hurt = target.hurtServer(serverLevel, this.damageSources().mobAttack(this), WHIPLASH_DAMAGE);
            if (!hurt) {
                continue;
            }

            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, WHIPLASH_SLOWNESS_TICKS, 0), this);

            if (distance > 0.0001D) {
                double x = dx / distance;
                double z = dz / distance;
                target.push(
                        x * WHIPLASH_KNOCKBACK,
                        WHIPLASH_UPWARD_VELOCITY,
                        z * WHIPLASH_KNOCKBACK
                );
                target.hurtMarked = true;
            }
        }
    }

    private void tickRecovery() {
        this.getNavigation().stop();
        Vec3 velocity = this.getDeltaMovement();
        this.setDeltaMovement(velocity.x * 0.55D, velocity.y, velocity.z * 0.55D);

        if (this.getAttackStateTicks() >= RECOVERY_TICKS) {
            this.attackCooldown = this.randomAttackCooldown();
            this.resetAttackToNormal();
        }
    }

    private void tickNormalSound(ServerLevel serverLevel) {
        if (this.spinSoundCooldown > 0) {
            this.spinSoundCooldown--;
        }

        if (this.spinSoundCooldown <= 0 && this.serverSpinVelocity >= CONTACT_DAMAGE_SPIN_THRESHOLD) {
            serverLevel.playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.BREEZE_WHIRL,
                    SoundSource.HOSTILE,
                    0.16F,
                    1.35F + this.random.nextFloat() * 0.12F
            );
            this.spinSoundCooldown = 34 + this.random.nextInt(18);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        if (this.level() instanceof ServerLevel serverLevel && entity instanceof LivingEntity livingEntity) {
            this.applyVenomousTouch(serverLevel, livingEntity);
        }

        super.doPush(entity);
    }

    private void applyVenomousTouch(ServerLevel serverLevel, LivingEntity target) {
        if (!this.isValidDamageTarget(target)) {
            return;
        }

        int nextAllowedTick = this.contactCooldowns.getOrDefault(target.getUUID(), Integer.MIN_VALUE);
        if (this.tickCount < nextAllowedTick) {
            return;
        }
        this.contactCooldowns.put(target.getUUID(), this.tickCount + CONTACT_COOLDOWN_TICKS);

        target.addEffect(new MobEffectInstance(MobEffects.POISON, TOUCH_POISON_TICKS, 0), this);

        if (this.getAttackState() != AttackState.LEAPING
                && this.serverSpinVelocity >= CONTACT_DAMAGE_SPIN_THRESHOLD) {
            boolean hurt = target.hurtServer(
                    serverLevel,
                    this.damageSources().mobAttack(this),
                    SPINNING_TOUCH_DAMAGE
            );

            if (hurt) {
                Vec3 outward = target.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D);
                if (outward.horizontalDistanceSqr() > 0.0001D) {
                    outward = outward.normalize();
                    target.push(
                            outward.x * SPINNING_TOUCH_KNOCKBACK,
                            0.08D,
                            outward.z * SPINNING_TOUCH_KNOCKBACK
                    );
                    target.hurtMarked = true;
                }
            }
        }

        serverLevel.playSound(
                null,
                target.blockPosition(),
                SoundEvents.PUFFER_FISH_STING,
                SoundSource.HOSTILE,
                0.28F,
                0.78F + this.random.nextFloat() * 0.18F
        );
        serverLevel.sendParticles(
                ParticleTypes.PORTAL,
                target.getX(),
                target.getY() + target.getBbHeight() * 0.55D,
                target.getZ(),
                3,
                0.16D,
                0.2D,
                0.16D,
                0.025D
        );
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        AttackState stateBeforeDamage = this.getAttackState();
        if (this.isLeapProtected(source)) {
            this.playLeapDeflectFeedback(serverLevel);
            return false;
        }

        float healthBefore = this.getHealth();
        boolean hurt = super.hurtServer(serverLevel, source, amount);
        if (hurt
                && this.isAlive()
                && stateBeforeDamage == AttackState.NORMAL
                && !source.is(DamageTypeTags.NO_ANGER)) {
            float damageTaken = Math.max(0.0F, healthBefore - this.getHealth());
            if (damageTaken > 0.0F && source.getEntity() instanceof LivingEntity attacker) {
                this.recordDamagePressure(attacker, damageTaken);
            }
        }
        return hurt;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource source) {
        return super.isInvulnerableTo(serverLevel, source) || this.isLeapProtected(source);
    }

    private boolean isLeapProtected(DamageSource source) {
        AttackState state = this.getAttackState();
        return (state == AttackState.LEAP_WINDUP || state == AttackState.LEAPING)
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && !source.isCreativePlayer();
    }

    private void recordDamagePressure(LivingEntity attacker, float damageTaken) {
        if (this.retaliationRequestTicks > 0 || !this.isValidDamageTarget(attacker)) {
            return;
        }

        UUID attackerId = attacker.getUUID();
        if (this.damagePressureTicks <= 0) {
            this.recentDamagePressure = 0.0F;
            this.damagePressureTicks = RETALIATION_DAMAGE_WINDOW_TICKS;
        }

        this.recentDamagePressure += damageTaken;

        if (this.recentDamagePressure < RETALIATION_DAMAGE_THRESHOLD) {
            return;
        }

        this.retaliationTargetId = attackerId;
        this.retaliationRequestTicks = RETALIATION_REQUEST_TIMEOUT_TICKS;
        this.retaliationDelayTicks = RETALIATION_DELAY_MIN_TICKS + this.random.nextInt(
                RETALIATION_DELAY_MAX_TICKS - RETALIATION_DELAY_MIN_TICKS + 1
        );
        this.recentDamagePressure = 0.0F;
        this.damagePressureTicks = 0;
        this.retaliationTarget = attacker;
        if (this.getAttackState() == AttackState.NORMAL) {
            this.setTarget(attacker);
        }
    }

    private void tickRetaliationPressure() {
        if (this.damagePressureTicks > 0) {
            this.damagePressureTicks--;
            if (this.damagePressureTicks == 0) {
                this.recentDamagePressure = 0.0F;
            }
        }

        if (this.retaliationRequestTicks > 0) {
            this.retaliationRequestTicks--;
            if (this.retaliationDelayTicks > 0) {
                this.retaliationDelayTicks--;
            }
            if (this.retaliationRequestTicks == 0 || !this.isValidAttackTarget(this.retaliationTarget)) {
                this.retaliationRequestTicks = 0;
                this.retaliationDelayTicks = 0;
                this.retaliationTargetId = null;
                this.retaliationTarget = null;
            } else if (this.getAttackState() == AttackState.NORMAL
                    && this.getTarget() != this.retaliationTarget) {
                this.setTarget(this.retaliationTarget);
            }
        }
    }

    private boolean isRetaliationTarget(LivingEntity target) {
        return this.retaliationRequestTicks > 0
                && this.retaliationDelayTicks <= 0
                && this.retaliationTargetId != null
                && target != null
                && this.retaliationTargetId.equals(target.getUUID());
    }

    private void clearRetaliationPressure() {
        this.recentDamagePressure = 0.0F;
        this.damagePressureTicks = 0;
        this.retaliationDelayTicks = 0;
        this.retaliationRequestTicks = 0;
        this.retaliationTargetId = null;
        this.retaliationTarget = null;
    }

    private void playLeapDeflectFeedback(ServerLevel serverLevel) {
        if (this.attackDeflectSoundCooldown > 0) {
            return;
        }

        this.attackDeflectSoundCooldown = ATTACK_DEFLECT_SOUND_COOLDOWN_TICKS;
        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.BREEZE_DEFLECT,
                SoundSource.HOSTILE,
                0.7F,
                0.78F + this.random.nextFloat() * 0.12F
        );
        serverLevel.sendParticles(
                ParticleTypes.PORTAL,
                this.getX(),
                this.getY() + this.getBbHeight() * 0.55D,
                this.getZ(),
                5,
                0.45D,
                0.3D,
                0.45D,
                0.04D
        );
    }

    private void tickClientVisuals() {
        this.updateClientSpinAndLean();
        this.updateClientAnimationStates();

        if (this.level().getNearestPlayer(this, CLIENT_PARTICLE_DISTANCE) == null) {
            return;
        }

        AttackState state = this.getAttackState();
        int stateTicks = this.getAttackStateTicks();

        if (state == AttackState.LEAP_WINDUP && stateTicks % 2 == 0) {
            this.spawnClientWindupRing(stateTicks);
        }

        if (state == AttackState.WHIPLASH && stateTicks < WHIPLASH_TICKS) {
            this.spawnClientWhiplashRing(stateTicks);
        } else if (this.spinVelocity >= CONTACT_DAMAGE_SPIN_THRESHOLD && this.tickCount % 4 == 0) {
            this.spawnClientLimbParticles();
        }
    }

    private void updateClientAnimationStates() {
        AttackState state = this.getAttackState();
        boolean attacking = state == AttackState.LEAP_WINDUP || state == AttackState.LEAPING;

        if (attacking) {
            int animationAge = this.getAttackStateTicks();
            if (state == AttackState.LEAPING) {
                animationAge += LEAP_WINDUP_TICKS;
            }
            int animationStart = this.tickCount - animationAge;
            this.attackAnimationState.startIfStopped(animationStart);
            return;
        }

        this.attackAnimationState.stop();
    }

    private void updateClientSpinAndLean() {
        this.previousSpinAngle = this.spinAngle;
        this.previousSpinVelocity = this.spinVelocity;
        this.previousSmoothedHorizontalSpeed = this.smoothedHorizontalSpeed;
        this.previousLeanX = this.leanX;
        this.previousLeanZ = this.leanZ;

        Vec3 velocity = this.getDeltaMovement();
        float horizontalSpeed = (float) velocity.horizontalDistance();
        float movementBlend = horizontalSpeed > this.smoothedHorizontalSpeed
                ? CLIENT_MOVEMENT_ACCEL_BLEND
                : CLIENT_MOVEMENT_DECEL_BLEND;
        this.smoothedHorizontalSpeed = Mth.lerp(
                movementBlend,
                this.smoothedHorizontalSpeed,
                horizontalSpeed
        );

        this.initializeClientSpinIfNeeded();
        float wantedSpin = this.getClientDesiredSpinVelocity();
        this.spinVelocity = this.easeSpinVelocity(this.spinVelocity, wantedSpin);

        float bodyYawDelta = Mth.wrapDegrees(this.yBodyRot - this.lastVisualBodyYaw);
        this.lastVisualBodyYaw = this.yBodyRot;
        float yawCompensation = smoothStep(
                (this.spinVelocity - BODY_YAW_COMPENSATION_START_SPEED)
                        / (BODY_YAW_COMPENSATION_FULL_SPEED - BODY_YAW_COMPENSATION_START_SPEED)
        );
        this.spinAngle = Mth.wrapDegrees(
                this.spinAngle
                        + this.spinVelocity * this.visualSpinDirection
                        + bodyYawDelta * yawCompensation
        );

        float yaw = this.yBodyRot * Mth.DEG_TO_RAD;
        double localForward = velocity.z * Mth.cos(yaw) + velocity.x * Mth.sin(yaw);
        double localSide = velocity.x * Mth.cos(yaw) - velocity.z * Mth.sin(yaw);
        float leanScale = this.getAttackState() == AttackState.NORMAL ? 0.85F : 0.45F;
        float wantedLeanX = Mth.clamp((float) (-localForward * leanScale), -0.12F, 0.12F);
        float wantedLeanZ = Mth.clamp((float) (localSide * leanScale), -0.12F, 0.12F);
        this.leanX = Mth.lerp(0.25F, this.leanX, wantedLeanX);
        this.leanZ = Mth.lerp(0.25F, this.leanZ, wantedLeanZ);
    }

    private float getDesiredSpinVelocity() {
        return switch (this.getAttackState()) {
            case LEAP_WINDUP, LEAPING -> ATTACK_SPIN_SPEED;
            case WHIPLASH -> WHIPLASH_SPIN_SPEED;
            case RECOVERY -> RECOVERY_SPIN_SPEED;
            case NORMAL -> {
                float speed = (float) this.getDeltaMovement().horizontalDistance();
                float movement = smoothStep(
                        (speed - NORMAL_SPIN_START_HORIZONTAL_SPEED)
                                / (FULL_SPIN_HORIZONTAL_SPEED - NORMAL_SPIN_START_HORIZONTAL_SPEED)
                );
                yield Mth.lerp(movement, IDLE_SPIN_SPEED, PURSUIT_SPIN_SPEED);
            }
        };
    }

    private float getClientDesiredSpinVelocity() {
        if (this.getAttackState() != AttackState.NORMAL) {
            return this.getDesiredSpinVelocity();
        }

        float movement = smoothStep(
                (this.smoothedHorizontalSpeed - NORMAL_SPIN_START_HORIZONTAL_SPEED)
                        / (FULL_SPIN_HORIZONTAL_SPEED - NORMAL_SPIN_START_HORIZONTAL_SPEED)
        );
        float baseSpeed = Mth.lerp(movement, IDLE_SPIN_SPEED, PURSUIT_SPIN_SPEED);
        float pulse = Mth.sin(this.tickCount * 0.16F + this.visualSpinPhase)
                * ORGANIC_SPIN_PULSE
                * movement;
        return baseSpeed + pulse;
    }

    private void updateServerSpinVelocity() {
        float wantedSpin = this.getDesiredSpinVelocity();
        this.serverSpinVelocity = this.easeSpinVelocity(this.serverSpinVelocity, wantedSpin);
    }

    private float easeSpinVelocity(float currentSpin, float wantedSpin) {
        float difference = wantedSpin - currentSpin;
        if (Math.abs(difference) < 0.01F) {
            return wantedSpin;
        }

        float response = difference > 0.0F
                ? 0.24F
                : this.getAttackState() == AttackState.RECOVERY ? 0.32F : 0.18F;
        float maximumChange = this.getSpinAcceleration(currentSpin, wantedSpin);
        float change = Mth.clamp(difference * response, -maximumChange, maximumChange);
        return Math.max(0.0F, currentSpin + change);
    }

    private float getSpinAcceleration(float currentSpin, float wantedSpin) {
        if (wantedSpin > currentSpin) {
            return SPIN_ACCELERATION;
        }
        return this.getAttackState() == AttackState.RECOVERY
                ? RECOVERY_SPIN_DECELERATION
                : SPIN_DECELERATION;
    }

    private void initializeClientSpinIfNeeded() {
        if (this.clientSpinInitialized) {
            return;
        }

        long mixedUuid = this.getUUID().getMostSignificantBits() ^ this.getUUID().getLeastSignificantBits();
        this.visualSpinDirection = (mixedUuid & 1L) == 0L ? 1.0F : -1.0F;
        this.visualSpinPhase = (mixedUuid & 255L) / 255.0F * Mth.TWO_PI;
        this.lastVisualBodyYaw = this.yBodyRot;
        this.clientSpinInitialized = true;
    }

    private static float smoothStep(float value) {
        float clamped = Mth.clamp(value, 0.0F, 1.0F);
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }

    private void spawnClientWindupRing(int stateTicks) {
        float progress = Mth.clamp(stateTicks / (float) (LEAP_WINDUP_TICKS - 1), 0.0F, 1.0F);
        double radius = Mth.lerp(progress, 1.35D, 0.48D);
        double phase = stateTicks * 0.31D;

        for (int i = 0; i < 8; i++) {
            double angle = phase + Mth.TWO_PI * i / 8.0D;
            double x = Mth.cos((float) angle);
            double z = Mth.sin((float) angle);
            ParticleOptions particle = (i & 1) == 0 ? ParticleTypes.PORTAL : ParticleTypes.SCULK_SOUL;
            this.level().addParticle(
                    particle,
                    this.getX() + x * radius,
                    this.getY() + 0.06D,
                    this.getZ() + z * radius,
                    -x * 0.018D,
                    0.012D,
                    -z * 0.018D
            );
        }
    }

    private void spawnClientWhiplashRing(int stateTicks) {
        float progress = WHIPLASH_TICKS <= 1
                ? 1.0F
                : Mth.clamp(stateTicks / (float) (WHIPLASH_TICKS - 1), 0.0F, 1.0F);
        double radius = Mth.lerp(progress, WHIPLASH_START_RADIUS, WHIPLASH_END_RADIUS);
        int particleCount = 20;

        for (int i = 0; i < particleCount; i++) {
            double angle = Mth.TWO_PI * i / particleCount;
            double x = Mth.cos((float) angle);
            double z = Mth.sin((float) angle);
            ParticleOptions particle;

            if (i % 7 == 0) {
                particle = ParticleTypes.SMALL_FLAME;
            } else if ((i & 1) == 0) {
                particle = ParticleTypes.PORTAL;
            } else {
                particle = ParticleTypes.SOUL_FIRE_FLAME;
            }

            this.level().addParticle(
                    particle,
                    this.getX() + x * radius,
                    this.getY() + 0.12D,
                    this.getZ() + z * radius,
                    x * 0.025D,
                    0.018D,
                    z * 0.025D
            );
        }
    }

    private void spawnClientLimbParticles() {
        double baseAngle = (-this.yBodyRot + this.spinAngle) * Mth.DEG_TO_RAD;

        for (int i = 0; i < 2; i++) {
            double angle = baseAngle + i * Math.PI;
            double radius = 1.45D + this.random.nextDouble() * 0.32D;
            double x = Mth.cos((float) angle);
            double z = Mth.sin((float) angle);
            ParticleOptions particle = i == 0 ? ParticleTypes.PORTAL : ParticleTypes.SOUL_FIRE_FLAME;
            this.level().addParticle(
                    particle,
                    this.getX() + x * radius,
                    this.getY() + 0.35D + this.random.nextDouble() * 0.32D,
                    this.getZ() + z * radius,
                    x * 0.015D,
                    0.008D,
                    z * 0.015D
            );
        }
    }

    private Vec3 predictLeapDestination(LivingEntity target) {
        Vec3 prediction = target.getDeltaMovement().multiply(LEAP_PREDICTION_TICKS, 0.0D, LEAP_PREDICTION_TICKS);
        if (prediction.horizontalDistance() > LEAP_MAX_PREDICTION) {
            prediction = prediction.normalize().scale(LEAP_MAX_PREDICTION);
        }
        return target.position().add(prediction.x, 0.0D, prediction.z);
    }

    private void stopHorizontalMovement() {
        Vec3 velocity = this.getDeltaMovement();
        if (Math.abs(velocity.x) > 0.0001D || Math.abs(velocity.z) > 0.0001D) {
            this.setDeltaMovement(0.0D, velocity.y, 0.0D);
            this.hurtMarked = true;
        }
    }

    private double horizontalDistanceSqr(Vec3 position) {
        double dx = position.x - this.getX();
        double dz = position.z - this.getZ();
        return dx * dx + dz * dz;
    }

    private boolean hasValidAttackTarget() {
        return this.isValidAttackTarget(this.getTarget());
    }

    private boolean hasValidTransientAttackTarget() {
        return this.isValidTransientAttackTarget(this.getTarget());
    }

    private boolean isValidTransientAttackTarget(LivingEntity target) {
        return this.attackTargetId != null
                && target != null
                && this.attackTargetId.equals(target.getUUID())
                && this.isValidAttackTarget(target);
    }

    private boolean isValidAttackTarget(LivingEntity target) {
        return target != null && this.isValidDamageTarget(target);
    }

    private boolean isValidDamageTarget(LivingEntity target) {
        return target != this
                && !(target instanceof StarCrawlerEntity)
                && target.isAlive()
                && !target.isRemoved()
                && !this.isAlliedTo(target)
                && this.canAttack(target);
    }

    private int randomAttackCooldown() {
        return ATTACK_COOLDOWN_MIN_TICKS
                + this.random.nextInt(ATTACK_COOLDOWN_MAX_TICKS - ATTACK_COOLDOWN_MIN_TICKS + 1);
    }

    private void transitionTo(AttackState state) {
        if (this.getAttackState() == state) {
            return;
        }

        this.entityData.set(ATTACK_STATE, state.id);
        this.entityData.set(ATTACK_STATE_START_TIME, state == AttackState.NORMAL ? -1L : this.level().getGameTime());
    }

    private void resetAttackToNormal() {
        this.getNavigation().stop();
        this.stopAttackMovement();
        this.leapDestination = null;
        this.impactCenter = null;
        this.attackTargetId = null;
        this.lastLeapPosition = Vec3.ZERO;
        this.leapStuckTicks = 0;
        this.leapHitTargets.clear();
        this.whiplashHitTargets.clear();

        if (this.getAttackState() != AttackState.NORMAL) {
            this.entityData.set(ATTACK_STATE, AttackState.NORMAL.id);
        }
        if (this.entityData.get(ATTACK_STATE_START_TIME) != -1L) {
            this.entityData.set(ATTACK_STATE_START_TIME, -1L);
        }
    }

    private void stopAttackMovement() {
        Vec3 velocity = this.getDeltaMovement();
        this.setDeltaMovement(0.0D, Math.min(velocity.y, 0.12D), 0.0D);
        this.hurtMarked = true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHULKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SHULKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHULKER_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("attack_cooldown", this.attackCooldown);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.attackCooldown = Mth.clamp(
                input.getIntOr("attack_cooldown", this.randomAttackCooldown()),
                0,
                ATTACK_COOLDOWN_MAX_TICKS
        );
        this.contactCooldowns.clear();
        this.clearRetaliationPressure();
        this.resetAttackToNormal();
    }

    public enum AttackState {
        NORMAL(0),
        LEAP_WINDUP(1),
        LEAPING(2),
        WHIPLASH(3),
        RECOVERY(4);

        private static final AttackState[] VALUES = values();
        private final int id;

        AttackState(int id) {
            this.id = id;
        }

        public static AttackState byId(int id) {
            return VALUES[Mth.clamp(id, 0, VALUES.length - 1)];
        }
    }

    private static final class StarCrawlerCombatGoal extends Goal {
        private final StarCrawlerEntity crawler;
        private int pathUpdateTicks;
        private float curveDirection = 1.0F;

        private StarCrawlerCombatGoal(StarCrawlerEntity crawler) {
            this.crawler = crawler;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.crawler.hasValidAttackTarget();
        }

        @Override
        public boolean canContinueToUse() {
            return this.crawler.hasValidAttackTarget();
        }

        @Override
        public void start() {
            this.pathUpdateTicks = 0;
            this.curveDirection = this.crawler.getRandom().nextBoolean() ? 1.0F : -1.0F;
        }

        @Override
        public void stop() {
            this.pathUpdateTicks = 0;
            this.crawler.getNavigation().stop();
            if (!this.crawler.hasValidAttackTarget()
                    && (this.crawler.getAttackState() == AttackState.LEAP_WINDUP
                    || this.crawler.getAttackState() == AttackState.LEAPING)) {
                this.crawler.resetAttackToNormal();
            }
        }

        @Override
        public void tick() {
            LivingEntity target = this.crawler.getTarget();
            if (!this.crawler.isValidAttackTarget(target)) {
                return;
            }

            this.crawler.getLookControl().setLookAt(target, 35.0F, 35.0F);

            if (this.crawler.getAttackState() != AttackState.NORMAL) {
                this.crawler.getNavigation().stop();
                return;
            }

            if (this.crawler.canBeginLeap(target)) {
                this.crawler.beginLeapWindup(target);
                return;
            }

            if (this.crawler.tickCount % 32 == 0) {
                this.curveDirection = -this.curveDirection;
            }

            double distanceSqr = this.crawler.distanceToSqr(target);
            if (--this.pathUpdateTicks <= 0) {
                this.pathUpdateTicks = 5 + this.crawler.getRandom().nextInt(4);
                double dx = target.getX() - this.crawler.getX();
                double dz = target.getZ() - this.crawler.getZ();
                double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

                if (distanceSqr > 4.0D
                        && distanceSqr < 64.0D
                        && horizontalDistance > 0.001D
                        && this.crawler.hasLineOfSight(target)) {
                    double sideX = -dz / horizontalDistance * this.curveDirection * PURSUIT_CURVE_OFFSET;
                    double sideZ = dx / horizontalDistance * this.curveDirection * PURSUIT_CURVE_OFFSET;
                    boolean curvedPathStarted = this.crawler.getNavigation().moveTo(
                            target.getX() + sideX,
                            target.getY(),
                            target.getZ() + sideZ,
                            PATH_SPEED
                    );
                    if (!curvedPathStarted) {
                        this.crawler.getNavigation().moveTo(target, PATH_SPEED);
                    }
                } else {
                    this.crawler.getNavigation().moveTo(target, PATH_SPEED);
                }

            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
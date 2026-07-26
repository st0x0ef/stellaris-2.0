package org.exodusstudio.stellaris.common.entities.mobs;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.utils.InfectionUtils;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;

import java.util.EnumSet;

public class ParasiteAffectedVillagerEntity extends Monster {
    private static final int EVOLUTION_TICKS = 20 * 120;

    private static final byte EVENT_ATTACK_ANIMATION = 61;

    private static final int ATTACK_ANIMATION_TICKS = 19;

    private static final int ATTACK_DAMAGE_FRAME_TICKS = 5;

    private static final int ATTACK_RECOVERY_TICKS = 5;

    private static final double WALK_POSITION_DELTA_THRESHOLD_SQR = 0.0000012D;
    private static final double WALK_VELOCITY_THRESHOLD_SQR = 0.0000012D;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    private int visualAttackAnimationTicks = 0;

    public ParasiteAffectedVillagerEntity(EntityType<? extends ParasiteAffectedVillagerEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
    }

    public static ParasiteAffectedVillagerEntity convertFrom(ServerLevel level, LivingEntity host, EntitySpawnReason spawnReason) {
        ParasiteAffectedVillagerEntity infected = EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER.get().create(level, spawnReason);

        if (infected != null) {
            infected.snapTo(host.getX(), host.getY(), host.getZ(), host.getYRot(), host.getXRot());
            infected.setPersistenceRequired();

            if (host.hasCustomName()) {
                infected.setCustomName(host.getCustomName());
                infected.setCustomNameVisible(host.isCustomNameVisible());
            }

            level.addFreshEntity(infected);
            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    host.getX(),
                    host.getY() + host.getBbHeight() * 0.5D,
                    host.getZ(),
                    20,
                    0.35D,
                    0.45D,
                    0.35D,
                    0.04D
            );
            level.playSound(null, host.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 0.9F, 0.65F);
            host.discard();
        }

        return infected;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new DelayedMeleeAttackGoal(1.0D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.updateAnimationStates();
        }

        if (!this.level().isClientSide() && this.tickCount % 12 == 0 && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SCULK_CHARGE_POP,
                    this.getX(),
                    this.getY() + 1.4D,
                    this.getZ(),
                    2,
                    0.25D,
                    0.35D,
                    0.25D,
                    0.01D
            );
        }

        if (!this.level().isClientSide()
                && this.tickCount > EVOLUTION_TICKS
                && this.getHealth() < this.getMaxHealth() * 0.7F
                && this.random.nextInt(40) == 0) {
            this.evolve();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ATTACK_ANIMATION) {
            this.startVisualAttackAnimation();
            return;
        }

        super.handleEntityEvent(id);
    }

    private void startVisualAttackAnimation() {
        this.visualAttackAnimationTicks = ATTACK_ANIMATION_TICKS;

        this.attackAnimationState.start(this.tickCount);
        this.idleAnimationState.stop();
        this.walkAnimationState.stop();
        this.deathAnimationState.stop();
    }

    private void updateAnimationStates() {
        if (this.deathTime > 0 || !this.isAlive()) {
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.attackAnimationState.stop();
            this.deathAnimationState.startIfStopped(this.tickCount);

            this.visualAttackAnimationTicks = 0;
            return;
        }

        this.deathAnimationState.stop();

        if (this.visualAttackAnimationTicks > 0) {
            this.visualAttackAnimationTicks--;

            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.attackAnimationState.startIfStopped(this.tickCount);
            return;
        }

        this.attackAnimationState.stop();

        if (this.isWalkAnimationWanted()) {
            this.idleAnimationState.stop();
            this.walkAnimationState.startIfStopped(this.tickCount);
        } else {
            this.walkAnimationState.stop();
            this.idleAnimationState.startIfStopped(this.tickCount);
        }
    }

    private boolean isWalkAnimationWanted() {
        double velocityX = this.getDeltaMovement().x;
        double velocityZ = this.getDeltaMovement().z;
        double velocitySqr = velocityX * velocityX + velocityZ * velocityZ;

        double positionDeltaX = this.getX() - this.xo;
        double positionDeltaZ = this.getZ() - this.zo;
        double positionDeltaSqr = positionDeltaX * positionDeltaX + positionDeltaZ * positionDeltaZ;

        return positionDeltaSqr > WALK_POSITION_DELTA_THRESHOLD_SQR
                || velocitySqr > WALK_VELOCITY_THRESHOLD_SQR;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);

        if (hurt && target instanceof LivingEntity livingEntity && !MoonLoreUtils.isImmuneToInfection(livingEntity)) {
            InfectionUtils.infect(livingEntity, 20 * 16);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20 * 4, 0));
        }

        return hurt;
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource damageSource) {
        if (!this.level().isClientSide() && this.random.nextFloat() < 0.35F && this.level() instanceof ServerLevel serverLevel) {
            LunarParasiteEntity parasite = EntityTypesRegistry.LUNAR_PARASITE.get().create(serverLevel, EntitySpawnReason.MOB_SUMMONED);

            if (parasite != null) {
                parasite.snapTo(this.getX(), this.getY() + 0.25D, this.getZ(), this.getYRot(), 0.0F);
                serverLevel.addFreshEntity(parasite);
            }
        }

        super.die(damageSource);
    }

    protected void evolve() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        EvolvedParasiteAffectedVillagerEntity evolved = EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER_EVOLVED.get().create(serverLevel, EntitySpawnReason.CONVERSION);

        if (evolved == null) {
            return;
        }

        evolved.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        evolved.setPersistenceRequired();

        if (this.hasCustomName()) {
            evolved.setCustomName(this.getCustomName());
            evolved.setCustomNameVisible(this.isCustomNameVisible());
        }

        serverLevel.addFreshEntity(evolved);
        serverLevel.sendParticles(
                ParticleTypes.SONIC_BOOM,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 0.8F, 1.8F);
        this.discard();
    }

    private class DelayedMeleeAttackGoal extends Goal {
        private final double speedModifier;

        private int pathUpdateTicks;
        private int attackRecoveryTicks;
        private int attackAnimationFrame;
        private boolean damageApplied;
        private LivingEntity attackTarget;

        private DelayedMeleeAttackGoal(double speedModifier) {
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ParasiteAffectedVillagerEntity.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = ParasiteAffectedVillagerEntity.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.pathUpdateTicks = 0;
            this.attackRecoveryTicks = 0;
            this.attackAnimationFrame = 0;
            this.damageApplied = false;
            this.attackTarget = null;
        }

        @Override
        public void stop() {
            ParasiteAffectedVillagerEntity.this.getNavigation().stop();

            this.pathUpdateTicks = 0;
            this.attackRecoveryTicks = 0;
            this.attackAnimationFrame = 0;
            this.damageApplied = false;
            this.attackTarget = null;
        }

        @Override
        public void tick() {
            LivingEntity target = ParasiteAffectedVillagerEntity.this.getTarget();

            if (target == null || !target.isAlive()) {
                return;
            }

            ParasiteAffectedVillagerEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (this.attackAnimationFrame > 0) {
                this.tickDelayedAttack();
                return;
            }

            if (this.attackRecoveryTicks > 0) {
                this.attackRecoveryTicks--;
            }

            double distanceSqr = ParasiteAffectedVillagerEntity.this.distanceToSqr(target);
            boolean canReach = distanceSqr <= this.getAttackReachSqr(target);

            if (canReach && this.attackRecoveryTicks <= 0) {
                this.beginDelayedAttack(target);
                return;
            }

            this.pathUpdateTicks--;

            if (this.pathUpdateTicks <= 0) {
                this.pathUpdateTicks = 4 + ParasiteAffectedVillagerEntity.this.getRandom().nextInt(7);
                ParasiteAffectedVillagerEntity.this.getNavigation().moveTo(target, this.speedModifier);
            }
        }

        private void beginDelayedAttack(LivingEntity target) {
            this.attackTarget = target;
            this.attackAnimationFrame = 1;
            this.damageApplied = false;

            ParasiteAffectedVillagerEntity.this.getNavigation().stop();
            ParasiteAffectedVillagerEntity.this.level().broadcastEntityEvent(ParasiteAffectedVillagerEntity.this, EVENT_ATTACK_ANIMATION);
        }

        private void tickDelayedAttack() {
            LivingEntity target = this.attackTarget;

            if (target == null || !target.isAlive()) {
                this.clearDelayedAttack();
                return;
            }

            ParasiteAffectedVillagerEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            ParasiteAffectedVillagerEntity.this.getNavigation().stop();

            if (!this.damageApplied && this.attackAnimationFrame >= ATTACK_DAMAGE_FRAME_TICKS) {
                this.damageApplied = true;
                this.tryApplyDelayedDamage(target);
            }

            this.attackAnimationFrame++;

            if (this.attackAnimationFrame > ATTACK_ANIMATION_TICKS) {
                this.clearDelayedAttack();
                this.attackRecoveryTicks = ATTACK_RECOVERY_TICKS;
            }
        }

        private void tryApplyDelayedDamage(LivingEntity target) {
            if (!(ParasiteAffectedVillagerEntity.this.level() instanceof ServerLevel serverLevel)) {
                return;
            }

            if (ParasiteAffectedVillagerEntity.this.distanceToSqr(target) > this.getAttackReachSqr(target) * 1.25D) {
                return;
            }

            if (!ParasiteAffectedVillagerEntity.this.getSensing().hasLineOfSight(target)) {
                return;
            }

            ParasiteAffectedVillagerEntity.this.doHurtTarget(serverLevel, target);
        }

        private void clearDelayedAttack() {
            this.attackTarget = null;
            this.attackAnimationFrame = 0;
            this.damageApplied = false;
        }

        private double getAttackReachSqr(LivingEntity target) {
            double reach = ParasiteAffectedVillagerEntity.this.getBbWidth() * 2.0D + target.getBbWidth();
            return reach * reach;
        }
    }
}
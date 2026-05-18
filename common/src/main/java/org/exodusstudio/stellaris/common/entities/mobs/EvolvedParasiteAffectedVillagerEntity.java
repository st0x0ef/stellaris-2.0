package org.exodusstudio.stellaris.common.entities.mobs;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.network.packets.ParasiteCameraShakePacket;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

import java.util.EnumSet;
import java.util.List;

public class EvolvedParasiteAffectedVillagerEntity extends ParasiteAffectedVillagerEntity {
    private static final EntityDataAccessor<Integer> EVOLVED_ATTACK_VARIANT =
            SynchedEntityData.defineId(EvolvedParasiteAffectedVillagerEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> EVOLVED_ATTACK_START_TICK =
            SynchedEntityData.defineId(EvolvedParasiteAffectedVillagerEntity.class, EntityDataSerializers.INT);

    private static final int ATTACK_NONE = -1;
    private static final int ATTACK_CLAW = 0;
    private static final int ATTACK_TENTACLE = 1;
    private static final int ATTACK_SPIT = 2;

    private static final int CLAW_ATTACK_TICKS = 20;
    private static final int TENTACLE_ATTACK_TICKS = 25;
    private static final int SPIT_ATTACK_TICKS = 19;

    private static final int CLAW_IMPACT_TICK = 13;
    private static final int TENTACLE_IMPACT_TICK = 16;
    private static final int SPIT_IMPACT_TICK = 12;

    public final AnimationState evolvedIdleAnimationState = new AnimationState();
    public final AnimationState evolvedWalkAnimationState = new AnimationState();
    public final AnimationState evolvedAttackAnimationState = new AnimationState();
    public final AnimationState evolvedAttackTentacleAnimationState = new AnimationState();
    public final AnimationState evolvedAttackSpitAnimationState = new AnimationState();
    public final AnimationState evolvedDeathAnimationState = new AnimationState();

    private int currentMoveAnimationMode = -1;
    private int animationSwitchCooldown = 0;
    private int lastSeenAttackStartTick = -1;

    public EvolvedParasiteAffectedVillagerEntity(EntityType<? extends EvolvedParasiteAffectedVillagerEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 16;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ParasiteAffectedVillagerEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 44.0)
                .add(Attributes.MOVEMENT_SPEED, 0.31)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.55);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EVOLVED_ATTACK_VARIANT, ATTACK_NONE);
        builder.define(EVOLVED_ATTACK_START_TICK, -1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new EvolvedAttackGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.updateEvolvedAnimationStates();
            return;
        }

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.tickCount % 28 == 0) {
            List<LivingEntity> entities = this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.getBoundingBox().inflate(4.0D),
                    entity -> entity != this && !entity.getType().is(TagsRegistry.EntityTags.INFECTION_IMMUNE)
            );

            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(EffectsRegistry.getHolder(EffectsRegistry.INFECTED), 20 * 10, 0));

                if (entity instanceof ServerPlayer serverPlayer) {
                    NetworkManager.sendToPlayer(serverPlayer, new ParasiteCameraShakePacket(8, 0.35F));
                }
            }

            serverLevel.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(),
                    this.getY() + 1.3D,
                    this.getZ(),
                    14,
                    0.9D,
                    0.65D,
                    0.9D,
                    0.025D
            );
        }

        if (this.tickCount % (20 * 28) == 0) {
            this.spawnParasite(serverLevel);
        }
    }

    private void updateEvolvedAnimationStates() {
        if (this.deathTime > 0) {
            this.stopNonDeathAnimations();
            this.evolvedDeathAnimationState.startIfStopped(this.tickCount - this.deathTime);
            return;
        }

        this.evolvedDeathAnimationState.stop();

        int attackStartTick = this.entityData.get(EVOLVED_ATTACK_START_TICK);
        int attackVariant = this.entityData.get(EVOLVED_ATTACK_VARIANT);
        int elapsedAttackTicks = attackStartTick >= 0 ? this.tickCount - attackStartTick : Integer.MAX_VALUE;

        boolean attacking = attackVariant != ATTACK_NONE
                && attackStartTick >= 0
                && elapsedAttackTicks >= 0
                && elapsedAttackTicks <= getAttackLengthTicks(attackVariant);

        if (attacking) {
            if (attackStartTick != this.lastSeenAttackStartTick) {
                this.evolvedAttackAnimationState.stop();
                this.evolvedAttackTentacleAnimationState.stop();
                this.evolvedAttackSpitAnimationState.stop();
                this.lastSeenAttackStartTick = attackStartTick;
            }

            this.evolvedIdleAnimationState.stop();
            this.evolvedWalkAnimationState.stop();

            if (attackVariant == ATTACK_CLAW) {
                this.evolvedAttackAnimationState.startIfStopped(attackStartTick);
                this.evolvedAttackTentacleAnimationState.stop();
                this.evolvedAttackSpitAnimationState.stop();
            } else if (attackVariant == ATTACK_TENTACLE) {
                this.evolvedAttackAnimationState.stop();
                this.evolvedAttackTentacleAnimationState.startIfStopped(attackStartTick);
                this.evolvedAttackSpitAnimationState.stop();
            } else if (attackVariant == ATTACK_SPIT) {
                this.evolvedAttackAnimationState.stop();
                this.evolvedAttackTentacleAnimationState.stop();
                this.evolvedAttackSpitAnimationState.startIfStopped(attackStartTick);
            }

            return;
        }

        this.evolvedAttackAnimationState.stop();
        this.evolvedAttackTentacleAnimationState.stop();
        this.evolvedAttackSpitAnimationState.stop();

        int wantedMoveMode = this.getWantedMoveAnimationMode();

        if (this.animationSwitchCooldown > 0) {
            this.animationSwitchCooldown--;
        }

        if (wantedMoveMode != this.currentMoveAnimationMode && this.animationSwitchCooldown <= 0) {
            this.currentMoveAnimationMode = wantedMoveMode;
            this.animationSwitchCooldown = 6;
        }

        if (this.currentMoveAnimationMode == 1) {
            this.evolvedWalkAnimationState.startIfStopped(this.tickCount);
            this.evolvedIdleAnimationState.stop();
        } else {
            this.evolvedIdleAnimationState.startIfStopped(this.tickCount);
            this.evolvedWalkAnimationState.stop();
        }
    }

    private int getWantedMoveAnimationMode() {
        double horizontalSpeedSqr = this.getDeltaMovement().horizontalDistanceSqr();

        if (this.getNavigation().isInProgress() || horizontalSpeedSqr > 0.0025D) {
            return 1;
        }

        return 0;
    }

    private void stopNonDeathAnimations() {
        this.evolvedIdleAnimationState.stop();
        this.evolvedWalkAnimationState.stop();
        this.evolvedAttackAnimationState.stop();
        this.evolvedAttackTentacleAnimationState.stop();
        this.evolvedAttackSpitAnimationState.stop();
    }

    private void startEvolvedAttack(int variant) {
        this.entityData.set(EVOLVED_ATTACK_VARIANT, variant);
        this.entityData.set(EVOLVED_ATTACK_START_TICK, this.tickCount);
        this.swing(InteractionHand.MAIN_HAND);
    }

    private static int getAttackLengthTicks(int variant) {
        return switch (variant) {
            case ATTACK_TENTACLE -> TENTACLE_ATTACK_TICKS;
            case ATTACK_SPIT -> SPIT_ATTACK_TICKS;
            case ATTACK_CLAW -> CLAW_ATTACK_TICKS;
            default -> 0;
        };
    }

    private static int getAttackImpactTick(int variant) {
        return switch (variant) {
            case ATTACK_TENTACLE -> TENTACLE_IMPACT_TICK;
            case ATTACK_SPIT -> SPIT_IMPACT_TICK;
            case ATTACK_CLAW -> CLAW_IMPACT_TICK;
            default -> 0;
        };
    }

    private static int getAttackCooldownTicks(int variant) {
        return switch (variant) {
            case ATTACK_TENTACLE -> 34;
            case ATTACK_SPIT -> 38;
            case ATTACK_CLAW -> 28;
            default -> 30;
        };
    }

    private static double getAttackReach(int variant) {
        return switch (variant) {
            case ATTACK_TENTACLE -> 3.35D;
            case ATTACK_SPIT -> 4.75D;
            case ATTACK_CLAW -> 2.25D;
            default -> 2.0D;
        };
    }

    private int chooseAttackVariant(LivingEntity target) {
        double distanceSqr = this.distanceToSqr(target);

        if (distanceSqr > 9.0D && this.hasLineOfSight(target)) {
            return this.random.nextFloat() < 0.55F ? ATTACK_SPIT : ATTACK_TENTACLE;
        }

        if (distanceSqr > 4.5D) {
            return this.random.nextFloat() < 0.75F ? ATTACK_TENTACLE : ATTACK_SPIT;
        }

        float roll = this.random.nextFloat();

        if (roll < 0.50F) {
            return ATTACK_CLAW;
        }

        if (roll < 0.80F) {
            return ATTACK_TENTACLE;
        }

        return ATTACK_SPIT;
    }

    private boolean doTimedEvolvedDamage(ServerLevel level, LivingEntity target, int variant) {
        if (!target.isAlive() || target.getType().is(TagsRegistry.EntityTags.INFECTION_IMMUNE)) {
            return false;
        }

        boolean hurt = super.doHurtTarget(level, target);

        if (hurt) {
            this.applyEvolvedOnHitEffects(level, target, variant);
        }

        return hurt;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);

        if (hurt && target instanceof LivingEntity livingEntity) {
            this.applyEvolvedOnHitEffects(level, livingEntity, ATTACK_CLAW);
        }

        return hurt;
    }

    private void applyEvolvedOnHitEffects(ServerLevel level, LivingEntity target, int variant) {
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 8, 0));
        target.addEffect(new MobEffectInstance(EffectsRegistry.getHolder(EffectsRegistry.INFECTED), 20 * 8, 0));

        if (variant == ATTACK_TENTACLE) {
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20 * 3, 0));
        }

        if (variant == ATTACK_SPIT) {
            List<LivingEntity> splashTargets = this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    target.getBoundingBox().inflate(2.25D),
                    entity -> entity != this && entity.isAlive() && !entity.getType().is(TagsRegistry.EntityTags.INFECTION_IMMUNE)
            );

            for (LivingEntity splashTarget : splashTargets) {
                splashTarget.addEffect(new MobEffectInstance(EffectsRegistry.getHolder(EffectsRegistry.INFECTED), 20 * 10, 0));
                splashTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 5, 0));

                if (splashTarget instanceof ServerPlayer serverPlayer) {
                    NetworkManager.sendToPlayer(serverPlayer, new ParasiteCameraShakePacket(5, 0.22F));
                }
            }

            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    target.getX(),
                    target.getY() + target.getBbHeight() * 0.5D,
                    target.getZ(),
                    18,
                    0.45D,
                    0.35D,
                    0.45D,
                    0.035D
            );
        }

        if (target instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new ParasiteCameraShakePacket(8, 0.35F));
        }
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource damageSource) {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 2 + this.random.nextInt(2); i++) {
                this.spawnParasite(serverLevel);
            }

            serverLevel.playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.SCULK_SHRIEKER_SHRIEK,
                    SoundSource.HOSTILE,
                    1.0F,
                    0.75F
            );
        }

        super.die(damageSource);
    }

    private void spawnParasite(ServerLevel level) {
        LunarParasiteEntity parasite = EntityTypesRegistry.LUNAR_PARASITE.get().create(level, EntitySpawnReason.MOB_SUMMONED);

        if (parasite == null) {
            return;
        }

        parasite.snapTo(
                this.getX() + (this.random.nextDouble() - 0.5D) * 1.5D,
                this.getY() + 0.2D,
                this.getZ() + (this.random.nextDouble() - 0.5D) * 1.5D,
                this.getYRot(),
                0.0F
        );

        parasite.setDeltaMovement(
                (this.random.nextDouble() - 0.5D) * 0.25D,
                0.15D,
                (this.random.nextDouble() - 0.5D) * 0.25D
        );

        level.addFreshEntity(parasite);
    }

    private static final class EvolvedAttackGoal extends Goal {
        private final EvolvedParasiteAffectedVillagerEntity mob;

        private int attackVariant = ATTACK_NONE;
        private int attackTicks = 0;
        private int attackCooldown = 0;
        private boolean hasDealtDamage = false;

        private EvolvedAttackGoal(EvolvedParasiteAffectedVillagerEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void stop() {
            this.attackVariant = ATTACK_NONE;
            this.attackTicks = 0;
            this.attackCooldown = 10;
            this.hasDealtDamage = false;
            this.mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();

            if (target == null || !target.isAlive()) {
                return;
            }

            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (this.attackCooldown > 0) {
                this.attackCooldown--;
            }

            if (this.attackTicks > 0) {
                this.tickCurrentAttack(target);
                return;
            }

            double distanceSqr = this.mob.distanceToSqr(target);
            int wantedVariant = this.mob.chooseAttackVariant(target);
            double reach = getAttackReach(wantedVariant);

            if (distanceSqr <= reach * reach && this.attackCooldown <= 0) {
                this.startAttack(wantedVariant);
                return;
            }

            this.mob.getNavigation().moveTo(target, 1.15D);
        }

        private void startAttack(int variant) {
            this.attackVariant = variant;
            this.attackTicks = getAttackLengthTicks(variant);
            this.attackCooldown = getAttackCooldownTicks(variant);
            this.hasDealtDamage = false;

            this.mob.getNavigation().stop();
            this.mob.startEvolvedAttack(variant);
        }

        private void tickCurrentAttack(LivingEntity target) {
            this.mob.getNavigation().stop();

            int totalLength = getAttackLengthTicks(this.attackVariant);
            int elapsed = totalLength - this.attackTicks;
            int impactTick = getAttackImpactTick(this.attackVariant);

            if (!this.hasDealtDamage && elapsed >= impactTick) {
                this.hasDealtDamage = true;

                double reach = getAttackReach(this.attackVariant);
                double distanceSqr = this.mob.distanceToSqr(target);

                if (distanceSqr <= reach * reach && this.mob.level() instanceof ServerLevel serverLevel) {
                    this.mob.doTimedEvolvedDamage(serverLevel, target, this.attackVariant);
                }
            }

            this.attackTicks--;

            if (this.attackTicks <= 0) {
                this.attackVariant = ATTACK_NONE;
                this.hasDealtDamage = false;
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
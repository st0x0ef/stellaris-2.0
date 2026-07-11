package org.exodusstudio.stellaris.common.entities.mobs;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.network.packets.ParasiteCameraShakePacket;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;

import java.util.EnumSet;

public class LunaShadowEntity extends Monster {
    private static final byte CLIENT_EVENT_ATTACK = 60;
    private static final byte CLIENT_EVENT_ATTACK_BITE = 61;

    private static final int ATTACK_ANIMATION_TICKS = 25;
    private static final int ATTACK_DAMAGE_DELAY_TICKS = 16;
    private static final int ATTACK_COOLDOWN_TICKS = 32;

    private static final int DEATH_LINGER_TICKS = 32;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState attackBiteAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    private int currentAnimationMode = -1;
    private int animationSwitchCooldown = 0;

    private int clientAttackAnimationTicks = 0;
    private boolean clientBiteAttackVariant = false;

    private boolean nextAttackBiteVariant = false;
    private boolean pickedFirstAttackVariant = false;

    private LivingEntity pendingAttackTarget;
    private int pendingAttackDamageTicks = 0;
    private int serverAttackCooldownTicks = 0;

    public LunaShadowEntity(EntityType<? extends LunaShadowEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 34.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 42.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LunaShadowDelayedAttackGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.82D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.updateAnimationStates();
        } else if (this.level() instanceof ServerLevel serverLevel) {
            this.tickDelayedAttack(serverLevel);
            this.spawnAmbientShadowParticles(serverLevel);
            this.tryShadowTeleport(serverLevel);
        }
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;

        if (this.deathTime >= DEATH_LINGER_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void updateAnimationStates() {
        if (this.deathTime > 0) {
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.attackAnimationState.stop();
            this.attackBiteAnimationState.stop();
            this.deathAnimationState.startIfStopped(this.tickCount);
            return;
        }

        this.deathAnimationState.stop();

        if (this.clientAttackAnimationTicks > 0) {
            this.clientAttackAnimationTicks--;

            this.idleAnimationState.stop();
            this.walkAnimationState.stop();

            if (this.clientBiteAttackVariant) {
                this.attackBiteAnimationState.startIfStopped(this.tickCount);
                this.attackAnimationState.stop();
            } else {
                this.attackAnimationState.startIfStopped(this.tickCount);
                this.attackBiteAnimationState.stop();
            }

            return;
        }

        this.attackAnimationState.stop();
        this.attackBiteAnimationState.stop();

        int wantedMode = this.getWantedAnimationMode();

        if (this.animationSwitchCooldown > 0) {
            this.animationSwitchCooldown--;
        }

        if (wantedMode != this.currentAnimationMode && this.animationSwitchCooldown <= 0) {
            this.currentAnimationMode = wantedMode;
            this.animationSwitchCooldown = 6;
        }

        if (this.currentAnimationMode == 1) {
            this.walkAnimationState.startIfStopped(this.tickCount);
            this.idleAnimationState.stop();
        } else {
            this.idleAnimationState.startIfStopped(this.tickCount);
            this.walkAnimationState.stop();
        }
    }

    private int getWantedAnimationMode() {
        double horizontalSpeedSqr = this.getDeltaMovement().horizontalDistanceSqr();

        if (this.getNavigation().isInProgress() || horizontalSpeedSqr > 0.0009D) {
            return 1;
        }

        return 0;
    }

    private void tickDelayedAttack(ServerLevel serverLevel) {
        if (this.serverAttackCooldownTicks > 0) {
            this.serverAttackCooldownTicks--;
        }

        if (this.pendingAttackDamageTicks > 0) {
            this.pendingAttackDamageTicks--;

            if (this.pendingAttackDamageTicks == 0) {
                LivingEntity target = this.pendingAttackTarget;
                this.pendingAttackTarget = null;

                if (target != null && target.isAlive() && this.isAlive()) {
                    this.getLookControl().setLookAt(target, 30.0F, 30.0F);

                    if (this.distanceToSqr(target) <= this.getAttackReachSqr(target) && this.hasLineOfSight(target)) {
                        this.doHurtTarget(serverLevel, target);
                    }
                }
            }
        }
    }

    private boolean canStartDelayedAttack() {
        return this.serverAttackCooldownTicks <= 0
                && this.pendingAttackDamageTicks <= 0
                && this.pendingAttackTarget == null;
    }

    private void startDelayedAttack(ServerLevel serverLevel, LivingEntity target) {
        if (!this.canStartDelayedAttack()) {
            return;
        }

        if (!this.pickedFirstAttackVariant) {
            this.nextAttackBiteVariant = this.getRandom().nextBoolean();
            this.pickedFirstAttackVariant = true;
        }

        boolean bite = this.nextAttackBiteVariant;
        this.nextAttackBiteVariant = !this.nextAttackBiteVariant;

        this.pendingAttackTarget = target;
        this.pendingAttackDamageTicks = ATTACK_DAMAGE_DELAY_TICKS;
        this.serverAttackCooldownTicks = ATTACK_COOLDOWN_TICKS;

        this.getNavigation().stop();
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);

        serverLevel.broadcastEntityEvent(this, bite ? CLIENT_EVENT_ATTACK_BITE : CLIENT_EVENT_ATTACK);
    }

    private double getAttackReachSqr(LivingEntity target) {
        double width = this.getBbWidth() * 2.25D;
        return width * width + target.getBbWidth();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == CLIENT_EVENT_ATTACK) {
            this.startClientAttackAnimation(false);
            return;
        }

        if (id == CLIENT_EVENT_ATTACK_BITE) {
            this.startClientAttackAnimation(true);
            return;
        }

        super.handleEntityEvent(id);
    }

    private void startClientAttackAnimation(boolean bite) {
        this.clientBiteAttackVariant = bite;
        this.clientAttackAnimationTicks = ATTACK_ANIMATION_TICKS;

        this.idleAnimationState.stop();
        this.walkAnimationState.stop();

        if (bite) {
            this.attackBiteAnimationState.start(this.tickCount);
            this.attackAnimationState.stop();
        } else {
            this.attackAnimationState.start(this.tickCount);
            this.attackBiteAnimationState.stop();
        }
    }

    private void spawnAmbientShadowParticles(ServerLevel serverLevel) {
        if (this.tickCount % 10 != 0) {
            return;
        }

        serverLevel.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                2,
                0.35D,
                0.45D,
                0.35D,
                0.005D
        );
    }

    private void tryShadowTeleport(ServerLevel serverLevel) {
        LivingEntity target = this.getTarget();

        if (target == null || !target.isAlive()) {
            return;
        }

        if (this.tickCount % 90 != 0) {
            return;
        }

        if (this.distanceToSqr(target) <= 72.0D) {
            return;
        }

        Vec3 look = target.getLookAngle().scale(-2.5D);

        this.teleportTo(
                target.getX() + look.x,
                target.getY(),
                target.getZ() + look.z
        );

        serverLevel.sendParticles(
                ParticleTypes.PORTAL,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                28,
                0.5D,
                0.7D,
                0.5D,
                0.1D
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.HOSTILE,
                0.8F,
                0.55F
        );

        if (target instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new ParasiteCameraShakePacket(18, 0.65F));
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);

        if (hurt && target instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 6, 0));
            if (!MoonLoreUtils.isImmuneToInfection(livingEntity)) {
                livingEntity.addEffect(new MobEffectInstance(EffectsRegistry.getHolder(EffectsRegistry.INFECTED), 20 * 8, 0));
            }

            this.level().playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.SCULK_CATALYST_BLOOM,
                    SoundSource.HOSTILE,
                    0.9F,
                    0.7F
            );
        }

        return hurt;
    }

    private static final class LunaShadowDelayedAttackGoal extends Goal {
        private final LunaShadowEntity shadow;
        private final double speedModifier;
        private int pathUpdateTicks;

        private LunaShadowDelayedAttackGoal(LunaShadowEntity shadow, double speedModifier) {
            this.shadow = shadow;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.shadow.getTarget();
            return target != null && target.isAlive() && this.shadow.canAttack(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.shadow.getTarget();
            return target != null && target.isAlive() && this.shadow.canAttack(target);
        }

        @Override
        public void stop() {
            this.pathUpdateTicks = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.shadow.getTarget();

            if (target == null || !target.isAlive()) {
                return;
            }

            this.shadow.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double distanceSqr = this.shadow.distanceToSqr(target);
            double attackReachSqr = this.shadow.getAttackReachSqr(target);

            if (distanceSqr > attackReachSqr) {
                if (--this.pathUpdateTicks <= 0) {
                    this.pathUpdateTicks = 6;
                    this.shadow.getNavigation().moveTo(target, this.speedModifier);
                }

                return;
            }

            this.shadow.getNavigation().stop();

            if (this.shadow.level() instanceof ServerLevel serverLevel && this.shadow.canStartDelayedAttack()) {
                this.shadow.startDelayedAttack(serverLevel, target);
            }
        }
    }
}
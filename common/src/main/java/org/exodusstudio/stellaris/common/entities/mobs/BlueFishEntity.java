package org.exodusstudio.stellaris.common.entities.mobs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.fish.AbstractSchoolingFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

public class BlueFishEntity extends AbstractSchoolingFish {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState moveAnimationState = new AnimationState();
    public final AnimationState moveFastAnimationState = new AnimationState();

    private int currentAnimationMode = -1;
    private int animationSwitchCooldown = 0;

    public BlueFishEntity(EntityType<? extends BlueFishEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSchoolingFish.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 12.0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.updateAnimationStates();
        }
    }

    public static boolean checkBlueFishSpawnRules(
            EntityType<BlueFishEntity> entityType,
            ServerLevelAccessor level,
            EntitySpawnReason spawnReason,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getFluidState(pos).is(FluidTags.WATER)
                || level.getFluidState(pos).is(TagsRegistry.FluidTags.BLUE_LIQUID);
    }

    private void updateAnimationStates() {
        int wantedMode = getWantedAnimationMode();

        if (this.animationSwitchCooldown > 0) {
            this.animationSwitchCooldown--;
        }

        if (wantedMode != this.currentAnimationMode && this.animationSwitchCooldown <= 0) {
            this.currentAnimationMode = wantedMode;
            this.animationSwitchCooldown = wantedMode == 2 ? 4 : 8;
        }

        if (this.currentAnimationMode == 0) {
            this.idleAnimationState.startIfStopped(this.tickCount);
        } else {
            this.idleAnimationState.stop();
        }

        if (this.currentAnimationMode == 1) {
            this.moveAnimationState.startIfStopped(this.tickCount);
        } else {
            this.moveAnimationState.stop();
        }

        if (this.currentAnimationMode == 2) {
            this.moveFastAnimationState.startIfStopped(this.tickCount);
        } else {
            this.moveFastAnimationState.stop();
        }
    }

    private int getWantedAnimationMode() {
        if (!this.isAlive() || this.deathTime > 0) {
            return -1;
        }

        if (!this.isInWater()) {
            return -1;
        }

        double speedSqr = this.getDeltaMovement().lengthSqr();

        if (this.isAggressive() || this.swinging || this.getAttackAnim(1.0F) > 0.0F || speedSqr > 0.0125D) {
            return 2;
        }

        if (this.getNavigation().isInProgress() || speedSqr > 0.00045D) {
            return 1;
        }

        return 0;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, (player, serverLevel) -> player.isInWater()));
    }

    @Override
    public int getMaxSchoolSize() {
        return 9;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.TROPICAL_FISH_BUCKET);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TROPICAL_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.TROPICAL_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt) {
            this.swing(InteractionHand.MAIN_HAND, true);
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.knockback(0.12, this.getX() - livingEntity.getX(), this.getZ() - livingEntity.getZ());
            }
        }
        return hurt;
    }
}
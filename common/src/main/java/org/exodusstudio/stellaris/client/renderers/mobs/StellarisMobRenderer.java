package org.exodusstudio.stellaris.client.renderers.mobs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.entities.mobs.BlueFishEntity;
import org.exodusstudio.stellaris.common.entities.mobs.EvolvedParasiteAffectedVillagerEntity;
import org.exodusstudio.stellaris.common.entities.mobs.LunaShadowEntity;
import org.exodusstudio.stellaris.common.entities.mobs.LunarParasiteEntity;
import org.exodusstudio.stellaris.common.entities.mobs.ParasiteAffectedVillagerEntity;
import org.jetbrains.annotations.NotNull;

public class StellarisMobRenderer<T extends Mob> extends EntityRenderer<T, StellarisMobRenderState> {

    private static final float BLUE_FISH_DEATH_GROUND_Y_OFFSET = 1.55F;
    private static final float LUNAR_PARASITE_DEATH_GROUND_Y_OFFSET = 1.40F;

    private static final float PARASITE_VILLAGER_DEATH_GROUND_Y_OFFSET = 1.40F;
    private static final float EVOLVED_PARASITE_VILLAGER_DEATH_GROUND_Y_OFFSET = 1.55F;
    private static final float LUNA_SHADOW_DEATH_GROUND_Y_OFFSET = 0.85F;

    private static final float PARASITE_VILLAGER_DEATH_ANIMATION_TICKS = 10.0F;
    private static final float EVOLVED_PARASITE_VILLAGER_DEATH_ANIMATION_TICKS = 14.0F;

    private final EntityModel<StellarisMobRenderState> model;
    private final RenderType renderType;
    private final float scale;
    private final float yOffset;

    public StellarisMobRenderer(
            EntityRendererProvider.Context context,
            EntityModel<StellarisMobRenderState> model,
            Identifier texture,
            float scale,
            float yOffset,
            float shadowRadius
    ) {
        super(context);
        this.model = model;
        this.renderType = RenderTypes.entityCutoutCull(texture);
        this.scale = scale;
        this.yOffset = yOffset;
        this.shadowRadius = shadowRadius;
    }

    @Override
    public void extractRenderState(T entity, StellarisMobRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);

        reusedState.ageInTicks = entity.tickCount + partialTick;
        reusedState.bodyRotation = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);

        float headRotation = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);

        reusedState.walkAnimationPos = entity.walkAnimation.position(partialTick);
        reusedState.walkAnimationSpeed = Math.min(entity.walkAnimation.speed(partialTick), 1.0F);

        double dx = entity.getDeltaMovement().x;
        double dy = entity.getDeltaMovement().y;
        double dz = entity.getDeltaMovement().z;

        float deathTicks = entity.deathTime > 0 ? entity.deathTime + partialTick : 0.0F;

        reusedState.movementSpeed = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        reusedState.inWater = entity.isInWater();
        reusedState.swimAmount = reusedState.inWater ? 1.0F : 0.0F;

        reusedState.headYaw = Mth.wrapDegrees(headRotation - reusedState.bodyRotation);
        reusedState.headPitch = entity.getViewXRot(partialTick);
        reusedState.attackProgress = entity.getAttackAnim(partialTick);

        reusedState.deathProgress = Mth.clamp(deathTicks / 20.0F, 0.0F, 1.0F);
        reusedState.customDeathProgress = Mth.clamp(deathTicks / PARASITE_VILLAGER_DEATH_ANIMATION_TICKS, 0.0F, 1.0F);
        reusedState.dying = deathTicks > 0.0F;
        reusedState.hasCustomDeathAnimation = false;
        reusedState.customDeathGroundYOffset = 0.0F;

        reusedState.hurt = entity.hurtTime > 0 || entity.deathTime > 0;

        reusedState.aggressive = entity.isAggressive();
        reusedState.attached = entity instanceof LunarParasiteEntity parasite && parasite.isAttached();
        reusedState.evolved = entity instanceof EvolvedParasiteAffectedVillagerEntity;
        reusedState.modelYOffsetCorrection = entity instanceof LunarParasiteEntity ? -0.20F : 0.0F;

        if (entity instanceof BlueFishEntity blueFish) {
            reusedState.blueFishIdleAnimationState.copyFrom(blueFish.idleAnimationState);
            reusedState.blueFishMoveAnimationState.copyFrom(blueFish.moveAnimationState);
            reusedState.blueFishMoveFastAnimationState.copyFrom(blueFish.moveFastAnimationState);

            if (reusedState.dying) {
                reusedState.customDeathGroundYOffset = BLUE_FISH_DEATH_GROUND_Y_OFFSET;
            }
        } else {
            reusedState.blueFishIdleAnimationState.stop();
            reusedState.blueFishMoveAnimationState.stop();
            reusedState.blueFishMoveFastAnimationState.stop();
        }

        if (entity instanceof LunarParasiteEntity lunarParasite) {
            reusedState.lunarParasiteIdleAnimationState.copyFrom(lunarParasite.idleAnimationState);
            reusedState.lunarParasiteMoveAnimationState.copyFrom(lunarParasite.moveAnimationState);
            reusedState.lunarParasiteInfectAnimationState.copyFrom(lunarParasite.infectAnimationState);
            reusedState.lunarParasiteAttachedAnimationState.copyFrom(lunarParasite.attachedAnimationState);

            if (reusedState.dying) {
                reusedState.customDeathGroundYOffset = LUNAR_PARASITE_DEATH_GROUND_Y_OFFSET;
            }
        } else {
            reusedState.lunarParasiteIdleAnimationState.stop();
            reusedState.lunarParasiteMoveAnimationState.stop();
            reusedState.lunarParasiteInfectAnimationState.stop();
            reusedState.lunarParasiteAttachedAnimationState.stop();
        }

        if (entity instanceof EvolvedParasiteAffectedVillagerEntity evolvedVillager) {
            reusedState.evolvedParasiteAffectedVillagerIdleAnimationState.copyFrom(evolvedVillager.evolvedIdleAnimationState);
            reusedState.evolvedParasiteAffectedVillagerWalkAnimationState.copyFrom(evolvedVillager.evolvedWalkAnimationState);
            reusedState.evolvedParasiteAffectedVillagerAttackAnimationState.copyFrom(evolvedVillager.evolvedAttackAnimationState);
            reusedState.evolvedParasiteAffectedVillagerAttackTentacleAnimationState.copyFrom(evolvedVillager.evolvedAttackTentacleAnimationState);
            reusedState.evolvedParasiteAffectedVillagerAttackSpitAnimationState.copyFrom(evolvedVillager.evolvedAttackSpitAnimationState);
            reusedState.evolvedParasiteAffectedVillagerDeathAnimationState.copyFrom(evolvedVillager.evolvedDeathAnimationState);

            if (reusedState.dying) {
                reusedState.hasCustomDeathAnimation = true;
                reusedState.customDeathProgress = Mth.clamp(deathTicks / EVOLVED_PARASITE_VILLAGER_DEATH_ANIMATION_TICKS, 0.0F, 1.0F);
                reusedState.customDeathGroundYOffset = EVOLVED_PARASITE_VILLAGER_DEATH_GROUND_Y_OFFSET;
            }
        } else {
            reusedState.evolvedParasiteAffectedVillagerIdleAnimationState.stop();
            reusedState.evolvedParasiteAffectedVillagerWalkAnimationState.stop();
            reusedState.evolvedParasiteAffectedVillagerAttackAnimationState.stop();
            reusedState.evolvedParasiteAffectedVillagerAttackTentacleAnimationState.stop();
            reusedState.evolvedParasiteAffectedVillagerAttackSpitAnimationState.stop();
            reusedState.evolvedParasiteAffectedVillagerDeathAnimationState.stop();
        }

        if (entity instanceof ParasiteAffectedVillagerEntity infectedVillager && !(entity instanceof EvolvedParasiteAffectedVillagerEntity)) {
            reusedState.parasiteAffectedVillagerIdleAnimationState.copyFrom(infectedVillager.idleAnimationState);
            reusedState.parasiteAffectedVillagerWalkAnimationState.copyFrom(infectedVillager.walkAnimationState);
            reusedState.parasiteAffectedVillagerAttackAnimationState.copyFrom(infectedVillager.attackAnimationState);
            reusedState.parasiteAffectedVillagerDeathAnimationState.copyFrom(infectedVillager.deathAnimationState);

            if (reusedState.dying) {
                reusedState.hasCustomDeathAnimation = true;
                reusedState.customDeathGroundYOffset = PARASITE_VILLAGER_DEATH_GROUND_Y_OFFSET;
            }
        } else {
            reusedState.parasiteAffectedVillagerIdleAnimationState.stop();
            reusedState.parasiteAffectedVillagerWalkAnimationState.stop();
            reusedState.parasiteAffectedVillagerAttackAnimationState.stop();
            reusedState.parasiteAffectedVillagerDeathAnimationState.stop();
        }

        if (entity instanceof LunaShadowEntity lunaShadow) {
            reusedState.lunaShadowIdleAnimationState.copyFrom(lunaShadow.idleAnimationState);
            reusedState.lunaShadowWalkAnimationState.copyFrom(lunaShadow.walkAnimationState);
            reusedState.lunaShadowAttackAnimationState.copyFrom(lunaShadow.attackAnimationState);
            reusedState.lunaShadowAttackBiteAnimationState.copyFrom(lunaShadow.attackBiteAnimationState);
            reusedState.lunaShadowDeathAnimationState.copyFrom(lunaShadow.deathAnimationState);

            if (reusedState.dying) {
                reusedState.hasCustomDeathAnimation = true;
                reusedState.customDeathGroundYOffset = 0.0F;
            }
        } else {
            reusedState.lunaShadowIdleAnimationState.stop();
            reusedState.lunaShadowWalkAnimationState.stop();
            reusedState.lunaShadowAttackAnimationState.stop();
            reusedState.lunaShadowAttackBiteAnimationState.stop();
            reusedState.lunaShadowDeathAnimationState.stop();
        }
    }

    @Override
    public @NotNull StellarisMobRenderState createRenderState() {
        return new StellarisMobRenderState();
    }

    @Override
    public void submit(
            StellarisMobRenderState renderState,
            PoseStack poseStack,
            SubmitNodeCollector nodeCollector,
            CameraRenderState cameraRenderState
    ) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.bodyRotation));
        poseStack.translate(0.0D, this.yOffset + renderState.modelYOffsetCorrection, 0.0D);

        if (renderState.dying && renderState.hasCustomDeathAnimation && renderState.customDeathGroundYOffset != 0.0F) {
            float death = Mth.clamp(renderState.customDeathProgress, 0.0F, 1.0F);
            float easedDeath = death * death * (3.0F - 2.0F * death);

            poseStack.translate(0.0D, -renderState.customDeathGroundYOffset * easedDeath, 0.0D);
        }

        if (renderState.dying && !renderState.hasCustomDeathAnimation) {
            float death = renderState.deathProgress;
            float easedDeath = Mth.sqrt(death);
            float easedGround = death * death * (3.0F - 2.0F * death);

            float deathRoll = easedDeath * 90.0F;
            float deathSink = death * 0.18F;
            float deathGroundOffset = renderState.customDeathGroundYOffset * easedGround;
            float deathSquash = 1.0F - death * 0.08F;

            poseStack.translate(0.0D, deathSink - deathGroundOffset, 0.0D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(deathRoll));
            poseStack.scale(1.0F, deathSquash, 1.0F);
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(this.scale, this.scale, this.scale);

        this.model.setupAnim(renderState);

        int overlayCoords = renderState.hurt
                ? OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true))
                : OverlayTexture.NO_OVERLAY;

        nodeCollector.submitModelPart(
                this.model.root(),
                poseStack,
                this.renderType,
                renderState.lightCoords,
                overlayCoords,
                null
        );

        poseStack.popPose();
    }

    @Override
    protected AABB getBoundingBoxForCulling(T entity) {
        return entity.getBoundingBox().inflate(0.75D);
    }
}
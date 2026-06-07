package org.exodusstudio.stellaris.client.renderers.mobs;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class StellarisMobRenderState extends EntityRenderState {
    public final AnimationState blueFishIdleAnimationState = new AnimationState();
    public final AnimationState blueFishMoveAnimationState = new AnimationState();
    public final AnimationState blueFishMoveFastAnimationState = new AnimationState();

    public final AnimationState lunarParasiteIdleAnimationState = new AnimationState();
    public final AnimationState lunarParasiteMoveAnimationState = new AnimationState();
    public final AnimationState lunarParasiteInfectAnimationState = new AnimationState();
    public final AnimationState lunarParasiteAttachedAnimationState = new AnimationState();

    public final AnimationState parasiteAffectedVillagerIdleAnimationState = new AnimationState();
    public final AnimationState parasiteAffectedVillagerWalkAnimationState = new AnimationState();
    public final AnimationState parasiteAffectedVillagerAttackAnimationState = new AnimationState();
    public final AnimationState parasiteAffectedVillagerDeathAnimationState = new AnimationState();

    public final AnimationState lunaShadowIdleAnimationState = new AnimationState();
    public final AnimationState lunaShadowWalkAnimationState = new AnimationState();
    public final AnimationState lunaShadowAttackAnimationState = new AnimationState();
    public final AnimationState lunaShadowAttackBiteAnimationState = new AnimationState();
    public final AnimationState lunaShadowDeathAnimationState = new AnimationState();

    public final AnimationState evolvedParasiteAffectedVillagerIdleAnimationState = new AnimationState();
    public final AnimationState evolvedParasiteAffectedVillagerWalkAnimationState = new AnimationState();
    public final AnimationState evolvedParasiteAffectedVillagerAttackAnimationState = new AnimationState();
    public final AnimationState evolvedParasiteAffectedVillagerAttackTentacleAnimationState = new AnimationState();
    public final AnimationState evolvedParasiteAffectedVillagerAttackSpitAnimationState = new AnimationState();
    public final AnimationState evolvedParasiteAffectedVillagerDeathAnimationState = new AnimationState();

    public float bodyRotation;
    public float walkAnimationPos;
    public float walkAnimationSpeed;
    public float movementSpeed;
    public float swimAmount;
    public float headYaw;
    public float headPitch;
    public float attackProgress;

    public boolean aggressive;
    public boolean attached;
    public boolean inWater;
    public boolean evolved;
    public boolean hurt;

    public float deathProgress;
    public float customDeathProgress;
    public boolean dying;
    public boolean hasCustomDeathAnimation;

    public float modelYOffsetCorrection;
    public float customDeathGroundYOffset;
}
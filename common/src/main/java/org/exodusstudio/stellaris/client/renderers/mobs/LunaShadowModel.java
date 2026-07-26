package org.exodusstudio.stellaris.client.renderers.mobs;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class LunaShadowModel extends EntityModel<StellarisMobRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("mob_luna_shadow"), "main");

    private final ModelPart shadowRoot;
    private final ModelPart upperBody;
    private final ModelPart head;
    private final ModelPart headUpper;
    private final ModelPart mouth;
    private final ModelPart lowerBody;
    private final ModelPart heart;
    private final ModelPart tentacle;
    private final ModelPart tentacleA;
    private final ModelPart tentacleB;
    private final ModelPart tentacle2;
    private final ModelPart tentacleA2;
    private final ModelPart tentacleB2;
    private final ModelPart tentacle3;
    private final ModelPart tentacleA4;
    private final ModelPart tentacleB4;

    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation attackAnimation;
    private final KeyframeAnimation attackBiteAnimation;
    private final KeyframeAnimation deathAnimation;

    public LunaShadowModel(ModelPart root) {
        super(root);

        this.shadowRoot = root.getChild("root");
        this.upperBody = this.shadowRoot.getChild("upper_body");
        this.head = this.upperBody.getChild("head");
        this.headUpper = this.head.getChild("head_upper");
        this.mouth = this.head.getChild("mouth");
        this.lowerBody = this.shadowRoot.getChild("lower_body");
        this.heart = this.lowerBody.getChild("heart");
        this.tentacle = this.lowerBody.getChild("tentacle");
        this.tentacleA = this.tentacle.getChild("tentacle_segment_a");
        this.tentacleB = this.tentacleA.getChild("tentacle_segment_b");
        this.tentacle2 = this.lowerBody.getChild("tentacle2");
        this.tentacleA2 = this.tentacle2.getChild("tentacle_segment_a2");
        this.tentacleB2 = this.tentacleA2.getChild("tentacle_segment_b2");
        this.tentacle3 = this.lowerBody.getChild("tentacle3");
        this.tentacleA4 = this.tentacle3.getChild("tentacle_segment_a4");
        this.tentacleB4 = this.tentacleA4.getChild("tentacle_segment_b4");

        this.idleAnimation = LunaShadowAnimations.IDLE.bake(root);
        this.walkAnimation = LunaShadowAnimations.WALK.bake(root);
        this.attackAnimation = LunaShadowAnimations.ATTACK.bake(root);
        this.attackBiteAnimation = LunaShadowAnimations.ATTACK_BITE.bake(root);
        this.deathAnimation = LunaShadowAnimations.DEATH.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild(
                "root",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        PartDefinition upperBody = root.addOrReplaceChild(
                "upper_body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-6.5F, -5.9167F, -4.5127F, 14.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 14).addBox(-4.5F, -8.9167F, -5.5127F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 27).addBox(0.5F, -15.9167F, -0.5127F, 0.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.5F, -31.0833F, 0.5127F)
        );

        upperBody.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create()
                        .texOffs(34, 44).addBox(0.0F, -3.0F, -4.5F, 0.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.5F, -11.9167F, 3.7373F, 0.0F, 0.3927F, 0.0F)
        );

        upperBody.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create()
                        .texOffs(18, 44).addBox(0.0F, -3.0F, -4.5F, 0.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.5F, -11.9167F, 3.7373F, 0.0F, -0.3927F, 0.0F)
        );

        PartDefinition head = upperBody.addOrReplaceChild(
                "head",
                CubeListBuilder.create(),
                PartPose.offset(-2.5F, -8.9167F, -3.5127F)
        );

        PartDefinition headUpper = head.addOrReplaceChild(
                "head_upper",
                CubeListBuilder.create()
                        .texOffs(0, 27).addBox(-4.0F, -7.0257F, -3.0333F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(3.0F, 0.0257F, -1.9667F)
        );

        headUpper.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create()
                        .texOffs(66, 46).addBox(0.0F, -1.5F, -3.0F, 0.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(5.1471F, -3.5257F, 2.7387F, 0.0F, 0.3927F, 0.0F)
        );

        headUpper.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create()
                        .texOffs(66, 26).addBox(0.0F, -1.5F, -3.0F, 0.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-5.1471F, -3.5257F, 2.7387F, 0.0F, -0.3927F, 0.0F)
        );

        headUpper.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create()
                        .texOffs(50, 26).addBox(0.0F, -1.85F, -4.0F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.8F, -9.0257F, 2.9667F, 0.0F, 0.0F, -0.3927F)
        );

        headUpper.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create()
                        .texOffs(40, 14).addBox(0.0F, -1.85F, -4.0F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.8F, -9.0257F, 2.9667F, 0.0F, 0.0F, 0.3927F)
        );

        head.addOrReplaceChild(
                "mouth",
                CubeListBuilder.create()
                        .texOffs(50, 38).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.01F)),
                PartPose.offset(3.0F, 0.0F, -2.0F)
        );

        PartDefinition lowerBody = root.addOrReplaceChild(
                "lower_body",
                CubeListBuilder.create()
                        .texOffs(0, 68).addBox(-4.0148F, -0.6867F, -4.6846F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(50, 69).addBox(-0.0159F, -0.6867F, -4.6846F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 89).mirror().addBox(-2.5154F, 1.1219F, 3.0654F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(70, 42).addBox(-2.5154F, 0.1219F, 2.0654F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0154F, -31.1219F, -0.3154F)
        );

        lowerBody.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create()
                        .texOffs(72, 0).addBox(0.0F, -3.0F, -1.5F, 0.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.3488F, 2.5479F, 4.8154F, 0.0F, 0.0F, 0.3927F)
        );

        lowerBody.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create()
                        .texOffs(40, 71).addBox(0.0F, -3.0F, -1.5F, 0.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.3796F, 2.5479F, 4.8154F, 0.0F, 0.0F, -0.3927F)
        );

        lowerBody.addOrReplaceChild(
                "rib_r1",
                CubeListBuilder.create()
                        .texOffs(72, 60).addBox(-0.5F, 0.0F, -4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(44, 0).addBox(-1.5F, -4.0F, -2.975F, 3.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.7654F, 3.1219F, 0.2904F, 0.0F, 0.0F, -0.3927F)
        );

        lowerBody.addOrReplaceChild(
                "rib_r2",
                CubeListBuilder.create()
                        .texOffs(72, 55).addBox(-2.5F, 0.0F, -4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 42).addBox(-1.5F, -4.0F, -2.975F, 3.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.7346F, 3.1219F, 0.2904F, 0.0F, 0.0F, 0.3927F)
        );

        PartDefinition heart = lowerBody.addOrReplaceChild(
                "heart",
                CubeListBuilder.create(),
                PartPose.offset(0.0513F, 1.2719F, 2.0654F)
        );

        heart.addOrReplaceChild(
                "cube_r9",
                CubeListBuilder.create()
                        .texOffs(66, 55).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.5833F, -0.5F, 0.0F, 0.0F, 0.0F, 0.3927F)
        );

        heart.addOrReplaceChild(
                "cube_r10",
                CubeListBuilder.create()
                        .texOffs(72, 19).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.1667F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
        );

        heart.addOrReplaceChild(
                "cube_r11",
                CubeListBuilder.create()
                        .texOffs(66, 35).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.4167F, -0.5F, 0.0F, 0.0F, 0.0F, -0.3927F)
        );

        PartDefinition tentacle = lowerBody.addOrReplaceChild(
                "tentacle",
                CubeListBuilder.create()
                        .texOffs(50, 46).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(46, 71).addBox(0.0F, 0.0F, 2.0F, 0.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.0154F, 6.1219F, -1.9346F)
        );

        PartDefinition tentacleA = tentacle.addOrReplaceChild(
                "tentacle_segment_a",
                CubeListBuilder.create()
                        .texOffs(72, 9).addBox(0.0F, 0.0F, 1.5F, 0.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(50, 58).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );

        tentacleA.addOrReplaceChild(
                "tentacle_segment_b",
                CubeListBuilder.create()
                        .texOffs(72, 65).addBox(0.0F, -0.0833F, 1.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 70).addBox(-1.0F, -0.0833F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 59).addBox(0.0F, 2.9167F, -2.5F, 0.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 70).addBox(-2.5F, 2.9167F, 0.0F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 8.0833F, 0.0F)
        );

        PartDefinition tentacle2 = lowerBody.addOrReplaceChild(
                "tentacle2",
                CubeListBuilder.create()
                        .texOffs(0, 56).addBox(-1.9F, -0.1F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 74).addBox(2.1F, -0.1F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.5154F, 6.2219F, 1.4654F)
        );

        PartDefinition tentacleA2 = tentacle2.addOrReplaceChild(
                "tentacle_segment_a2",
                CubeListBuilder.create()
                        .texOffs(4, 74).addBox(1.6F, 0.0F, 1.4F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 59).addBox(-1.4F, 0.0F, -0.1F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 7.9F, -1.4F)
        );

        tentacleA2.addOrReplaceChild(
                "tentacle_segment_b2",
                CubeListBuilder.create()
                        .texOffs(16, 56).addBox(1.0F, -0.0833F, 1.4F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(62, 70).addBox(-1.0F, -0.0833F, 0.4F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 70).addBox(-2.5F, 2.9167F, 1.4F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(62, 0).addBox(0.0F, 2.9167F, -1.1F, 0.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.1F, 8.0833F, 0.0F)
        );

        PartDefinition tentacle3 = lowerBody.addOrReplaceChild(
                "tentacle3",
                CubeListBuilder.create()
                        .texOffs(56, 14).addBox(-2.07F, -0.1F, -1.72F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 74).addBox(-4.07F, -0.1F, 0.28F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.4546F, 6.2219F, 1.1854F)
        );

        PartDefinition tentacleA4 = tentacle3.addOrReplaceChild(
                "tentacle_segment_a4",
                CubeListBuilder.create()
                        .texOffs(50, 75).addBox(-3.5F, -0.0417F, 0.7F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 59).addBox(-1.5F, -0.0417F, -0.8F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.07F, 7.9417F, -0.42F)
        );

        tentacleA4.addOrReplaceChild(
                "tentacle_segment_b4",
                CubeListBuilder.create()
                        .texOffs(70, 55).addBox(-2.0F, 0.0667F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(70, 70).addBox(-1.0F, 0.0667F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(70, 35).addBox(-2.5F, 3.0667F, 0.0F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(62, 58).addBox(0.0F, 3.0667F, -2.5F, 0.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 7.8917F, 0.7F)
        );

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(StellarisMobRenderState state) {
        this.resetAllPoses();

        if (state.dying) {
            this.deathAnimation.apply(state.lunaShadowDeathAnimationState, state.ageInTicks, 1.0F);
            return;
        }

        this.idleAnimation.apply(state.lunaShadowIdleAnimationState, state.ageInTicks, 1.0F);
        this.walkAnimation.apply(state.lunaShadowWalkAnimationState, state.ageInTicks, 1.0F);
        this.attackAnimation.apply(state.lunaShadowAttackAnimationState, state.ageInTicks, 1.0F);
        this.attackBiteAnimation.apply(state.lunaShadowAttackBiteAnimationState, state.ageInTicks, 1.0F);

        if (!state.aggressive) {
            this.head.yRot += Mth.clamp(state.headYaw, -35.0F, 35.0F) * Mth.DEG_TO_RAD * 0.35F;
            this.head.xRot += Mth.clamp(state.headPitch, -25.0F, 25.0F) * Mth.DEG_TO_RAD * 0.2F;
        }
    }

    private void resetAllPoses() {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }
}
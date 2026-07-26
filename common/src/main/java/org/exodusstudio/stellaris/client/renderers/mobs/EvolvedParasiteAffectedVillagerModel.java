package org.exodusstudio.stellaris.client.renderers.mobs;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class EvolvedParasiteAffectedVillagerModel extends EntityModel<StellarisMobRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(IdentifierUtils.id("mob_parasite_affected_villager_evolved"), "main");

    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation attackAnimation;
    private final KeyframeAnimation attackTentacleAnimation;
    private final KeyframeAnimation attackSpitAnimation;
    private final KeyframeAnimation deathAnimation;

    public EvolvedParasiteAffectedVillagerModel(ModelPart root) {
        super(root);

        this.idleAnimation = EvolvedParasiteAffectedVillagerAnimations.IDLE.bake(root);
        this.walkAnimation = EvolvedParasiteAffectedVillagerAnimations.WALK.bake(root);
        this.attackAnimation = EvolvedParasiteAffectedVillagerAnimations.ATTACK.bake(root);
        this.attackTentacleAnimation = EvolvedParasiteAffectedVillagerAnimations.ATTACK_TENTACLE.bake(root);
        this.attackSpitAnimation = EvolvedParasiteAffectedVillagerAnimations.ATTACK_SPIT.bake(root);
        this.deathAnimation = EvolvedParasiteAffectedVillagerAnimations.DEATH.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild(
                "root",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(38, 41).mirror().addBox(4.05F, -7.0F, 0.0F, 6.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(48, 50).mirror().addBox(-12.0F, -11.0F, 2.0F, 8.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(16, 18).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -12.0F, 0.0F)
        );

        body.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create()
                        .texOffs(65, 0).mirror().addBox(-1.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(5.0F, -10.05F, -0.05F)
        );

        body.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create()
                        .texOffs(65, 0).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-5.0F, -10.05F, -0.05F)
        );

        PartDefinition head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.51F))
                        .texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(52, 40).addBox(-10.0F, -14.0F, -1.0F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 50).addBox(4.0F, -11.0F, 1.0F, 8.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 33).addBox(-1.0F, -16.0F, -0.1F, 0.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -12.0F, 0.0F)
        );

        head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(0, 34).mirror().addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(3.5F, -3.0F, -4.0F, 0.0F, -0.3927F, 0.0F)
        );

        head.addOrReplaceChild(
                "head_r2",
                CubeListBuilder.create().texOffs(30, 4).mirror().addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(3.5F, -6.0F, -3.7F, 0.0F, -0.7854F, 0.0F)
        );

        head.addOrReplaceChild(
                "head_r3",
                CubeListBuilder.create().texOffs(30, 4).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.5F, -6.0F, -3.7F, 0.0F, 0.7854F, 0.0F)
        );

        head.addOrReplaceChild(
                "head_r4",
                CubeListBuilder.create().texOffs(0, 34).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.5F, -3.0F, -4.0F, 0.0F, 0.3927F, 0.0F)
        );

        head.addOrReplaceChild(
                "head_r5",
                CubeListBuilder.create().texOffs(28, 40).addBox(0.0F, -4.0F, -2.0F, 0.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.0F, 8.0F, 4.9F, 0.0F, 0.3927F, 0.0F)
        );

        head.addOrReplaceChild(
                "head_r6",
                CubeListBuilder.create().texOffs(28, 40).addBox(0.0F, -4.0F, -2.0F, 0.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.0F, -5.0F, 5.9F, 0.0F, 0.3927F, 0.0F)
        );

        head.addOrReplaceChild(
                "head_r7",
                CubeListBuilder.create().texOffs(38, 35).addBox(-2.0F, -4.5F, -2.3F, 0.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, -4.5F, 7.0F, 0.0F, -0.7854F, 0.0F)
        );

        head.addOrReplaceChild(
                "head_r8",
                CubeListBuilder.create().texOffs(28, 48).addBox(0.0F, -5.0F, -2.5F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, -13.0F, -4.5F, 0.0F, -0.3927F, 0.0F)
        );

        PartDefinition mouth = head.addOrReplaceChild(
                "mouth",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -1.2119F, -3.8087F)
        );

        PartDefinition m_l = mouth.addOrReplaceChild(
                "m_l",
                CubeListBuilder.create(),
                PartPose.offset(-2.0F, 0.0F, -0.25F)
        );

        m_l.addOrReplaceChild(
                "nose_r1",
                CubeListBuilder.create().texOffs(40, 55).addBox(-3.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, 0.5F, -0.25F, -0.7854F, 0.0F, 0.0F)
        );

        PartDefinition m_r = mouth.addOrReplaceChild(
                "m_r",
                CubeListBuilder.create(),
                PartPose.offset(2.0F, 0.0F, -0.25F)
        );

        m_r.addOrReplaceChild(
                "nose_r2",
                CubeListBuilder.create().texOffs(40, 50).mirror().addBox(1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-2.0F, 0.5F, -0.25F, -0.7854F, 0.0F, 0.0F)
        );

        PartDefinition nose = head.addOrReplaceChild(
                "nose",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -2.0F, 0.0F)
        );

        nose.addOrReplaceChild(
                "nose_r3",
                CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, -0.3927F, 0.0F, 0.0F)
        );

        PartDefinition tentacle_start = body.addOrReplaceChild(
                "tentacle_start",
                CubeListBuilder.create()
                        .texOffs(116, 118).addBox(-1.5034F, -4.0444F, -1.5034F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.2F))
                        .texOffs(58, 119).addBox(-1.5034F, -4.0444F, -1.5034F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(70, 119).addBox(-0.0034F, -4.0444F, 1.4966F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.9916F, -5.9056F, 5.0034F)
        );

        PartDefinition tentacle_a = tentacle_start.addOrReplaceChild(
                "tentacle_a",
                CubeListBuilder.create()
                        .texOffs(76, 112).addBox(-1.5136F, -4.1775F, -1.5136F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.21F))
                        .texOffs(102, 102).addBox(-1.5136F, -4.1775F, -1.5136F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0102F, -4.2669F, 0.0102F)
        );

        PartDefinition tentacle_b = tentacle_a.addOrReplaceChild(
                "tentacle_b",
                CubeListBuilder.create()
                        .texOffs(116, 104).addBox(-0.0408F, -4.2525F, 1.4592F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(114, 102).addBox(-1.5408F, -4.2525F, -1.5408F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(76, 112).addBox(-1.5408F, -4.2525F, -1.5408F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.21F)),
                PartPose.offset(0.0272F, -4.345F, 0.0272F)
        );

        PartDefinition tentacle_c = tentacle_b.addOrReplaceChild(
                "tentacle_c",
                CubeListBuilder.create()
                        .texOffs(76, 112).addBox(-1.6633F, -4.23F, -1.4633F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.21F))
                        .texOffs(90, 102).addBox(-1.6633F, -4.23F, -1.4633F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.1225F, -4.4425F, -0.0775F)
        );

        PartDefinition tentacle_d = tentacle_c.addOrReplaceChild(
                "tentacle_d",
                CubeListBuilder.create()
                        .texOffs(64, 112).addBox(-1.6237F, -4.25F, -1.475F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.2F))
                        .texOffs(112, 66).addBox(-1.6237F, -4.25F, -1.475F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(112, 73).addBox(-0.1237F, -4.25F, 1.525F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.0496F, -4.39F, 0.0017F)
        );

        tentacle_d.addOrReplaceChild(
                "tentacle_final",
                CubeListBuilder.create()
                        .texOffs(0, 113).addBox(-1.6717F, -4.1667F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 121).addBox(-1.6717F, -4.1667F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.2F))
                        .texOffs(13, 109).mirror().addBox(-0.1567F, -9.1667F, -6.5F, 0.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(0.0479F, -4.0833F, 0.025F)
        );

        PartDefinition tentacle_start2 = body.addOrReplaceChild(
                "tentacle_start2",
                CubeListBuilder.create()
                        .texOffs(116, 118).mirror().addBox(-1.4966F, -4.0444F, -1.5034F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.2F)).mirror(false)
                        .texOffs(58, 119).mirror().addBox(-1.4966F, -4.0444F, -1.5034F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(70, 119).mirror().addBox(0.0034F, -4.0444F, 1.4966F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(1.9916F, -5.9056F, 5.0034F)
        );

        PartDefinition tentacle_a3 = tentacle_start2.addOrReplaceChild(
                "tentacle_a3",
                CubeListBuilder.create()
                        .texOffs(76, 112).mirror().addBox(-1.4864F, -4.1775F, -1.5136F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.21F)).mirror(false)
                        .texOffs(114, 102).mirror().addBox(-1.4864F, -4.1775F, -1.5136F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-0.0102F, -4.2669F, 0.0102F)
        );

        PartDefinition tentacle_b3 = tentacle_a3.addOrReplaceChild(
                "tentacle_b3",
                CubeListBuilder.create()
                        .texOffs(116, 104).mirror().addBox(0.0408F, -4.2525F, 1.4592F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(114, 102).mirror().addBox(-1.4592F, -4.2525F, -1.5408F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(76, 112).mirror().addBox(-1.4592F, -4.2525F, -1.5408F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.21F)).mirror(false),
                PartPose.offset(-0.0272F, -4.345F, 0.0272F)
        );

        PartDefinition tentacle_c3 = tentacle_b3.addOrReplaceChild(
                "tentacle_c3",
                CubeListBuilder.create()
                        .texOffs(76, 112).mirror().addBox(-1.3367F, -4.23F, -1.4633F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.21F)).mirror(false)
                        .texOffs(114, 102).mirror().addBox(-1.3367F, -4.23F, -1.4633F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-0.1225F, -4.4425F, -0.0775F)
        );

        PartDefinition tentacle_d3 = tentacle_c3.addOrReplaceChild(
                "tentacle_d3",
                CubeListBuilder.create()
                        .texOffs(64, 112).mirror().addBox(-1.3763F, -4.25F, -1.475F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.2F)).mirror(false)
                        .texOffs(112, 66).mirror().addBox(-1.3763F, -4.25F, -1.475F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(112, 73).mirror().addBox(0.1237F, -4.25F, 1.525F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(0.0496F, -4.39F, 0.0017F)
        );

        tentacle_d3.addOrReplaceChild(
                "tentacle_final3",
                CubeListBuilder.create()
                        .texOffs(0, 113).mirror().addBox(-1.3283F, -4.1667F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(0, 121).mirror().addBox(-1.3283F, -4.1667F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.2F)).mirror(false)
                        .texOffs(13, 109).addBox(-0.3433F, -9.1667F, -6.5F, 0.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.0479F, -4.0833F, 0.025F)
        );

        body.addOrReplaceChild(
                "bodywear",
                CubeListBuilder.create()
                        .texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, -12.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 18).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, -12.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create()
                        .texOffs(0, 18).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, -12.0F, 0.0F)
        );

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(StellarisMobRenderState state) {
        this.resetAllPoses();

        if (state.dying) {
            this.deathAnimation.apply(state.evolvedParasiteAffectedVillagerDeathAnimationState, state.ageInTicks, 1.0F);
            return;
        }

        this.idleAnimation.apply(state.evolvedParasiteAffectedVillagerIdleAnimationState, state.ageInTicks, 1.0F);
        this.walkAnimation.apply(state.evolvedParasiteAffectedVillagerWalkAnimationState, state.ageInTicks, 1.0F);
        this.attackAnimation.apply(state.evolvedParasiteAffectedVillagerAttackAnimationState, state.ageInTicks, 1.0F);
        this.attackTentacleAnimation.apply(state.evolvedParasiteAffectedVillagerAttackTentacleAnimationState, state.ageInTicks, 1.0F);
        this.attackSpitAnimation.apply(state.evolvedParasiteAffectedVillagerAttackSpitAnimationState, state.ageInTicks, 1.0F);
    }

    private void resetAllPoses() {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }
}
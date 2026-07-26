package org.exodusstudio.stellaris.client.renderers.mobs;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class BlueFishModel extends EntityModel<StellarisMobRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("mob_blue_fish"), "main");

    private final ModelPart bodyRoot;
    private final ModelPart backBody;
    private final ModelPart tail;
    private final ModelPart mouth;
    private final ModelPart eyeLeft;
    private final ModelPart eyeRight;
    private final ModelPart primaryFinLeft;
    private final ModelPart primaryFinRight;
    private final ModelPart subFinLargeLeft;
    private final ModelPart subFinLargeRight;
    private final ModelPart lowerBody;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation moveAnimation;
    private final KeyframeAnimation moveFastAnimation;

    public BlueFishModel(ModelPart root) {
        super(root);
        this.bodyRoot = root.getChild("root");
        this.backBody = this.bodyRoot.getChild("back_body");
        this.tail = this.backBody.getChild("tail");
        this.mouth = this.bodyRoot.getChild("mouth");
        this.eyeLeft = this.bodyRoot.getChild("eye_left");
        this.eyeRight = this.bodyRoot.getChild("eye_right");
        this.primaryFinLeft = this.bodyRoot.getChild("primary_fin_left");
        this.primaryFinRight = this.bodyRoot.getChild("primary_fin_right");
        this.lowerBody = this.bodyRoot.getChild("lower_body");
        this.subFinLargeLeft = this.lowerBody.getChild("sub_fin_large_left");
        this.subFinLargeRight = this.lowerBody.getChild("sub_fin_large_right");

        this.idleAnimation = BlueFishAnimations.IDLE.bake(root);
        this.moveAnimation = BlueFishAnimations.MOVE.bake(root);
        this.moveFastAnimation = BlueFishAnimations.MOVE_FAST.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(48, 39).addBox(-1.0F, -4.0F, -7.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -12.0F, -7.0F, 3.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, -0.9F));

        PartDefinition backBody = root.addOrReplaceChild("back_body", CubeListBuilder.create().texOffs(38, 37).addBox(-1.0F, -3.375F, 0.125F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(26, 49).addBox(0.0F, -5.375F, 0.125F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(48, 33).addBox(0.0F, 2.625F, 0.125F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.625F, 2.875F));

        backBody.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(26, 0).addBox(0.0F, -7.0F, 0.0F, 0.0F, 14.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.625F, 3.125F));

        root.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(44, 20).addBox(-1.0F, -2.0F, 1.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -1.9F, -8.5F));

        PartDefinition eyeLeft = root.addOrReplaceChild("eye_left", CubeListBuilder.create().texOffs(44, 25).addBox(-0.8545F, 1.9134F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offset(-1.3955F, -7.5134F, -3.5F));
        eyeLeft.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(42, 4).addBox(0.5F, -1.5F, 0.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6045F, 1.0134F, -2.0F, 0.0F, 0.0F, -0.3927F));
        eyeLeft.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(42, 0).addBox(-1.75F, -1.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-0.2545F, -0.9866F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition eyeRight = root.addOrReplaceChild("eye_right", CubeListBuilder.create().texOffs(44, 29).addBox(-1.1455F, 1.9134F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offset(1.3955F, -7.5134F, -3.5F));
        eyeRight.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(42, 14).addBox(-2.5F, -1.5F, 0.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6045F, 1.0134F, -2.0F, 0.0F, 0.0F, 0.3927F));
        eyeRight.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(42, 10).addBox(-1.25F, -1.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.2545F, -0.9866F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition primaryFinLeft = root.addOrReplaceChild("primary_fin_left", CubeListBuilder.create(), PartPose.offset(-1.725F, -7.0F, -0.9F));
        primaryFinLeft.addOrReplaceChild("primary_fin_left_r1", CubeListBuilder.create().texOffs(26, 22).addBox(0.0F, -2.0F, -4.5F, 0.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7F, -1.0F, 4.1F, 0.0F, -0.3927F, 0.0F));

        PartDefinition primaryFinRight = root.addOrReplaceChild("primary_fin_right", CubeListBuilder.create(), PartPose.offset(1.725F, -7.0F, -0.9F));
        primaryFinRight.addOrReplaceChild("primary_fin_right_r1", CubeListBuilder.create().texOffs(0, 36).addBox(0.0F, -2.0F, -4.5F, 0.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7F, -1.0F, 4.1F, 0.0F, 0.3927F, 0.0F));

        PartDefinition lowerBody = root.addOrReplaceChild("lower_body", CubeListBuilder.create().texOffs(18, 37).addBox(-1.0F, -0.511F, -3.5F, 2.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(18, 49).addBox(-1.0F, 1.489F, -5.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.489F, -1.5F));

        lowerBody.addOrReplaceChild("sub_fin_small_r1", CubeListBuilder.create().texOffs(32, 49).addBox(-0.55F, -1.25F, -1.25F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 2.489F, 3.0F, 0.0F, 0.0F, -0.3927F));
        lowerBody.addOrReplaceChild("sub_fin_small_r2", CubeListBuilder.create().texOffs(44, 33).addBox(0.55F, -1.25F, -1.25F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 2.489F, 3.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition subFinLargeLeft = lowerBody.addOrReplaceChild("sub_fin_large_left", CubeListBuilder.create(), PartPose.offset(-1.0539F, 1.5424F, -0.75F));
        subFinLargeLeft.addOrReplaceChild("sub_fin_large_r1", CubeListBuilder.create().texOffs(38, 46).addBox(0.55F, -1.25F, -1.25F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9461F, 0.9466F, -0.25F, 0.0F, 0.0F, 0.3927F));

        PartDefinition subFinLargeRight = lowerBody.addOrReplaceChild("sub_fin_large_right", CubeListBuilder.create(), PartPose.offset(1.0539F, 1.5424F, -0.75F));
        subFinLargeRight.addOrReplaceChild("sub_fin_large_r2", CubeListBuilder.create().texOffs(44, 46).addBox(-0.55F, -1.25F, -1.25F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9461F, 0.9466F, -0.25F, 0.0F, 0.0F, -0.3927F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    private void resetAllPoses() {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }

    @Override
    public void setupAnim(StellarisMobRenderState state) {
        resetAllPoses();

        this.idleAnimation.apply(state.blueFishIdleAnimationState, state.ageInTicks, 1.0F);
        this.moveAnimation.apply(state.blueFishMoveAnimationState, state.ageInTicks, 1.0F);
        this.moveFastAnimation.apply(state.blueFishMoveFastAnimationState, state.ageInTicks, 1.0F);

        if (!state.inWater) {
            setupFlopPose(state.ageInTicks, state.attackProgress);
        }
    }

    private void setupFlopPose(float age, float attackProgress) {
        float attack = Mth.sin(attackProgress * (float) Math.PI);
        float flop = Mth.sin(age * 0.58F);
        float flopAbs = Math.abs(flop);

        this.bodyRoot.y = 23.0F + flopAbs * 0.28F;
        this.bodyRoot.xRot = deg(72.5F) + flop * deg(7.5F);
        this.bodyRoot.yRot = flop * deg(5.0F);
        this.bodyRoot.zRot = flop * deg(27.5F);

        this.backBody.yRot = -flop * deg(16.0F);
        this.tail.yRot = flop * deg(30.0F);

        this.lowerBody.yRot = -flop * deg(3.0F);

        this.mouth.y = -1.9F - flopAbs * 0.12F - attack * 0.18F;
        this.mouth.xRot = -attack * deg(14.0F);

        this.eyeLeft.zRot = flop * deg(1.5F);
        this.eyeRight.zRot = -this.eyeLeft.zRot;

        this.primaryFinLeft.xRot = flop * deg(10.0F);
        this.primaryFinRight.xRot = -this.primaryFinLeft.xRot;

        this.primaryFinLeft.zRot = deg(14.0F);
        this.primaryFinRight.zRot = -deg(14.0F);

        this.subFinLargeLeft.zRot = deg(15.0F) + flop * deg(10.0F);
        this.subFinLargeRight.zRot = -this.subFinLargeLeft.zRot;
    }

    private static float deg(float degrees) {
        return degrees * ((float) Math.PI / 180.0F);
    }
}

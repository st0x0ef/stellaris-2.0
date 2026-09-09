package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public final class LunaBoss extends EntityModel<HeartOfLunaRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("heart_of_luna"), "main");
    private final KeyframeAnimation[] animations;

    public LunaBoss(ModelPart root) {
        super(root);
        animations = new KeyframeAnimation[] {
            HeartOfLunaAnimations.WALK.bake(root),
            HeartOfLunaAnimations.IDLE.bake(root),
            HeartOfLunaAnimations.BLOCK.bake(root),
            HeartOfLunaAnimations.ROAR.bake(root),
            HeartOfLunaAnimations.PULSE_RAY.bake(root),
            HeartOfLunaAnimations.PUNCH.bake(root),
            HeartOfLunaAnimations.DEATH.bake(root)
        };
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bone0 = root.addOrReplaceChild("Upper Full Body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, -3.0000000F, 6.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone1 = bone0.addOrReplaceChild("Upper Body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, -18.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone2 = bone1.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, -3.2500000F, -6.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone3 = bone2.addOrReplaceChild("Eyes", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 48.2500000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone4 = bone3.addOrReplaceChild("Right Eye", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone4.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(62, 106).addBox(-8.5000000F, -52.2500000F, -7.7500000F, 3.0000000F, 3.0000000F, 3.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone6 = bone3.addOrReplaceChild("Left Eye", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone6.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(62, 100).addBox(5.5000000F, -52.2500000F, -7.7500000F, 3.0000000F, 3.0000000F, 3.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone8 = bone3.addOrReplaceChild("Central Eye", CubeListBuilder.create(), PartPose.offsetAndRotation(5.5000000F, -49.2500000F, -7.7500000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone8.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(114, 46).addBox(-8.5000000F, -5.0000000F, -0.2500000F, 6.0000000F, 3.0000000F, 2.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone10 = bone2.addOrReplaceChild("Mouth Lower", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 3.0000000F, -0.2500000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone10.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(82, 33).addBox(-8.0000000F, 0.0000000F, -8.0000000F, 16.0000000F, 3.0000000F, 10.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone10.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(82, 19).addBox(-8.0000000F, 0.0000000F, -8.0000000F, 16.0000000F, 4.0000000F, 10.0000000F, new CubeDeformation(0.1000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone10.addOrReplaceChild("cube_13", CubeListBuilder.create().texOffs(36, 131).addBox(-5.0000000F, -1.0000000F, -8.7500000F, 1.0000000F, 2.0000000F, 1.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone10.addOrReplaceChild("cube_14", CubeListBuilder.create().texOffs(32, 127).addBox(-2.0000000F, -1.5000000F, -8.5000000F, 1.0000000F, 3.0000000F, 1.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone10.addOrReplaceChild("cube_15", CubeListBuilder.create().texOffs(36, 127).addBox(1.0000000F, -1.5000000F, -8.5000000F, 1.0000000F, 3.0000000F, 1.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone10.addOrReplaceChild("cube_16", CubeListBuilder.create().texOffs(116, 136).addBox(4.0000000F, -1.0000000F, -8.7500000F, 1.0000000F, 2.0000000F, 1.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone17 = bone2.addOrReplaceChild("Upper Mouth", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 48.2500000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone17.addOrReplaceChild("cube_18", CubeListBuilder.create().texOffs(82, 0).addBox(-8.0000000F, -56.2500000F, -7.2500000F, 16.0000000F, 9.0000000F, 10.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone17.addOrReplaceChild("cube_19", CubeListBuilder.create().texOffs(40, 100).addBox(6.0000000F, -47.2500000F, -6.2500000F, 2.0000000F, 2.0000000F, 9.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone17.addOrReplaceChild("cube_20", CubeListBuilder.create().texOffs(0, 140).addBox(-8.0000000F, -47.2500000F, -6.2500000F, 2.0000000F, 2.0000000F, 9.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone17.addOrReplaceChild("cube_21", CubeListBuilder.create().texOffs(82, 46).addBox(-6.0000000F, -47.2500000F, -1.2500000F, 12.0000000F, 2.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone17.addOrReplaceChild("cube_22", CubeListBuilder.create().texOffs(32, 123).addBox(2.5000000F, -48.7500000F, -7.7500000F, 1.0000000F, 3.0000000F, 1.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone17.addOrReplaceChild("cube_23", CubeListBuilder.create().texOffs(32, 131).addBox(5.5000000F, -48.2500000F, -8.0000000F, 1.0000000F, 2.0000000F, 1.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone17.addOrReplaceChild("cube_24", CubeListBuilder.create().texOffs(130, 46).addBox(-0.5000000F, -48.2500000F, -8.0000000F, 1.0000000F, 2.0000000F, 1.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone17.addOrReplaceChild("cube_25", CubeListBuilder.create().texOffs(36, 123).addBox(-3.5000000F, -48.7500000F, -7.7500000F, 1.0000000F, 3.0000000F, 1.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone17.addOrReplaceChild("cube_26", CubeListBuilder.create().texOffs(130, 49).addBox(-6.5000000F, -48.2500000F, -8.0000000F, 1.0000000F, 2.0000000F, 1.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone27 = bone1.addOrReplaceChild("Right Arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-16.0000000F, -1.0000000F, 0.0000000F, 0.0000000F, 3.1415927F, 0.0000000F));
        bone27.addOrReplaceChild("cube_28", CubeListBuilder.create().texOffs(40, 113).addBox(-9.0000000F, -10.0000000F, -1.0000000F, 10.0000000F, 10.0000000F, 11.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(5.0000000F, 5.0000000F, -4.5000000F, 0.0000000F, 0.0000000F, 0.1745329F));
        PartDefinition bone29 = bone27.addOrReplaceChild("Rock_2", CubeListBuilder.create(), PartPose.offsetAndRotation(7.0000000F, 2.0000000F, 0.2500000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone29.addOrReplaceChild("cube_30", CubeListBuilder.create().texOffs(86, 150).addBox(-3.0000000F, -10.0000000F, -1.0000000F, 4.0000000F, 10.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(-2.0000000F, 1.0000000F, -1.2500000F, 0.1745329F, 0.0000000F, -0.3926991F));
        bone29.addOrReplaceChild("cube_31", CubeListBuilder.create().texOffs(134, 0).addBox(-7.0000000F, -9.0000000F, -1.0000000F, 8.0000000F, 9.0000000F, 8.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(4.0000000F, 6.0000000F, -3.2500000F, 0.0000000F, 0.0000000F, -0.5235988F));
        PartDefinition bone32 = bone29.addOrReplaceChild("Rock_3", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0000000F, 7.5000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone32.addOrReplaceChild("cube_33", CubeListBuilder.create().texOffs(0, 151).addBox(-3.0000000F, -10.0000000F, -1.0000000F, 4.0000000F, 10.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, -3.5000000F, -0.2500000F, 0.5235988F, 0.0000000F, 0.1745329F));
        bone32.addOrReplaceChild("cube_34", CubeListBuilder.create().texOffs(82, 113).addBox(-9.0000000F, -13.0000000F, -1.0000000F, 10.0000000F, 13.0000000F, 10.0000000F, new CubeDeformation(0.1000000F)), PartPose.offsetAndRotation(2.0000000F, 7.5000000F, -4.2500000F, 0.0000000F, 0.0000000F, 0.0872665F));
        PartDefinition bone35 = bone32.addOrReplaceChild("Rock_4", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0000000F, 3.5000000F, -0.2500000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone35.addOrReplaceChild("cube_36", CubeListBuilder.create().texOffs(154, 132).addBox(-3.0000000F, -10.0000000F, -1.0000000F, 4.0000000F, 10.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.5235988F, 0.0000000F, 0.0000000F));
        bone35.addOrReplaceChild("cube_37", CubeListBuilder.create().texOffs(134, 17).addBox(-4.0000000F, -3.0000000F, -4.0000000F, 8.0000000F, 9.0000000F, 8.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone38 = bone35.addOrReplaceChild("Rock_5", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0000000F, 6.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone38.addOrReplaceChild("cube_39", CubeListBuilder.create().texOffs(156, 146).addBox(-3.0000000F, -10.0000000F, -1.0000000F, 4.0000000F, 10.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(2.0000000F, 0.0000000F, -1.0000000F, 0.1745329F, 0.0000000F, 0.0000000F));
        bone38.addOrReplaceChild("cube_40", CubeListBuilder.create().texOffs(122, 112).addBox(-5.0000000F, -3.0000000F, -5.0000000F, 10.0000000F, 10.0000000F, 10.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone41 = bone1.addOrReplaceChild("Left Arm", CubeListBuilder.create(), PartPose.offsetAndRotation(16.0000000F, -1.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone41.addOrReplaceChild("cube_42", CubeListBuilder.create().texOffs(76, 92).addBox(-9.0000000F, -10.0000000F, -1.0000000F, 10.0000000F, 10.0000000F, 11.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(5.0000000F, 5.0000000F, -4.5000000F, 0.0000000F, 0.0000000F, 0.1745329F));
        PartDefinition bone43 = bone41.addOrReplaceChild("Rock_10", CubeListBuilder.create(), PartPose.offsetAndRotation(7.0000000F, 2.0000000F, 0.2500000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone43.addOrReplaceChild("cube_44", CubeListBuilder.create().texOffs(22, 150).addBox(-3.0000000F, -10.0000000F, -1.0000000F, 4.0000000F, 10.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(-2.0000000F, 1.0000000F, -1.2500000F, 0.1745329F, 0.0000000F, -0.3926991F));
        bone43.addOrReplaceChild("cube_45", CubeListBuilder.create().texOffs(0, 123).addBox(-7.0000000F, -9.0000000F, -1.0000000F, 8.0000000F, 9.0000000F, 8.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(4.0000000F, 6.0000000F, -3.2500000F, 0.0000000F, 0.0000000F, -0.5235988F));
        PartDefinition bone46 = bone43.addOrReplaceChild("Rock_11", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0000000F, 7.5000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone46.addOrReplaceChild("cube_47", CubeListBuilder.create().texOffs(38, 150).addBox(-3.0000000F, -10.0000000F, -1.0000000F, 4.0000000F, 10.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, -3.5000000F, -0.2500000F, 0.5235988F, 0.0000000F, 0.1745329F));
        bone46.addOrReplaceChild("cube_48", CubeListBuilder.create().texOffs(0, 100).addBox(-9.0000000F, -13.0000000F, -1.0000000F, 10.0000000F, 13.0000000F, 10.0000000F, new CubeDeformation(0.1000000F)), PartPose.offsetAndRotation(2.0000000F, 7.5000000F, -4.2500000F, 0.0000000F, 0.0000000F, 0.0872665F));
        PartDefinition bone49 = bone46.addOrReplaceChild("Rock_12", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0000000F, 3.5000000F, -0.2500000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone49.addOrReplaceChild("cube_50", CubeListBuilder.create().texOffs(54, 150).addBox(-3.0000000F, -10.0000000F, -1.0000000F, 4.0000000F, 10.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.5235988F, 0.0000000F, 0.0000000F));
        bone49.addOrReplaceChild("cube_51", CubeListBuilder.create().texOffs(122, 132).addBox(-4.0000000F, -3.0000000F, -4.0000000F, 8.0000000F, 9.0000000F, 8.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone52 = bone49.addOrReplaceChild("Rock_13", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0000000F, 6.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone52.addOrReplaceChild("cube_53", CubeListBuilder.create().texOffs(70, 150).addBox(-3.0000000F, -10.0000000F, -1.0000000F, 4.0000000F, 10.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(2.0000000F, 0.0000000F, -1.0000000F, 0.1745329F, 0.0000000F, 0.0000000F));
        bone52.addOrReplaceChild("cube_54", CubeListBuilder.create().texOffs(118, 92).addBox(-5.0000000F, -3.0000000F, -5.0000000F, 10.0000000F, 10.0000000F, 10.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone55 = bone0.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, -11.2500000F, 1.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone56 = bone55.addOrReplaceChild("Up Body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, -6.7500000F, -1.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone56.addOrReplaceChild("cube_57", CubeListBuilder.create().texOffs(0, 52).addBox(-13.0000000F, -6.2500000F, -6.0000000F, 26.0000000F, 12.0000000F, 12.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone56.addOrReplaceChild("cube_58", CubeListBuilder.create().texOffs(0, 76).addBox(-13.0000000F, -6.2500000F, -6.0000000F, 26.0000000F, 12.0000000F, 12.0000000F, new CubeDeformation(0.2500000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone59 = bone55.addOrReplaceChild("Heart", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 2.2500000F, -1.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone59.addOrReplaceChild("cube_60", CubeListBuilder.create().texOffs(140, 50).addBox(-4.0000000F, -8.0000000F, -1.0000000F, 5.0000000F, 8.0000000F, 5.0000000F, new CubeDeformation(-1.0000000F)), PartPose.offsetAndRotation(-0.5928500F, 0.1347900F, -1.5000000F, 0.0000000F, 0.0000000F, 0.3490659F));
        bone59.addOrReplaceChild("cube_61", CubeListBuilder.create().texOffs(158, 98).addBox(-3.0000000F, -8.0000000F, -1.0000000F, 4.0000000F, 9.0000000F, 4.0000000F, new CubeDeformation(-1.0000000F)), PartPose.offsetAndRotation(-0.0928500F, -1.8652100F, -1.0000000F, 0.0000000F, 0.0000000F, -0.6544985F));
        PartDefinition bone62 = bone59.addOrReplaceChild("Pulsing heart", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone62.addOrReplaceChild("cube_63", CubeListBuilder.create().texOffs(158, 87).addBox(-4.0000000F, -7.0000000F, -1.0000000F, 5.0000000F, 7.0000000F, 4.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 3.7500000F, -1.0000000F, 0.0000000F, 0.0000000F, 0.3926991F));
        PartDefinition bone64 = bone55.addOrReplaceChild("Mid Body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 1.2500000F, -1.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone64.addOrReplaceChild("cube_65", CubeListBuilder.create().texOffs(0, 0).addBox(-15.0000000F, -6.2500000F, -7.0000000F, 27.0000000F, 12.0000000F, 14.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone64.addOrReplaceChild("cube_66", CubeListBuilder.create().texOffs(0, 26).addBox(-15.0000000F, -6.2500000F, -7.0000000F, 27.0000000F, 12.0000000F, 14.0000000F, new CubeDeformation(0.2500000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone67 = bone55.addOrReplaceChild("Low Body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 9.2500000F, -1.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone67.addOrReplaceChild("cube_68", CubeListBuilder.create().texOffs(76, 52).addBox(-11.0000000F, -5.2500000F, -5.0000000F, 22.0000000F, 10.0000000F, 10.0000000F, new CubeDeformation(0.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone67.addOrReplaceChild("cube_69", CubeListBuilder.create().texOffs(76, 72).addBox(-11.0000000F, -5.2500000F, -5.0000000F, 22.0000000F, 10.0000000F, 10.0000000F, new CubeDeformation(0.2500000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone70 = root.addOrReplaceChild("Lower Full Body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, -3.0000000F, 6.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone71 = bone70.addOrReplaceChild("Left Legs", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0000000F, 2.0000000F, -2.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone71.addOrReplaceChild("cube_72", CubeListBuilder.create().texOffs(134, 34).addBox(1.0000000F, -1.2500000F, -4.0000000F, 8.0000000F, 8.0000000F, 8.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone73 = bone71.addOrReplaceChild("Mid Leg L", CubeListBuilder.create(), PartPose.offsetAndRotation(5.0000000F, 8.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone73.addOrReplaceChild("cube_74", CubeListBuilder.create().texOffs(64, 136).addBox(-5.0000000F, -7.0000000F, -1.0000000F, 6.0000000F, 7.0000000F, 7.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(2.0000000F, 5.7500000F, -2.0000000F, 0.4363323F, 0.0000000F, 0.0000000F));
        PartDefinition bone75 = bone73.addOrReplaceChild("Foot L", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 4.0000000F, 0.2500000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone75.addOrReplaceChild("cube_76", CubeListBuilder.create().texOffs(140, 63).addBox(-2.5000000F, -1.2500000F, -2.5000000F, 5.0000000F, 7.0000000F, 5.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone75.addOrReplaceChild("cube_77", CubeListBuilder.create().texOffs(116, 149).addBox(-2.5000000F, 5.7500000F, -2.5000000F, 5.0000000F, 6.0000000F, 5.0000000F, new CubeDeformation(1.2500000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone78 = bone70.addOrReplaceChild("Right Legs", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0000000F, 2.0000000F, -2.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone78.addOrReplaceChild("cube_79", CubeListBuilder.create().texOffs(32, 134).addBox(-9.0000000F, -1.2500000F, -4.0000000F, 8.0000000F, 8.0000000F, 8.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        PartDefinition bone80 = bone78.addOrReplaceChild("Mid Leg R", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.0000000F, 8.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone80.addOrReplaceChild("cube_81", CubeListBuilder.create().texOffs(90, 136).addBox(-5.0000000F, -7.0000000F, -1.0000000F, 6.0000000F, 7.0000000F, 7.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(2.0000000F, 5.7500000F, -2.0000000F, 0.4363323F, 0.0000000F, 0.0000000F));
        PartDefinition bone82 = bone80.addOrReplaceChild("Foot R", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0000000F, 4.0000000F, 0.2500000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone82.addOrReplaceChild("cube_83", CubeListBuilder.create().texOffs(140, 75).addBox(-2.5000000F, -1.2500000F, -2.5000000F, 5.0000000F, 7.0000000F, 5.0000000F, new CubeDeformation(1.0000000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        bone82.addOrReplaceChild("cube_84", CubeListBuilder.create().texOffs(136, 149).addBox(-2.5000000F, 5.7500000F, -2.5000000F, 5.0000000F, 6.0000000F, 5.0000000F, new CubeDeformation(1.2500000F)), PartPose.offsetAndRotation(0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F, 0.0000000F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(HeartOfLunaRenderState state) {
        resetPose();
        if (state.blend < 1) animations[state.previousAnimation].apply((long) (state.previousTicks * 50.0F), 1 - state.blend);
        animations[state.animation].apply((long) (state.animationTicks * 50.0F), state.blend);
    }
}

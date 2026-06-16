package org.exodusstudio.stellaris.client.renderers.rockets.models;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class SmallRocketModel extends RocketModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("small_rocket"), "main");

    private final ModelPart pipes_1;
    private final ModelPart pipes_2;

    public SmallRocketModel(EntityModelSet context) {
        super(context.bakeLayer(LAYER_LOCATION));
        this.MainBody = root.getChild("MainBody");
        this.motor_upgrade = this.MainBody.getChild("motor_upgrade");
        this.pipes_1 = this.motor_upgrade.getChild("pipes_1");
        this.pipes_2 = this.motor_upgrade.getChild("pipes_2");
        this.sunflare_protection = this.MainBody.getChild("sunflare_protection");
        this.shield2 = this.sunflare_protection.getChild("shield2");
        this.shield1 = this.sunflare_protection.getChild("shield1");
        this.tank_upgrade = this.MainBody.getChild("tank_upgrade");
        this.Roof = root.getChild("Roof");
        this.RoofPlanes = this.Roof.getChild("RoofPlanes");
        this.RoofBars = this.Roof.getChild("RoofBars");
        this.RoofFrame = this.Roof.getChild("RoofFrame");
        this.RoofTop = this.Roof.getChild("RoofTop");
        this.Bottom = root.getChild("Bottom");
        this.BottomPlanes = this.Bottom.getChild("BottomPlanes");
        this.BottomBars = this.Bottom.getChild("BottomBars");
        this.BottomFrame = this.Bottom.getChild("BottomFrame");
        this.Wings = root.getChild("Wings");

        this.setDefaultModel();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition MainBody = partdefinition.addOrReplaceChild("MainBody", CubeListBuilder.create().texOffs(0, 133).addBox(-16.0F, -52.0F, -5.0F, 20.0F, 43.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(192, 39).addBox(-13.0F, -43.0F, -6.0F, 14.0F, 14.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(42, 133).addBox(-16.0F, -52.0F, 24.0F, 20.0F, 43.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(172, 0).addBox(4.0F, -52.0F, -5.0F, 5.0F, 43.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(172, 0).mirror().addBox(4.0F, -52.0F, 20.0F, 5.0F, 43.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(172, 0).addBox(-21.0F, -52.0F, 20.0F, 5.0F, 43.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(172, 0).mirror().addBox(-21.0F, -52.0F, -5.0F, 5.0F, 43.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 74).addBox(-19.0F, -16.0F, -4.0F, 26.0F, 4.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(84, 166).addBox(-13.0F, -17.0F, 3.0F, 14.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(128, 54).addBox(-14.0F, -21.0F, 2.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 8.0F, -10.0F));

        PartDefinition cube_r1 = MainBody.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(42, 133).mirror().addBox(-10.0F, -23.0F, 7.5F, 20.0F, 43.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-12.5F, -29.0F, 10.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r2 = MainBody.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(42, 133).addBox(-10.0F, -23.0F, -8.5F, 20.0F, 43.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -29.0F, 10.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition motor_upgrade = MainBody.addOrReplaceChild("motor_upgrade", CubeListBuilder.create().texOffs(159, 243).addBox(2.0F, -2.0F, -4.0F, 20.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-18.0F, -18.0F, -6.0F));

        PartDefinition cube_r3 = motor_upgrade.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(85, 229).addBox(-10.0F, -18.0F, -2.5F, 20.0F, 22.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(159, 243).addBox(-10.0F, 7.0F, -2.5F, 20.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, -9.0F, 33.5F, 0.0F, 3.1416F, 0.0F));

        PartDefinition pipes_1 = motor_upgrade.addOrReplaceChild("pipes_1", CubeListBuilder.create().texOffs(136, 250).addBox(-4.0F, -1.5F, -34.5F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(136, 250).mirror().addBox(24.0F, -1.5F, -34.5F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 0.5F, 32.5F));

        PartDefinition cube_r4 = pipes_1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(136, 250).mirror().addBox(-4.0F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r5 = pipes_1.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(136, 250).addBox(-4.0F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(28.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition pipes_2 = motor_upgrade.addOrReplaceChild("pipes_2", CubeListBuilder.create().texOffs(136, 240).addBox(-34.5F, -1.5F, -27.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(136, 240).mirror().addBox(-1.5F, -1.5F, -27.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(28.5F, 0.5F, 28.0F));

        PartDefinition cube_r6 = pipes_2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(136, 240).addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r7 = pipes_2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(136, 240).mirror().addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-33.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition sunflare_protection = MainBody.addOrReplaceChild("sunflare_protection", CubeListBuilder.create(), PartPose.offset(-6.0F, 16.0F, 10.0F));

        PartDefinition shield2 = sunflare_protection.addOrReplaceChild("shield2", CubeListBuilder.create().texOffs(192, 147).mirror().addBox(-22.0F, -68.0F, -15.0F, 2.0F, 39.0F, 30.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(240, 120).mirror().addBox(-20.0F, -38.0F, -13.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(240, 120).mirror().addBox(-20.0F, -67.0F, -13.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r8 = shield2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(240, 120).addBox(-2.5F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(240, 120).addBox(-2.5F, 27.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-17.5F, -65.5F, 11.5F, 0.0F, -3.1416F, 0.0F));

        PartDefinition cube_r9 = shield2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(231, 126).addBox(-1.0F, -19.5F, -5.5F, 2.0F, 39.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-17.3109F, -48.5F, 18.1891F, 0.0F, -2.3562F, 0.0F));

        PartDefinition cube_r10 = shield2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(231, 126).mirror().addBox(-1.0F, -20.0F, -11.0F, 2.0F, 39.0F, 11.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-21.2F, -48.0F, -14.3F, 0.0F, -0.7854F, 0.0F));

        PartDefinition shield1 = sunflare_protection.addOrReplaceChild("shield1", CubeListBuilder.create().texOffs(192, 147).addBox(-1.2F, -20.0F, -29.3F, 2.0F, 39.0F, 30.0F, new CubeDeformation(0.0F))
                .texOffs(240, 120).addBox(-6.2F, -19.0F, -27.3F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(240, 120).addBox(-6.2F, 10.0F, -27.3F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(21.2F, -48.0F, 14.3F));

        PartDefinition cube_r11 = shield1.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(240, 120).mirror().addBox(-2.5F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(240, 120).mirror().addBox(-2.5F, -30.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.7F, 11.5F, -2.8F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r12 = shield1.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(231, 126).mirror().addBox(-1.0F, -19.5F, -5.5F, 2.0F, 39.0F, 11.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.8891F, -0.5F, 3.8891F, 0.0F, 2.3562F, 0.0F));

        PartDefinition cube_r13 = shield1.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(231, 126).addBox(-1.0F, -20.0F, -11.0F, 2.0F, 39.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -28.6F, 0.0F, 0.7854F, 0.0F));

        PartDefinition tank_upgrade = MainBody.addOrReplaceChild("tank_upgrade", CubeListBuilder.create().texOffs(210, 217).addBox(-2.0F, -14.0F, -9.0F, 5.0F, 21.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(210, 217).mirror().addBox(33.0F, -14.0F, -9.0F, 5.0F, 21.0F, 18.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-24.0F, -14.0F, 10.0F));

        PartDefinition Roof = partdefinition.addOrReplaceChild("Roof", CubeListBuilder.create().texOffs(72, 183).addBox(3.5F, -30.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(192, 26).addBox(3.5F, -35.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(112, 183).addBox(6.5F, -42.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, -55.0F, 0.0F));

        PartDefinition RoofPlanes = Roof.addOrReplaceChild("RoofPlanes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r14 = RoofPlanes.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(128, 0).addBox(2.5F, -19.0F, -4.5F, 0.0F, 32.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(15.0F, -5.0F, -6.5F, 0.0F, 0.0F, -0.2618F));

        PartDefinition cube_r15 = RoofPlanes.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(164, 74).addBox(-17.5F, -19.0F, 2.5F, 22.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(15.0F, -5.0F, 6.5F, 0.2618F, 0.0F, 0.0F));

        PartDefinition cube_r16 = RoofPlanes.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(164, 74).addBox(-4.5F, -19.0F, -2.5F, 22.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -5.0F, -6.5F, -0.2618F, 0.0F, 0.0F));

        PartDefinition cube_r17 = RoofPlanes.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(128, 0).addBox(-2.5F, -19.0F, -17.5F, 0.0F, 32.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -5.0F, 6.5F, 0.0F, 0.0F, 0.2618F));

        PartDefinition RoofBars = Roof.addOrReplaceChild("RoofBars", CubeListBuilder.create(), PartPose.offset(15.0F, -1.0F, -6.5F));

        PartDefinition cube_r18 = RoofBars.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(140, 166).mirror().addBox(-0.5F, -24.0F, -3.5F, 4.0F, 38.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, -4.0F, -1.0F, -0.2618F, 0.0F, -0.2618F));

        PartDefinition cube_r19 = RoofBars.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(140, 166).mirror().addBox(-2.0F, -19.0F, -2.0F, 4.0F, 38.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0984F, -9.4283F, 14.1548F, 1.5708F, -1.309F, -1.8326F));

        PartDefinition cube_r20 = RoofBars.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(140, 166).addBox(-3.5F, -24.0F, -3.5F, 4.0F, 38.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, -4.0F, -1.0F, -0.2618F, 0.0F, 0.2618F));

        PartDefinition cube_r21 = RoofBars.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(140, 166).mirror().addBox(-2.0F, -19.0F, -2.0F, 4.0F, 38.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-14.0984F, -9.4283F, 14.1548F, 2.8798F, 0.0F, -2.8798F));

        PartDefinition RoofFrame = Roof.addOrReplaceChild("RoofFrame", CubeListBuilder.create(), PartPose.offset(9.0F, 17.5F, 0.0F));

        PartDefinition cube_r22 = RoofFrame.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(0, 37).addBox(-16.0F, 6.393F, -16.0F, 32.0F, 5.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.107F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition RoofTop = Roof.addOrReplaceChild("RoofTop", CubeListBuilder.create(), PartPose.offset(8.5F, -17.0F, 0.0F));

        PartDefinition Bottom = partdefinition.addOrReplaceChild("Bottom", CubeListBuilder.create(), PartPose.offset(0.0F, 21.607F, 0.0F));

        PartDefinition BottomPlanes = Bottom.addOrReplaceChild("BottomPlanes", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition cube_r23 = BottomPlanes.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(28, 155).addBox(2.5F, -13.0F, -4.5F, 0.0F, 17.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.5F, 2.393F, -6.5F, 0.0F, 0.0F, 0.2618F));

        PartDefinition cube_r24 = BottomPlanes.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(28, 177).addBox(-4.5F, -13.0F, -2.5F, 22.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, 2.393F, -7.5F, 0.2618F, 0.0F, 0.0F));

        PartDefinition cube_r25 = BottomPlanes.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(28, 155).addBox(-2.5F, -13.0F, -17.5F, 0.0F, 17.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, 2.393F, 6.5F, 0.0F, 0.0F, -0.2618F));

        PartDefinition cube_r26 = BottomPlanes.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(28, 177).addBox(-17.5F, -13.0F, 2.5F, 22.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, 2.393F, 7.5F, -0.2618F, 0.0F, 0.0F));

        PartDefinition BottomBars = Bottom.addOrReplaceChild("BottomBars", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition cube_r27 = BottomBars.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(192, 0).mirror().addBox(-1.5F, -14.0F, -5.5F, 7.0F, 18.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.5F, 1.393F, -6.5F, 0.2618F, 0.0F, 0.2618F));

        PartDefinition cube_r28 = BottomBars.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(192, 0).mirror().addBox(-1.5F, -14.0F, -2.5F, 7.0F, 18.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.5F, 1.393F, 6.5F, -0.2618F, 0.0F, 0.2618F));

        PartDefinition cube_r29 = BottomBars.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(192, 0).addBox(-5.5F, -14.0F, -5.5F, 7.0F, 18.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, 1.393F, -6.5F, 0.2618F, 0.0F, -0.2618F));

        PartDefinition cube_r30 = BottomBars.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(192, 0).addBox(-5.5F, -14.0F, -2.5F, 7.0F, 18.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, 1.393F, 6.5F, -0.2618F, 0.0F, -0.2618F));

        PartDefinition BottomFrame = Bottom.addOrReplaceChild("BottomFrame", CubeListBuilder.create().texOffs(0, 104).addBox(-12.0F, -7.607F, -12.0F, 24.0F, 5.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r31 = BottomFrame.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, 6.393F, -16.0F, 32.0F, 5.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition Wings = partdefinition.addOrReplaceChild("Wings", CubeListBuilder.create(), PartPose.offset(20.5F, 5.5F, 20.5F));

        PartDefinition cube_r32 = Wings.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(96, 120).mirror().addBox(-2.5F, -5.5F, -3.5F, 5.0F, 21.0F, 25.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.0F, -0.1534F, -2.8798F, -0.7854F, -3.1416F));

        PartDefinition cube_r33 = Wings.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(96, 120).addBox(-2.5F, -5.5F, -3.5F, 5.0F, 21.0F, 25.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-41.0F, -2.0F, -0.1534F, -2.8798F, 0.7854F, 3.1416F));

        PartDefinition cube_r34 = Wings.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(104, 74).mirror().addBox(-2.5F, -5.5F, -21.5F, 5.0F, 21.0F, 25.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.0F, -40.8466F, 2.8798F, 0.7854F, -3.1416F));

        PartDefinition cube_r35 = Wings.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(104, 74).addBox(-2.5F, -5.5F, -21.5F, 5.0F, 21.0F, 25.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-41.0F, -2.0F, -40.8466F, 2.8798F, -0.7854F, 3.1416F));

        PartDefinition cube_r36 = Wings.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(0, 177).addBox(-3.5F, -13.5F, -3.5F, 7.0F, 32.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-43.0F, 0.0F, 1.8466F, 3.1416F, 0.7854F, 3.1416F));

        PartDefinition cube_r37 = Wings.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(0, 177).mirror().addBox(-3.5F, -13.5F, -3.5F, 7.0F, 32.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 0.0F, 1.8466F, 3.1416F, -0.7854F, -3.1416F));

        PartDefinition cube_r38 = Wings.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(0, 177).mirror().addBox(-3.5F, -13.5F, -3.5F, 7.0F, 32.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 0.0F, -42.8466F, -3.1416F, 0.7854F, -3.1416F));

        PartDefinition cube_r39 = Wings.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(0, 177).addBox(-3.5F, -13.5F, -3.5F, 7.0F, 32.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-43.0F, 0.0F, -42.8466F, -3.1416F, -0.7854F, 3.1416F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }
}

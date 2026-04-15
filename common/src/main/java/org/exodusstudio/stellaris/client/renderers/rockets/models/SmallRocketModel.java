package org.exodusstudio.stellaris.client.renderers.rockets.models;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class SmallRocketModel extends RocketModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("small_rocket"), "main");


    public SmallRocketModel(EntityModelSet context) {
        super(context.bakeLayer(LAYER_LOCATION));
        this.MainBody = root.getChild("MainBody");
        this.sunflare_protection = this.MainBody.getChild("sunflare_protection");
        this.shield1 = this.sunflare_protection.getChild("shield1");
        this.shield2 = this.sunflare_protection.getChild("shield2");
        this.storage_upgrade = this.MainBody.getChild("storage_upgrade");
        this.tank_upgrade = this.MainBody.getChild("tank_upgrade");
        this.motor_upgrade = this.MainBody.getChild("motor_upgrade");
        this.pipes = this.motor_upgrade.getChild("pipes");
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

        PartDefinition MainBody = partdefinition.addOrReplaceChild("MainBody", CubeListBuilder.create().texOffs(50, 128).addBox(-14.0F, -41.0F, -1.0F, 16.0F, 35.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(116, 140).addBox(-13.0F, -35.0F, -2.0F, 14.0F, 14.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 140).addBox(-14.0F, -41.0F, 20.0F, 16.0F, 35.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(34, 140).addBox(2.0F, -41.0F, -1.0F, 3.0F, 35.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(34, 140).mirror().addBox(2.0F, -41.0F, 18.0F, 3.0F, 35.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(34, 140).addBox(-17.0F, -41.0F, 18.0F, 3.0F, 35.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(34, 140).mirror().addBox(-17.0F, -41.0F, -1.0F, 3.0F, 35.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 54).addBox(-16.0F, -10.0F, 0.0F, 20.0F, 4.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(124, 86).addBox(-13.0F, -11.0F, 3.0F, 14.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(50, 108).addBox(-14.0F, -15.0F, 2.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 8.0F, -10.0F));

        PartDefinition cube_r1 = MainBody.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 140).mirror().addBox(-8.0F, -15.0F, 7.5F, 16.0F, 35.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.5F, -26.0F, 10.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r2 = MainBody.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 140).addBox(-8.0F, -15.0F, -8.5F, 16.0F, 35.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -26.0F, 10.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition sunflare_protection = MainBody.addOrReplaceChild("sunflare_protection", CubeListBuilder.create(), PartPose.offset(-6.0F, 16.0F, 10.0F));

        PartDefinition shield1 = sunflare_protection.addOrReplaceChild("shield1", CubeListBuilder.create().texOffs(0, 202).mirror().addBox(-18.0F, -57.0F, -11.0F, 2.0F, 32.0F, 22.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(69, 250).addBox(-17.0F, -56.0F, 7.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(69, 250).addBox(-17.0F, -56.0F, -10.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(69, 250).addBox(-17.0F, -33.0F, 7.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(69, 250).addBox(-17.0F, -33.0F, -10.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r3 = shield1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(47, 215).addBox(-1.0F, -16.0F, -4.5F, 2.0F, 32.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.118F, -41.0F, 13.482F, 0.0F, -2.3562F, 0.0F));

        PartDefinition cube_r4 = shield1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(47, 215).mirror().addBox(-1.0F, -15.0F, -9.0F, 2.0F, 32.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.3F, -42.0F, -10.3F, 0.0F, -0.7854F, 0.0F));

        PartDefinition shield2 = sunflare_protection.addOrReplaceChild("shield2", CubeListBuilder.create().texOffs(69, 250).mirror().addBox(-3.118F, -15.0F, -23.482F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(69, 250).mirror().addBox(-3.118F, -15.0F, -6.482F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(69, 250).mirror().addBox(-3.118F, 8.0F, -6.482F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(69, 250).mirror().addBox(-3.118F, 8.0F, -23.482F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 202).addBox(1.882F, -16.0F, -24.482F, 2.0F, 32.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(14.118F, -41.0F, 13.482F));

        PartDefinition cube_r5 = shield2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(47, 215).mirror().addBox(-1.0F, -16.0F, -4.5F, 2.0F, 32.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3562F, 0.0F));

        PartDefinition cube_r6 = shield2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(47, 215).addBox(-1.0F, -15.0F, -9.0F, 2.0F, 32.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.182F, -1.0F, -23.782F, 0.0F, 0.7854F, 0.0F));

        PartDefinition storage_upgrade = MainBody.addOrReplaceChild("storage_upgrade", CubeListBuilder.create(), PartPose.offset(-6.0F, -20.5F, 23.5F));

        PartDefinition cube_r7 = storage_upgrade.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(117, 231).addBox(-7.0F, -16.5F, -2.5F, 14.0F, 20.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition tank_upgrade = MainBody.addOrReplaceChild("tank_upgrade", CubeListBuilder.create().texOffs(218, 226).addBox(-2.0F, -14.0F, -7.0F, 5.0F, 16.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(-20.0F, -6.0F, 10.0F));

        PartDefinition jerrycan2_r1 = tank_upgrade.addOrReplaceChild("jerrycan2_r1", CubeListBuilder.create().texOffs(218, 226).addBox(-2.0F, -14.0F, -7.0F, 5.0F, 16.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(28.0F, 0.0F, 0.0F, 0.0F, -3.1416F, 0.0F));

        PartDefinition motor_upgrade = MainBody.addOrReplaceChild("motor_upgrade", CubeListBuilder.create().texOffs(178, 244).addBox(-15.0F, -4.0F, -3.0F, 14.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -11.0F, -3.0F));

        PartDefinition cube_r8 = motor_upgrade.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(178, 244).addBox(-7.0F, -3.5F, -2.5F, 14.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -0.5F, 26.5F, 0.0F, 3.1416F, 0.0F));

        PartDefinition pipes = motor_upgrade.addOrReplaceChild("pipes", CubeListBuilder.create().texOffs(157, 242).addBox(3.0F, -3.0F, 2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(157, 250).addBox(-1.0F, -3.0F, -1.0F, 7.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(157, 250).mirror().addBox(-22.0F, -3.0F, -1.0F, 7.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(157, 242).mirror().addBox(-22.0F, -3.0F, 2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r9 = pipes.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(157, 242).mirror().addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-20.5F, -1.5F, 22.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r10 = pipes.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(157, 250).addBox(-3.5F, -1.5F, -1.5F, 7.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-18.5F, -1.5F, 25.5F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r11 = pipes.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(157, 242).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -1.5F, 22.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r12 = pipes.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(157, 250).mirror().addBox(-3.5F, -1.5F, -1.5F, 7.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, -1.5F, 25.5F, 0.0F, 3.1416F, 0.0F));

        PartDefinition Roof = partdefinition.addOrReplaceChild("Roof", CubeListBuilder.create(), PartPose.offset(-8.5F, -52.0F, 0.0F));

        PartDefinition RoofPlanes = Roof.addOrReplaceChild("RoofPlanes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r13 = RoofPlanes.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(96, 0).addBox(1.5F, -19.0F, -4.5F, 0.0F, 32.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, 5.0F, -6.5F, 0.0F, 0.0F, -0.2618F));

        PartDefinition cube_r14 = RoofPlanes.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(114, 108).addBox(-17.5F, -19.0F, 1.5F, 22.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(15.0F, 5.0F, 5.5F, 0.2618F, 0.0F, 0.0F));

        PartDefinition cube_r15 = RoofPlanes.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(114, 108).addBox(-4.5F, -19.0F, -1.5F, 22.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 5.0F, -5.5F, -0.2618F, 0.0F, 0.0F));

        PartDefinition cube_r16 = RoofPlanes.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(80, 54).addBox(-1.5F, -19.0F, -17.5F, 0.0F, 32.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 5.0F, 6.5F, 0.0F, 0.0F, 0.2618F));

        PartDefinition RoofBars = Roof.addOrReplaceChild("RoofBars", CubeListBuilder.create(), PartPose.offset(15.0F, -1.0F, -6.5F));

        PartDefinition cube_r17 = RoofBars.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(104, 140).mirror().addBox(-1.5F, -19.0F, -1.5F, 3.0F, 32.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, -0.2618F, 0.0F, -0.2618F));

        PartDefinition cube_r18 = RoofBars.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(104, 140).addBox(-1.5F, -19.0F, -1.5F, 3.0F, 32.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 13.0F, 0.2618F, 0.0F, -0.2618F));

        PartDefinition cube_r19 = RoofBars.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(104, 140).addBox(-1.5F, -19.0F, -1.5F, 3.0F, 32.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, 6.0F, 0.0F, -0.2618F, 0.0F, 0.2618F));

        PartDefinition cube_r20 = RoofBars.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(104, 140).mirror().addBox(-1.5F, -19.0F, -1.5F, 3.0F, 32.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-13.0F, 6.0F, 13.0F, 0.2618F, 0.0F, 0.2618F));

        PartDefinition RoofFrame = Roof.addOrReplaceChild("RoofFrame", CubeListBuilder.create(), PartPose.offset(9.0F, 17.5F, 0.0F));

        PartDefinition cube_r21 = RoofFrame.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(0, 27).addBox(-12.0F, 6.393F, -12.0F, 24.0F, 3.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 8.107F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition RoofTop = Roof.addOrReplaceChild("RoofTop", CubeListBuilder.create().texOffs(146, 140).addBox(-3.0F, 2.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(116, 155).addBox(-1.5F, -8.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(146, 149).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, -17.0F, 0.0F));

        PartDefinition Bottom = partdefinition.addOrReplaceChild("Bottom", CubeListBuilder.create(), PartPose.offset(0.0F, 21.607F, 0.0F));

        PartDefinition BottomPlanes = Bottom.addOrReplaceChild("BottomPlanes", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition cube_r22 = BottomPlanes.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(140, 30).addBox(-11.0F, -0.0265F, 8.005F, 22.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, 0.0F, 0.0F));

        PartDefinition cube_r23 = BottomPlanes.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(124, 54).addBox(-8.005F, -0.0265F, -11.0F, 0.0F, 10.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, -0.2618F));

        PartDefinition cube_r24 = BottomPlanes.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(140, 30).addBox(-11.0F, -0.0265F, -8.005F, 22.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8798F, 0.0F, 0.0F));

        PartDefinition cube_r25 = BottomPlanes.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(124, 54).addBox(8.005F, -0.0265F, -11.0F, 0.0F, 10.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.2618F));

        PartDefinition BottomBars = Bottom.addOrReplaceChild("BottomBars", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition cube_r26 = BottomBars.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(128, 155).mirror().addBox(-8.9709F, 2.0089F, -8.4948F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8798F, 0.0F, -0.2618F));

        PartDefinition cube_r27 = BottomBars.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(128, 155).addBox(5.9709F, 2.0089F, -8.4948F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8798F, 0.0F, 0.2618F));

        PartDefinition cube_r28 = BottomBars.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(128, 155).mirror().addBox(5.9709F, 2.0089F, 5.4948F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, 0.0F, 0.2618F));

        PartDefinition cube_r29 = BottomBars.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(128, 155).addBox(-8.9709F, 2.0089F, 5.4948F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, 0.0F, -0.2618F));

        PartDefinition BottomFrame = Bottom.addOrReplaceChild("BottomFrame", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r30 = BottomFrame.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(0, 78).addBox(-10.0F, 6.393F, -10.0F, 20.0F, 3.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r31 = BottomFrame.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, 6.393F, -12.0F, 24.0F, 3.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition Wings = partdefinition.addOrReplaceChild("Wings", CubeListBuilder.create(), PartPose.offset(20.5F, 5.5F, 20.5F));

        PartDefinition cube_r32 = Wings.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(84, 128).addBox(-2.5F, -9.5F, -2.5F, 5.0F, 28.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r33 = Wings.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(0, 101).addBox(0.5F, -12.0F, -9.5F, 2.0F, 16.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 5.5F, -9.0F, -0.2618F, 0.7854F, 0.0F));

        PartDefinition cube_r34 = Wings.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(84, 128).addBox(-2.5F, -9.5F, -2.5F, 5.0F, 28.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -40.8466F, -3.1416F, 0.7854F, 3.1416F));

        PartDefinition cube_r35 = Wings.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(0, 101).addBox(-12.8073F, -9.7233F, -5.6519F, 2.0F, 16.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-20.5F, 2.3049F, -36.9233F, 2.8798F, 0.7854F, -3.1416F));

        PartDefinition cube_r36 = Wings.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(84, 128).addBox(-2.5F, -9.5F, -2.5F, 5.0F, 28.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-41.0F, 0.0F, -40.8466F, -3.1416F, -0.7854F, 3.1416F));

        PartDefinition cube_r37 = Wings.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(0, 101).addBox(10.8073F, -9.7233F, -5.6519F, 2.0F, 16.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-20.5F, 2.3049F, -36.9233F, 2.8798F, -0.7854F, 3.1416F));

        PartDefinition cube_r38 = Wings.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(84, 128).addBox(-2.5F, -15.0F, -2.5F, 5.0F, 28.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-41.0F, 5.5F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r39 = Wings.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(0, 101).addBox(-2.5F, -12.0F, -9.5F, 2.0F, 16.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-30.0F, 5.5F, -9.0F, -0.2618F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }
}

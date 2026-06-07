package org.exodusstudio.stellaris.client.renderers.rockets.models;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class TinyRocketModel extends RocketModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("tiny_rocket"), "main");

	public TinyRocketModel(EntityModelSet context) {
        super(context.bakeLayer(LAYER_LOCATION));
        this.MainBody = root.getChild("MainBody");
		this.tank_upgrade = this.MainBody.getChild("tank_upgrade");
		this.sunflare_protection = this.MainBody.getChild("sunflare_protection");
		this.shield2 = this.sunflare_protection.getChild("shield2");
		this.shield1 = this.sunflare_protection.getChild("shield1");
		this.storage_upgrade = this.MainBody.getChild("storage_upgrade");

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

		PartDefinition MainBody = partdefinition.addOrReplaceChild("MainBody", CubeListBuilder.create().texOffs(112, 0).addBox(-16.0F, -70.0F, -3.0F, 20.0F, 60.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(178, 57).addBox(-13.0F, -55.0F, -4.0F, 14.0F, 14.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 112).addBox(-16.0F, -70.0F, 22.0F, 20.0F, 60.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 157).mirror().addBox(4.0F, -70.0F, -3.0F, 3.0F, 60.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(86, 157).addBox(4.0F, -70.0F, 20.0F, 3.0F, 60.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(86, 157).mirror().addBox(-19.0F, -70.0F, 20.0F, 3.0F, 60.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(86, 157).addBox(-19.0F, -70.0F, -3.0F, 3.0F, 60.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(44, 88).addBox(-16.0F, -12.0F, 0.0F, 20.0F, 4.0F, 20.0F, new CubeDeformation(0.0F))
		.texOffs(154, 0).addBox(-13.0F, -29.0F, 3.0F, 14.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 62).addBox(-18.0F, -26.0F, -3.0F, 24.0F, 1.0F, 25.0F, new CubeDeformation(0.0F))
		.texOffs(138, 127).addBox(-14.0F, -32.0F, 2.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 8.0F, -10.0F));

		PartDefinition cube_r1 = MainBody.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(44, 112).mirror().addBox(-10.0F, -14.0F, 7.5F, 20.0F, 60.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-10.5F, -56.0F, 10.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r2 = MainBody.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(44, 112).addBox(-10.0F, -14.0F, -8.5F, 20.0F, 60.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -56.0F, 10.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition tank_upgrade = MainBody.addOrReplaceChild("tank_upgrade", CubeListBuilder.create().texOffs(214, 222).addBox(-4.0F, -15.0F, -8.0F, 5.0F, 18.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-20.0F, -11.0F, 10.0F));

		PartDefinition jerrycan2_r1 = tank_upgrade.addOrReplaceChild("jerrycan2_r1", CubeListBuilder.create().texOffs(214, 222).addBox(-2.0F, -15.0F, -8.0F, 5.0F, 18.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(30.0F, 0.0F, 0.0F, 0.0F, -3.1416F, 0.0F));

		PartDefinition sunflare_protection = MainBody.addOrReplaceChild("sunflare_protection", CubeListBuilder.create(), PartPose.offset(-6.0F, 16.0F, 10.0F));

		PartDefinition shield2 = sunflare_protection.addOrReplaceChild("shield2", CubeListBuilder.create().texOffs(0, 175).mirror().addBox(20.0F, -85.0F, -13.0F, 2.0F, 52.0F, 26.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 188).addBox(15.0F, -84.0F, -12.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 188).addBox(15.0F, -38.0F, -12.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 188).mirror().addBox(-17.0F, -38.0F, -12.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 188).mirror().addBox(-17.0F, -84.0F, -12.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition cube_r3 = shield2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 188).addBox(-3.0F, -1.5F, -1.5F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 188).addBox(-3.0F, 44.5F, -1.5F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, -82.5F, 10.5F, 0.0F, -3.1416F, 0.0F));

		PartDefinition cube_r4 = shield2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 188).mirror().addBox(-3.0F, -1.5F, -1.5F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 188).mirror().addBox(-3.0F, -47.5F, -1.5F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(18.0F, -36.5F, 10.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r5 = shield2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(56, 189).mirror().addBox(-1.0F, -24.0F, -12.0F, 2.0F, 52.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(21.3F, -61.0F, -12.3F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r6 = shield2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(56, 189).addBox(-1.0F, -25.0F, -6.0F, 2.0F, 52.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0574F, -60.0F, 16.5426F, 0.0F, 2.3562F, 0.0F));

		PartDefinition shield1 = sunflare_protection.addOrReplaceChild("shield1", CubeListBuilder.create().texOffs(0, 175).addBox(-22.0F, -85.0F, -13.0F, 2.0F, 52.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition cube_r7 = shield1.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(56, 189).addBox(-1.0F, -24.0F, -12.0F, 2.0F, 52.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-21.3F, -61.0F, -12.3F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r8 = shield1.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(56, 189).mirror().addBox(-1.0F, -25.0F, -6.0F, 2.0F, 52.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.0574F, -60.0F, 16.5426F, 0.0F, -2.3562F, 0.0F));

		PartDefinition storage_upgrade = MainBody.addOrReplaceChild("storage_upgrade", CubeListBuilder.create(), PartPose.offset(-6.0F, 16.0F, 10.0F));

		PartDefinition container_r1 = storage_upgrade.addOrReplaceChild("container_r1", CubeListBuilder.create().texOffs(214, 149).addBox(-2.5F, -22.0F, -8.0F, 5.0F, 31.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -48.0F, 15.5F, 0.0F, 1.5708F, 0.0F));

		PartDefinition motor_upgrade = MainBody.addOrReplaceChild("motor_upgrade", CubeListBuilder.create().texOffs(170, 244).addBox(-16.0F, -4.0F, -5.0F, 16.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -16.0F, -3.0F));

		PartDefinition cube_r9 = motor_upgrade.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(170, 244).addBox(-8.0F, -3.5F, -2.5F, 16.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -0.5F, 28.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition pipes = motor_upgrade.addOrReplaceChild("pipes", CubeListBuilder.create().texOffs(145, 240).addBox(5.0F, -3.0F, 0.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(145, 250).addBox(-1.0F, -3.0F, -3.0F, 9.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(145, 250).mirror().addBox(-24.0F, -3.0F, -3.0F, 9.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(145, 240).mirror().addBox(-24.0F, -3.0F, 0.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r10 = pipes.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(145, 240).mirror().addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-22.5F, -1.5F, 22.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r11 = pipes.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(145, 250).addBox(-3.5F, -1.5F, -1.5F, 9.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-18.5F, -1.5F, 27.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r12 = pipes.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(145, 240).addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, -1.5F, 22.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r13 = pipes.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(145, 250).mirror().addBox(-5.5F, -1.5F, -1.5F, 9.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, -1.5F, 27.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition Roof = partdefinition.addOrReplaceChild("Roof", CubeListBuilder.create(), PartPose.offset(-8.5F, -52.0F, 0.0F));

		PartDefinition RoofPlanes = Roof.addOrReplaceChild("RoofPlanes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r14 = RoofPlanes.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 88).addBox(1.5F, -19.0F, -4.5F, 0.0F, 41.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, -31.0F, -6.5F, 0.0F, 0.0F, -0.2182F));

		PartDefinition cube_r15 = RoofPlanes.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(138, 86).addBox(-17.5F, -19.0F, 1.5F, 22.0F, 41.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(15.0F, -31.0F, 5.5F, 0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r16 = RoofPlanes.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(138, 86).addBox(-4.5F, -19.0F, -1.5F, 22.0F, 41.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -31.0F, -5.5F, -0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r17 = RoofPlanes.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(0, 88).addBox(-1.5F, -19.0F, -17.5F, 0.0F, 41.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -31.0F, 6.5F, 0.0F, 0.0F, 0.2182F));

		PartDefinition RoofBars = Roof.addOrReplaceChild("RoofBars", CubeListBuilder.create(), PartPose.offset(15.0F, -1.0F, -6.5F));

		PartDefinition cube_r18 = RoofBars.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(98, 157).mirror().addBox(-1.5F, -19.0F, -1.5F, 3.0F, 41.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -30.0F, 0.0F, -0.2182F, 0.0F, -0.2182F));

		PartDefinition cube_r19 = RoofBars.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(98, 157).addBox(-1.5F, -19.0F, -1.5F, 3.0F, 41.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -30.0F, 13.0F, 0.2182F, 0.0F, -0.2182F));

		PartDefinition cube_r20 = RoofBars.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(98, 157).addBox(-1.5F, -19.0F, -1.5F, 3.0F, 41.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, -30.0F, 0.0F, -0.2182F, 0.0F, 0.2182F));

		PartDefinition cube_r21 = RoofBars.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(98, 157).mirror().addBox(-1.5F, -20.5F, -1.5F, 3.0F, 41.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-13.3136F, -28.5855F, 13.3882F, 0.2182F, 0.0F, 0.2182F));

		PartDefinition RoofFrame = Roof.addOrReplaceChild("RoofFrame", CubeListBuilder.create(), PartPose.offset(9.0F, 17.5F, 0.0F));

		PartDefinition cube_r22 = RoofFrame.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(0, 31).addBox(-14.0F, 6.393F, -14.0F, 28.0F, 3.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -20.893F, 0.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition RoofTop = Roof.addOrReplaceChild("RoofTop", CubeListBuilder.create().texOffs(178, 41).addBox(-4.0F, -34.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(124, 86).addBox(-1.5F, -48.0F, -1.5F, 3.0F, 19.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(178, 72).addBox(-4.0F, -39.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, -17.0F, 0.0F));

		PartDefinition Bottom = partdefinition.addOrReplaceChild("Bottom", CubeListBuilder.create(), PartPose.offset(0.0F, 21.607F, 0.0F));

		PartDefinition BottomPlanes = Bottom.addOrReplaceChild("BottomPlanes", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, 0.0F));

		PartDefinition cube_r23 = BottomPlanes.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(42, 173).addBox(-11.0F, -1.0265F, 8.005F, 22.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, 0.0F, 0.0F));

		PartDefinition cube_r24 = BottomPlanes.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(138, 147).addBox(-8.005F, -1.0265F, -11.0F, 0.0F, 16.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, -0.2618F));

		PartDefinition cube_r25 = BottomPlanes.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(42, 173).addBox(-11.0F, -1.0265F, -8.005F, 22.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8798F, 0.0F, 0.0F));

		PartDefinition cube_r26 = BottomPlanes.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(138, 147).addBox(8.005F, -1.0265F, -11.0F, 0.0F, 16.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.2618F));

		PartDefinition BottomBars = Bottom.addOrReplaceChild("BottomBars", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, 0.0F));

		PartDefinition cube_r27 = BottomBars.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(110, 157).addBox(-8.9709F, 1.0089F, 4.4948F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, 0.0F, -0.2618F));

		PartDefinition cube_r28 = BottomBars.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(110, 157).mirror().addBox(4.9709F, 1.0089F, 4.4948F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, 0.0F, 0.2618F));

		PartDefinition cube_r29 = BottomBars.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(110, 157).mirror().addBox(-8.9709F, 1.0089F, -8.4948F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8798F, 0.0F, -0.2618F));

		PartDefinition cube_r30 = BottomBars.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(110, 157).addBox(4.9709F, 1.0089F, -8.4948F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.8798F, 0.0F, 0.2618F));

		PartDefinition BottomFrame = Bottom.addOrReplaceChild("BottomFrame", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r31 = BottomFrame.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(98, 62).addBox(-10.0F, 5.393F, -10.0F, 20.0F, 4.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition cube_r32 = BottomFrame.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, 6.393F, -14.0F, 28.0F, 3.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -15.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition Wings = partdefinition.addOrReplaceChild("Wings", CubeListBuilder.create(), PartPose.offset(20.5F, 5.5F, 20.5F));

		PartDefinition cube_r33 = Wings.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(154, 17).mirror().addBox(-3.0F, -17.0F, -3.0F, 6.0F, 34.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5929F, 1.5F, -43.5466F, -3.1416F, 0.7854F, -3.1416F));

		PartDefinition cube_r34 = Wings.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(86, 112).mirror().addBox(-0.5F, -18.0F, -13.5F, 3.0F, 22.0F, 23.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.9F, 5.5F, -33.5F, 0.2618F, -0.7854F, 0.0F));

		PartDefinition cube_r35 = Wings.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(154, 17).addBox(-3.0F, -17.0F, -3.0F, 6.0F, 34.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-43.5929F, 1.5F, -43.5466F, -3.1416F, -0.7854F, 3.1416F));

		PartDefinition cube_r36 = Wings.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(86, 112).addBox(-2.5F, -18.0F, -13.5F, 3.0F, 22.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-32.1F, 5.5F, -33.5F, 0.2618F, 0.7854F, 0.0F));

		PartDefinition cube_r37 = Wings.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(154, 17).addBox(-3.0F, -17.0F, -3.0F, 6.0F, 34.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-43.5929F, 1.5F, 2.5466F, 3.1416F, 0.7854F, 3.1416F));

		PartDefinition cube_r38 = Wings.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(86, 112).addBox(-1.5F, -11.0F, -11.5F, 3.0F, 22.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-35.4542F, -0.7438F, -5.56F, -2.8798F, 0.7854F, 3.1416F));

		PartDefinition cube_r39 = Wings.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(154, 17).mirror().addBox(-3.0F, -17.0F, -3.0F, 6.0F, 34.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5929F, 1.5F, 2.5466F, 3.1416F, -0.7854F, -3.1416F));

		PartDefinition cube_r40 = Wings.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(86, 112).mirror().addBox(-1.5F, -11.0F, -11.5F, 3.0F, 22.0F, 23.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.5458F, -0.7438F, -5.56F, -2.8798F, -0.7854F, -3.1416F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}
}

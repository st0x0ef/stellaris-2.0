package org.exodusstudio.stellaris.client.renderers.mobs.starcrawlerboss;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.IntroState;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.DeathCinematicState;

public class StarCrawlerBossModel extends EntityModel<StarCrawlerBossRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(IdentifierUtils.id("star_crawler_boss"), "main");

	private final ModelPart body;
	private final ModelPart circle;
	private final ModelPart crystals;
	private final ModelPart crown;
	private final ModelPart bone2;
	private final ModelPart bone;
	private final ModelPart arm;
	private final ModelPart limb;
	private final ModelPart hand;
	private final ModelPart arm2;
	private final ModelPart limb2;
	private final ModelPart hand2;
	private final ModelPart arm3;
	private final ModelPart limb3;
	private final ModelPart hand3;
	private final ModelPart arm4;
	private final ModelPart limb4;
	private final ModelPart hand4;
	private final ModelPart backArmRoot;
	private final ModelPart backarm;
	private final ModelPart backlimb;
	private final ModelPart backhand;
	private final KeyframeAnimation walkingAnimation;
	private final KeyframeAnimation jumpSlamAnimation;
	private final KeyframeAnimation groundSmashAnimation;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation healingAnimation;
	private final KeyframeAnimation chargeAnimation;

	public StarCrawlerBossModel(ModelPart root) {
		this(root, false);
	}

	public StarCrawlerBossModel(ModelPart root, boolean crystalOnly) {
		super(root);
		this.body = root.getChild("Body");
		this.circle = this.body.getChild("circle");
		this.crystals = this.body.getChild("crystals");
		this.crown = this.body.getChild("crown");
		this.bone2 = this.crown.getChild("bone2");
		this.bone = this.crown.getChild("bone");
		this.arm = root.getChild("arm");
		this.limb = this.arm.getChild("limb");
		this.hand = this.limb.getChild("hand");
		this.arm2 = root.getChild("arm2");
		this.limb2 = this.arm2.getChild("limb2");
		this.hand2 = this.limb2.getChild("hand2");
		this.arm3 = root.getChild("arm3");
		this.limb3 = this.arm3.getChild("limb3");
		this.hand3 = this.limb3.getChild("hand3");
		this.arm4 = root.getChild("arm4");
		this.limb4 = this.arm4.getChild("limb4");
		this.hand4 = this.limb4.getChild("hand4");
		this.backArmRoot = root.getChild("Back_arm");
		this.backarm = this.backArmRoot.getChild("backarm");
		this.backlimb = this.backarm.getChild("backlimb");
		this.backhand = this.backlimb.getChild("backhand");

		this.walkingAnimation = StarCrawlerBossAnimations.WALKING.bake(root);
		this.jumpSlamAnimation = StarCrawlerBossAnimations.JUMP_SLAM.bake(root);
		this.groundSmashAnimation = StarCrawlerBossAnimations.GROUND_SMASH.bake(root);
		this.idleAnimation = StarCrawlerBossAnimations.IDLE.bake(root);
		this.healingAnimation = StarCrawlerBossAnimations.HEALING.bake(root);
		this.chargeAnimation = StarCrawlerBossAnimations.CHARGE.bake(root);

		if (crystalOnly) {
			root.getAllParts().forEach(part -> part.skipDraw = true);
			this.crystals.getAllParts().forEach(part -> part.skipDraw = false);
		}
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -13.0F, -11.0F, 22.0F, 13.0F, 22.0F, new CubeDeformation(0.0F))
		.texOffs(0, 65).addBox(-7.0F, -16.0F, -7.0F, 14.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 35).addBox(-10.0F, -6.0F, -10.0F, 20.0F, 10.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition cube_r1 = Body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(158, 124).addBox(-13.0F, -4.0F, -1.0F, 14.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -12.293F, 8.8051F, 0.6981F, 0.0F, 0.0F));

		PartDefinition cube_r2 = Body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(144, 70).addBox(-13.0F, -4.0F, -1.0F, 14.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -12.293F, -8.8051F, -0.6981F, 0.0F, 0.0F));

		PartDefinition cube_r3 = Body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 135).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.8051F, -12.293F, -6.0F, 0.0F, 0.0F, 0.6981F));

		PartDefinition cube_r4 = Body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(130, 77).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.8051F, -12.293F, -6.0F, 0.0F, 0.0F, -0.6981F));

		PartDefinition cube_r5 = Body.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(180, 140).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4875F, -12.2929F, 4.1124F, -0.7854F, -0.5236F, 0.0F));

		PartDefinition cube_r6 = Body.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(124, 179).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4875F, -12.2929F, 4.1124F, -0.7854F, 0.5236F, 0.0F));

		PartDefinition circle = Body.addOrReplaceChild("circle", CubeListBuilder.create().texOffs(106, 180).addBox(-3.75F, -13.75F, -12.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(6, 185).addBox(2.75F, -7.25F, -12.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 185).addBox(2.75F, -13.75F, -12.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(104, 77).addBox(-4.0F, -14.0F, -11.5F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(52, 185).addBox(-3.75F, -7.25F, -12.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition cube_r7 = circle.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(160, 183).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.342F, -11.6897F, -0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r8 = circle.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(184, 6).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0545F, -5.6125F, -11.4397F, -0.3491F, 0.0F, -0.2618F));

		PartDefinition cube_r9 = circle.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(184, 3).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8112F, -13.4798F, -11.0977F, 0.3491F, 0.0F, 0.2618F));

		PartDefinition cube_r10 = circle.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(184, 9).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0545F, -5.6125F, -11.4397F, -0.3491F, 0.0F, 0.2618F));

		PartDefinition cube_r11 = circle.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(184, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8112F, -13.4798F, -11.0977F, 0.3491F, 0.0F, -0.2618F));

		PartDefinition cube_r12 = circle.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(184, 167).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.1287F, -6.9796F, -11.4397F, 0.0F, -0.3491F, -0.2618F));

		PartDefinition cube_r13 = circle.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(184, 182).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.221F, -7.2228F, -11.0977F, 0.0F, 0.3491F, 0.2618F));

		PartDefinition cube_r14 = circle.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(184, 171).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7386F, -10.8453F, -11.0977F, 0.0F, 0.3491F, -0.2618F));

		PartDefinition cube_r15 = circle.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(114, 184).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.6463F, -11.0885F, -11.4397F, 0.0F, -0.3491F, 0.2618F));

		PartDefinition cube_r16 = circle.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(108, 184).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.658F, -9.0F, -11.6897F, 0.0F, -0.3491F, 0.0F));

		PartDefinition cube_r17 = circle.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(34, 183).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7183F, -9.0F, -11.3477F, 0.0F, 0.3491F, 0.0F));

		PartDefinition cube_r18 = circle.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(88, 162).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.7183F, -11.3477F, 0.3491F, 0.0F, 0.0F));

		PartDefinition crystals = Body.addOrReplaceChild("crystals", CubeListBuilder.create().texOffs(0, 168).addBox(-8.0F, -18.3274F, -1.0914F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(78, 136).addBox(-10.0F, -8.3273F, -3.0914F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -13.75F, -4.0F, -0.9163F, 0.0F, 0.0F));

		PartDefinition cube_r19 = crystals.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(132, 169).addBox(-2.0F, -11.0F, -1.0F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -3.6694F, 0.3483F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r20 = crystals.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(156, 169).addBox(-2.0F, -11.0F, -1.0F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4397F, -3.6694F, 0.4086F, 0.0F, 0.0F, 0.3491F));

		PartDefinition cube_r21 = crystals.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(68, 139).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3842F, 0.4682F, -0.2025F, 0.0F, 0.6981F, 0.3491F));

		PartDefinition cube_r22 = crystals.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(122, 77).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.3842F, 0.4682F, -0.2025F, 0.0F, -0.6981F, -0.3491F));

		PartDefinition cube_r23 = crystals.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(144, 169).addBox(-2.0F, -11.0F, -1.0F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.6206F, -4.0114F, 0.4086F, 0.0F, 0.0F, -0.3491F));

		PartDefinition cube_r24 = crystals.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(16, 168).addBox(-2.0F, -11.0F, -1.0F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -4.0114F, 0.5292F, -0.3491F, 0.0F, 0.0F));

		PartDefinition crown = Body.addOrReplaceChild("crown", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.1F, 1.0F, 17.0F, -0.6981F, 0.0F, 0.0F));

		PartDefinition cube_r25 = crown.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(176, 67).addBox(-6.0F, -6.0F, -1.0F, 7.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.868F, 8.5608F, -28.1573F, 0.0873F, -0.5236F, 0.0F));

		PartDefinition cube_r26 = crown.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(116, 169).addBox(-7.0F, -10.0F, -1.0F, 8.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.734F, 5.5608F, -26.9073F, 0.0F, -0.5236F, 0.0F));

		PartDefinition cube_r27 = crown.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(168, 176).addBox(-6.0F, -6.0F, -1.0F, 7.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.8058F, 8.5608F, -30.6573F, 0.0873F, 0.5236F, 0.0F));

		PartDefinition cube_r28 = crown.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(168, 0).addBox(-7.0F, -10.0F, -1.0F, 8.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.8058F, 5.5608F, -29.9073F, 0.0F, 0.5236F, 0.0F));

		PartDefinition bone2 = crown.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offsetAndRotation(8.1F, 4.0F, -19.75F, -0.2618F, 0.0F, 0.0F));

		PartDefinition cube_r29 = bone2.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(92, 183).addBox(-3.0F, -6.0F, -1.0F, 4.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4955F, -10.4392F, -11.0544F, 0.0F, 0.6109F, 0.0F));

		PartDefinition cube_r30 = bone2.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(100, 183).addBox(-3.0F, -6.0F, -1.0F, 4.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.634F, -10.4392F, -9.9073F, 0.0F, -0.6109F, 0.0F));

		PartDefinition bone = crown.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, -1.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition cube_r31 = bone.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(176, 59).addBox(-5.0F, -8.0F, -1.0F, 6.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.734F, -1.4392F, -26.9073F, 0.0F, -0.5672F, 0.0F));

		PartDefinition cube_r32 = bone.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(176, 51).addBox(-5.0F, -8.0F, -1.0F, 6.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.7482F, -1.4392F, -29.0565F, 0.0F, 0.5672F, 0.0F));

		PartDefinition arm = partdefinition.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(88, 0).addBox(-10.0F, -5.0F, -6.0F, 11.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(82, 124).addBox(-9.0F, 5.0F, -5.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(126, 158).addBox(-7.0F, -6.0F, -3.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 15.0F, 5.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r33 = arm.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(160, 41).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.369F, -8.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r34 = arm.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(160, 12).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.343F, 3.9393F, -0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r35 = arm.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(56, 177).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.366F, -4.384F, -2.5F, 0.0F, 0.0F, -0.5236F));

		PartDefinition cube_r36 = arm.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(102, 32).addBox(-4.0F, -1.0F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -4.634F, 0.933F, -0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r37 = arm.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(44, 177).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.634F, -4.384F, -2.5F, 0.0F, 0.0F, 0.5236F));

		PartDefinition cube_r38 = arm.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(88, 32).addBox(-4.0F, -1.0F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -4.134F, -3.799F, 0.5236F, 0.0F, 0.0F));

		PartDefinition limb = arm.addOrReplaceChild("limb", CubeListBuilder.create().texOffs(146, 136).addBox(-12.0F, 4.0F, -3.0F, 11.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(82, 108).addBox(-13.0F, -4.0F, -4.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(68, 148).addBox(-6.0F, -4.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 1.0F, -1.0F));

		PartDefinition cube_r39 = limb.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(160, 36).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 5.369F, -6.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r40 = limb.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(66, 151).addBox(-3.0F, -1.0F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5F, -3.4F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition cube_r41 = limb.addOrReplaceChild("cube_r41", CubeListBuilder.create().texOffs(158, 130).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 4.343F, 3.9394F, -0.3491F, 0.0F, 0.0F));

		PartDefinition hand = limb.addOrReplaceChild("hand", CubeListBuilder.create().texOffs(80, 63).addBox(-5.0F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(84, 63).addBox(-9.0F, -3.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(88, 20).addBox(-12.0F, -3.0F, -3.0F, 12.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(130, 12).addBox(-11.0F, 3.0F, -2.0F, 11.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, 1.0F, 0.0F));

		PartDefinition cube_r42 = hand.addOrReplaceChild("cube_r42", CubeListBuilder.create().texOffs(126, 164).addBox(-8.0F, 0.0F, -1.0F, 9.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 3.343F, 2.9394F, -0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r43 = hand.addOrReplaceChild("cube_r43", CubeListBuilder.create().texOffs(154, 164).addBox(-8.0F, 0.0F, -1.0F, 9.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 4.369F, -5.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition arm2 = partdefinition.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(46, 88).addBox(-10.0F, -5.0F, -4.0F, 11.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(120, 124).addBox(-9.0F, 5.0F, -3.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(44, 172).addBox(-7.0F, -6.0F, -1.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 15.0F, -5.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r44 = arm2.addOrReplaceChild("cube_r44", CubeListBuilder.create().texOffs(28, 161).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.369F, -6.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r45 = arm2.addOrReplaceChild("cube_r45", CubeListBuilder.create().texOffs(160, 46).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.343F, 5.9394F, -0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r46 = arm2.addOrReplaceChild("cube_r46", CubeListBuilder.create().texOffs(112, 179).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.366F, -4.384F, -0.5F, 0.0F, 0.0F, -0.5236F));

		PartDefinition cube_r47 = arm2.addOrReplaceChild("cube_r47", CubeListBuilder.create().texOffs(174, 161).addBox(-4.0F, -1.0F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -4.634F, 2.933F, -0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r48 = arm2.addOrReplaceChild("cube_r48", CubeListBuilder.create().texOffs(28, 178).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.634F, -4.384F, -0.5F, 0.0F, 0.0F, 0.5236F));

		PartDefinition cube_r49 = arm2.addOrReplaceChild("cube_r49", CubeListBuilder.create().texOffs(164, 32).addBox(-4.0F, -1.0F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -4.134F, -1.799F, 0.5236F, 0.0F, 0.0F));

		PartDefinition limb2 = arm2.addOrReplaceChild("limb2", CubeListBuilder.create().texOffs(146, 146).addBox(-12.0F, 4.0F, -3.0F, 11.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 119).addBox(-13.0F, -4.0F, -4.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(116, 166).addBox(-6.0F, -4.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 1.0F, 1.0F));

		PartDefinition cube_r50 = limb2.addOrReplaceChild("cube_r50", CubeListBuilder.create().texOffs(162, 76).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 5.369F, -6.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r51 = limb2.addOrReplaceChild("cube_r51", CubeListBuilder.create().texOffs(52, 182).addBox(-3.0F, -1.0F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5F, -3.4F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition cube_r52 = limb2.addOrReplaceChild("cube_r52", CubeListBuilder.create().texOffs(58, 162).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 4.343F, 3.9394F, -0.3491F, 0.0F, 0.0F));

		PartDefinition hand2 = limb2.addOrReplaceChild("hand2", CubeListBuilder.create().texOffs(92, 63).addBox(-5.0F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(96, 63).addBox(-9.0F, -3.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(130, 95).addBox(-12.0F, -3.0F, -3.0F, 12.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(66, 154).addBox(-11.0F, 3.0F, -2.0F, 11.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, 1.0F, 0.0F));

		PartDefinition cube_r53 = hand2.addOrReplaceChild("cube_r53", CubeListBuilder.create().texOffs(88, 166).addBox(-8.0F, 0.0F, -1.0F, 9.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 3.343F, 2.9394F, -0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r54 = hand2.addOrReplaceChild("cube_r54", CubeListBuilder.create().texOffs(28, 166).addBox(-8.0F, 0.0F, -1.0F, 9.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 4.369F, -5.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition arm3 = partdefinition.addOrReplaceChild("arm3", CubeListBuilder.create().texOffs(88, 88).addBox(-10.0F, -5.0F, -6.0F, 11.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(40, 127).addBox(-9.0F, 5.0F, -5.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(62, 172).addBox(-7.0F, -6.0F, -3.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, 15.0F, -5.0F));

		PartDefinition cube_r55 = arm3.addOrReplaceChild("cube_r55", CubeListBuilder.create().texOffs(162, 86).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.369F, -8.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r56 = arm3.addOrReplaceChild("cube_r56", CubeListBuilder.create().texOffs(162, 81).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.343F, 3.9394F, -0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r57 = arm3.addOrReplaceChild("cube_r57", CubeListBuilder.create().texOffs(180, 149).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.366F, -4.384F, -2.5F, 0.0F, 0.0F, -0.5236F));

		PartDefinition cube_r58 = arm3.addOrReplaceChild("cube_r58", CubeListBuilder.create().texOffs(178, 32).addBox(-4.0F, -1.0F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -4.634F, 0.933F, -0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r59 = arm3.addOrReplaceChild("cube_r59", CubeListBuilder.create().texOffs(80, 180).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.634F, -4.384F, -2.5F, 0.0F, 0.0F, 0.5236F));

		PartDefinition cube_r60 = arm3.addOrReplaceChild("cube_r60", CubeListBuilder.create().texOffs(176, 73).addBox(-4.0F, -1.0F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -4.134F, -3.799F, 0.5236F, 0.0F, 0.0F));

		PartDefinition limb3 = arm3.addOrReplaceChild("limb3", CubeListBuilder.create().texOffs(110, 148).addBox(-12.0F, 4.0F, -3.0F, 11.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(122, 108).addBox(-13.0F, -4.0F, -4.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(140, 183).addBox(-6.0F, -4.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 1.0F, -1.0F));

		PartDefinition cube_r61 = limb3.addOrReplaceChild("cube_r61", CubeListBuilder.create().texOffs(162, 112).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 5.369F, -6.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r62 = limb3.addOrReplaceChild("cube_r62", CubeListBuilder.create().texOffs(64, 182).addBox(-3.0F, -1.0F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5F, -3.4F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition cube_r63 = limb3.addOrReplaceChild("cube_r63", CubeListBuilder.create().texOffs(162, 107).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 4.343F, 3.9394F, -0.3491F, 0.0F, 0.0F));

		PartDefinition hand3 = limb3.addOrReplaceChild("hand3", CubeListBuilder.create().texOffs(100, 63).addBox(-5.0F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 86).addBox(-9.0F, -3.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(110, 136).addBox(-12.0F, -3.0F, -3.0F, 12.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(144, 156).addBox(-11.0F, 3.0F, -2.0F, 11.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, 1.0F, 0.0F));

		PartDefinition cube_r64 = hand3.addOrReplaceChild("cube_r64", CubeListBuilder.create().texOffs(166, 96).addBox(-8.0F, 0.0F, -1.0F, 9.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 3.343F, 2.9394F, -0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r65 = hand3.addOrReplaceChild("cube_r65", CubeListBuilder.create().texOffs(166, 91).addBox(-8.0F, 0.0F, -1.0F, 9.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 4.369F, -5.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition arm4 = partdefinition.addOrReplaceChild("arm4", CubeListBuilder.create().texOffs(0, 99).addBox(-10.0F, -5.0F, -4.0F, 11.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(130, 0).addBox(-9.0F, 5.0F, -3.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(174, 156).addBox(-7.0F, -6.0F, -1.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, 15.0F, 5.0F));

		PartDefinition cube_r66 = arm4.addOrReplaceChild("cube_r66", CubeListBuilder.create().texOffs(164, 17).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.369F, -6.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r67 = arm4.addOrReplaceChild("cube_r67", CubeListBuilder.create().texOffs(162, 117).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.343F, 5.9393F, -0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r68 = arm4.addOrReplaceChild("cube_r68", CubeListBuilder.create().texOffs(14, 182).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.366F, -4.384F, -0.5F, 0.0F, 0.0F, -0.5236F));

		PartDefinition cube_r69 = arm4.addOrReplaceChild("cube_r69", CubeListBuilder.create().texOffs(0, 182).addBox(-4.0F, -1.0F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -4.634F, 2.933F, -0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r70 = arm4.addOrReplaceChild("cube_r70", CubeListBuilder.create().texOffs(180, 135).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.634F, -4.384F, -0.5F, 0.0F, 0.0F, 0.5236F));

		PartDefinition cube_r71 = arm4.addOrReplaceChild("cube_r71", CubeListBuilder.create().texOffs(92, 180).addBox(-4.0F, -1.0F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -4.134F, -1.799F, 0.5236F, 0.0F, 0.0F));

		PartDefinition limb4 = arm4.addOrReplaceChild("limb4", CubeListBuilder.create().texOffs(32, 151).addBox(-12.0F, 4.0F, -3.0F, 11.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(124, 20).addBox(-13.0F, -4.0F, -4.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(150, 183).addBox(-6.0F, -4.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 1.0F, 1.0F));

		PartDefinition cube_r72 = limb4.addOrReplaceChild("cube_r72", CubeListBuilder.create().texOffs(164, 27).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 5.369F, -6.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r73 = limb4.addOrReplaceChild("cube_r73", CubeListBuilder.create().texOffs(182, 164).addBox(-3.0F, -1.0F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5F, -3.4F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition cube_r74 = limb4.addOrReplaceChild("cube_r74", CubeListBuilder.create().texOffs(164, 22).addBox(-9.0F, 0.0F, -1.0F, 10.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 4.343F, 3.9393F, -0.3491F, 0.0F, 0.0F));

		PartDefinition hand4 = limb4.addOrReplaceChild("hand4", CubeListBuilder.create().texOffs(108, 86).addBox(-5.0F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 86).addBox(-9.0F, -3.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 139).addBox(-12.0F, -3.0F, -3.0F, 12.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(96, 158).addBox(-11.0F, 3.0F, -2.0F, 11.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, 1.0F, 0.0F));

		PartDefinition cube_r75 = hand4.addOrReplaceChild("cube_r75", CubeListBuilder.create().texOffs(56, 167).addBox(-8.0F, 0.0F, -1.0F, 9.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 3.343F, 2.9393F, -0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r76 = hand4.addOrReplaceChild("cube_r76", CubeListBuilder.create().texOffs(166, 101).addBox(-8.0F, 0.0F, -1.0F, 9.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 4.369F, -5.7584F, 0.3491F, 0.0F, 0.0F));

		PartDefinition Back_arm = partdefinition.addOrReplaceChild("Back_arm", CubeListBuilder.create().texOffs(56, 65).addBox(-6.0F, -5.5F, -1.0F, 12.0F, 11.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 82).addBox(-5.0F, 5.5F, -3.0F, 10.0F, 4.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(80, 56).addBox(-2.5F, -6.5F, 2.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(132, 183).addBox(6.7139F, 0.3302F, 4.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(46, 82).addBox(-8.7861F, 0.3302F, 4.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.5F, 11.0F));

		PartDefinition cube_r77 = Back_arm.addOrReplaceChild("cube_r77", CubeListBuilder.create().texOffs(96, 154).addBox(-4.0F, -1.0F, -1.0F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -4.884F, 8.366F, -0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r78 = Back_arm.addOrReplaceChild("cube_r78", CubeListBuilder.create().texOffs(28, 171).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.866F, -4.884F, 3.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition cube_r79 = Back_arm.addOrReplaceChild("cube_r79", CubeListBuilder.create().texOffs(168, 169).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.866F, -4.884F, 3.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition cube_r80 = Back_arm.addOrReplaceChild("cube_r80", CubeListBuilder.create().texOffs(160, 51).addBox(-4.0F, -1.0F, -1.0F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -4.884F, 1.634F, 0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r81 = Back_arm.addOrReplaceChild("cube_r81", CubeListBuilder.create().texOffs(42, 103).addBox(-10.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8072F, 1.1783F, 6.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition cube_r82 = Back_arm.addOrReplaceChild("cube_r82", CubeListBuilder.create().texOffs(42, 99).addBox(-10.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(15.4412F, 11.1783F, 6.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition cube_r83 = Back_arm.addOrReplaceChild("cube_r83", CubeListBuilder.create().texOffs(98, 171).addBox(-12.5F, -5.0F, -1.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3616F, 8.3651F, 4.5F, 0.0F, 0.0F, 0.6981F));

		PartDefinition cube_r84 = Back_arm.addOrReplaceChild("cube_r84", CubeListBuilder.create().texOffs(84, 171).addBox(-12.5F, -5.0F, -1.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(16.4914F, -5.7763F, 4.5F, 0.0F, 0.0F, -0.6981F));

		PartDefinition backarm = Back_arm.addOrReplaceChild("backarm", CubeListBuilder.create().texOffs(80, 35).addBox(-5.0F, -4.5F, 0.0F, 10.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(104, 56).addBox(-4.0F, 4.5F, -1.0F, 8.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(78, 127).addBox(6.3212F, 0.5642F, 3.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 131).addBox(6.3212F, 0.5642F, 8.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 153).addBox(-7.4288F, 0.5642F, 8.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 157).addBox(-7.4288F, 0.5642F, 3.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 10.0F));

		PartDefinition cube_r85 = backarm.addOrReplaceChild("cube_r85", CubeListBuilder.create().texOffs(176, 182).addBox(-11.5F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(168, 182).addBox(-11.5F, -4.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.8486F, -5.5423F, 9.0F, 0.0F, 0.0F, -0.6981F));

		PartDefinition cube_r86 = backarm.addOrReplaceChild("cube_r86", CubeListBuilder.create().texOffs(26, 183).addBox(-11.5F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(182, 176).addBox(-11.5F, -4.0F, 4.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2384F, 7.9562F, 4.0F, 0.0F, 0.0F, 0.6981F));

		PartDefinition cube_r87 = backarm.addOrReplaceChild("cube_r87", CubeListBuilder.create().texOffs(116, 32).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.9F, 9.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition cube_r88 = backarm.addOrReplaceChild("cube_r88", CubeListBuilder.create().texOffs(40, 182).addBox(-2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -4.25F, 4.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition backlimb = backarm.addOrReplaceChild("backlimb", CubeListBuilder.create().texOffs(42, 108).addBox(-4.0F, -3.5F, -1.0F, 8.0F, 7.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(124, 36).addBox(-3.0F, 3.5F, -2.0F, 6.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(88, 63).addBox(-0.5F, -3.75F, 8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 11.0F));

		PartDefinition cube_r89 = backlimb.addOrReplaceChild("cube_r89", CubeListBuilder.create().texOffs(32, 135).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.75F, 4.5F, -0.1309F, 0.0F, 0.0F));

		PartDefinition backhand = backlimb.addOrReplaceChild("backhand", CubeListBuilder.create().texOffs(144, 55).addBox(-3.0F, -2.5F, -1.0F, 6.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 153).addBox(-2.0F, 2.5F, -2.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 10.0F));

		PartDefinition cube_r90 = backhand.addOrReplaceChild("cube_r90", CubeListBuilder.create().texOffs(68, 177).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.95F, 4.0F, -0.0524F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(StarCrawlerBossRenderState state) {
		this.resetPose();

		if (state.deathCinematicState == DeathCinematicState.ALIVE) switch (state.combatState) {
			case HEALING_PHASE_2, HEALING_PHASE_3 ->
					this.healingAnimation.apply(state.healingAnimationState, state.ageInTicks, 1.0F);

			case CHARGE_WINDUP, CHARGING, CHARGE_RECOVERY ->
					this.chargeAnimation.apply(state.chargeAnimationState, state.ageInTicks, 1.0F);

			case JUMP_SLAM_WINDUP, JUMP_SLAM_AIRBORNE, JUMP_SLAM_IMPACT, JUMP_SLAM_RECOVERY ->
					this.jumpSlamAnimation.apply(state.jumpSlamAnimationState, state.ageInTicks, 1.0F);

			case GROUND_SMASH_WINDUP, GROUND_SMASH_IMPACT, GROUND_SMASH_RECOVERY ->
					this.groundSmashAnimation.apply(state.groundSmashAnimationState, state.ageInTicks, 1.0F);

			case IDLE, CHASING -> {
				if (state.walkAnimationSpeed > 0.02F) {
					this.walkingAnimation.applyWalk(
							state.walkAnimationPos,
							state.walkAnimationSpeed,
							2.5F,
							2.5F
					);
				} else {
					this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks, 1.0F);
				}
			}
		}

		if (state.introState == IntroState.PLAYING
				&& state.deathCinematicState == DeathCinematicState.ALIVE
				&& state.introTicks >= 76.0F
				&& state.introTicks <= 108.0F) {
			float localTicks = state.introTicks - 76.0F;
			float fadeIn = smoothstep(Math.min(1.0F, localTicks / 8.0F));
			float fadeOut = smoothstep(Math.min(1.0F, (108.0F - state.introTicks) / 8.0F));
			float weight = Math.min(fadeIn, fadeOut) * 0.62F;
			long animationMillis = (long) (Math.min(localTicks, 29.0F) * 50.0F);
			this.groundSmashAnimation.apply(animationMillis, weight);
		}

		if (state.deathCinematicState != DeathCinematicState.ALIVE) {
			this.applyDeathCollapse(
					state.deathCinematicState == DeathCinematicState.FINALIZED
							? 140.0F
							: state.deathCinematicTicks
			);
		}
	}

	private void applyDeathCollapse(float ticks) {
		float realization = smoothstep(clampRange(ticks, 0.0F, 14.0F));
		float failing = smoothstep(clampRange(ticks, 12.0F, 70.0F));
		float collapse = smoothstep(clampRange(ticks, 62.0F, 104.0F));
		float settle = smoothstep(clampRange(ticks, 104.0F, 128.0F));
		float struggle = (float) Math.sin(ticks * 0.27F)
				* (1.0F - collapse)
				* failing;

		this.body.xRot += -0.10F * realization + 0.18F * failing + 0.78F * collapse;
		this.body.zRot += 0.035F * failing - 0.17F * collapse;
		this.body.y += 1.15F * failing + 7.25F * collapse + 0.35F * settle;
		this.body.x += 0.38F * collapse;

		this.arm.xRot += 0.13F * failing + 0.62F * collapse;
		this.arm.zRot += -0.08F * failing - 0.20F * collapse + struggle * 0.035F;
		this.limb.xRot += 0.28F * failing + 0.54F * collapse;
		this.hand.xRot += 0.34F * collapse;

		this.arm2.xRot += 0.08F * failing + 0.78F * collapse;
		this.arm2.zRot += 0.10F * failing + 0.14F * collapse - struggle * 0.025F;
		this.limb2.xRot += 0.18F * failing + 0.70F * collapse;
		this.hand2.zRot += 0.12F * collapse;

		this.arm3.xRot += 0.17F * failing + 0.48F * collapse;
		this.arm3.zRot += -0.05F * failing - 0.11F * collapse;
		this.limb3.xRot += 0.31F * failing + 0.42F * collapse;
		this.hand3.xRot += 0.26F * collapse;

		this.arm4.xRot += 0.11F * failing + 0.69F * collapse;
		this.arm4.zRot += 0.075F * failing + 0.22F * collapse;
		this.limb4.xRot += 0.22F * failing + 0.62F * collapse;
		this.hand4.zRot -= 0.10F * collapse;

		this.backArmRoot.xRot += 0.10F * failing + 0.52F * collapse;
		this.backArmRoot.y += 0.65F * failing + 4.0F * collapse;
		this.backarm.xRot += 0.22F * failing + 0.47F * collapse;
		this.backlimb.xRot += 0.18F * failing + 0.34F * collapse;
		this.backhand.xRot += 0.24F * collapse;

		this.crown.xRot += 0.07F * failing + 0.24F * collapse;
		this.crown.zRot -= 0.055F * collapse;
		float crystalTremor = (float) Math.sin(ticks * 1.35F)
				* (1.0F - smoothstep(clampRange(ticks, 102.0F, 122.0F)));
		this.crystals.zRot += crystalTremor * (0.018F + failing * 0.035F);
		this.crystals.xRot += crystalTremor * 0.015F;
	}

	private static float clampRange(float value, float start, float end) {
		return Math.max(0.0F, Math.min(1.0F, (value - start) / (end - start)));
	}

	private static float smoothstep(float value) {
		return value * value * (3.0F - 2.0F * value);
	}
}

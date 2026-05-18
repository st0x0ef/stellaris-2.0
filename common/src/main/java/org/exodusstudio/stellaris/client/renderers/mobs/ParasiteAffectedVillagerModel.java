package org.exodusstudio.stellaris.client.renderers.mobs;

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
import net.minecraft.util.Mth;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class ParasiteAffectedVillagerModel extends EntityModel<StellarisMobRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("mob_parasite_affected_villager"), "main");

    private static final float MODEL_ROOT_BASE_Y = 24.0F;

    private static final float DEATH_GROUNDING_EXTRA_Y = 12.0F;

    protected final ModelPart modelRoot;
    protected final ModelPart body;
    protected final ModelPart head;
    protected final ModelPart arms;
    protected final ModelPart rightLeg;
    protected final ModelPart leftLeg;
    protected final ModelPart mouth;
    protected final ModelPart rightMandible;
    protected final ModelPart leftMandible;
    protected final ModelPart shoulderGrowthLeft;
    protected final ModelPart shoulderGrowthRight;

    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation deathAnimation;
    private final KeyframeAnimation attackAnimation;

    public ParasiteAffectedVillagerModel(ModelPart root) {
        super(root);

        this.modelRoot = root.getChild("root");
        this.body = this.modelRoot.getChild("body");
        this.head = this.body.getChild("head");
        this.arms = this.body.getChild("arms");
        this.rightLeg = this.modelRoot.getChild("right_leg");
        this.leftLeg = this.modelRoot.getChild("left_leg");
        this.mouth = this.head.getChild("mouth");
        this.rightMandible = this.mouth.getChild("m_r");
        this.leftMandible = this.mouth.getChild("m_l");
        this.shoulderGrowthLeft = this.body.getChild("shoulder_growth_left");
        this.shoulderGrowthRight = this.body.getChild("shoulder_growth_right");

        this.idleAnimation = ParasiteAffectedVillagerAnimations.IDLE.bake(root);
        this.walkAnimation = ParasiteAffectedVillagerAnimations.WALK.bake(root);
        this.deathAnimation = ParasiteAffectedVillagerAnimations.DEATH.bake(root);
        this.attackAnimation = ParasiteAffectedVillagerAnimations.ATTACK.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, MODEL_ROOT_BASE_Y, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(16, 18).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.45F))
                .texOffs(38, 41).addBox(4.05F, -5.0F, 0.0F, 6.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 50).addBox(-12.0F, -11.0F, 2.0F, 8.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -9.7F, -3.75F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-4.0F, -9.7F, -3.75F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.51F))
                .texOffs(28, 33).addBox(-1.0F, -15.7F, 0.15F, 0.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(52, 40).addBox(-10.0F, -13.7F, -0.75F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 50).addBox(4.0F, -10.7F, 1.25F, 8.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.3F, -0.25F));

        head.addOrReplaceChild("nose", CubeListBuilder.create()
                        .texOffs(24, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.7F, -4.75F, -0.3927F, 0.0F, 0.0F));

        head.addOrReplaceChild("head_spike_left", CubeListBuilder.create()
                        .texOffs(30, 4).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.5F, -5.7F, -3.45F, 0.0F, 0.7854F, 0.0F));

        head.addOrReplaceChild("head_spike_right", CubeListBuilder.create()
                        .texOffs(30, 4).mirror().addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(3.5F, -5.7F, -3.45F, 0.0F, -0.7854F, 0.0F));

        PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create(), PartPose.offset(0.0F, -0.9F, -4.2F));

        PartDefinition rightMandible = mouth.addOrReplaceChild("m_r", CubeListBuilder.create(), PartPose.offset(-1.5F, 0.0F, 0.0F));
        rightMandible.addOrReplaceChild("m_r_r1", CubeListBuilder.create()
                        .texOffs(40, 55).addBox(-2.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition leftMandible = mouth.addOrReplaceChild("m_l", CubeListBuilder.create(), PartPose.offset(1.5F, 0.0F, 0.0F));
        leftMandible.addOrReplaceChild("m_l_r1", CubeListBuilder.create()
                        .texOffs(40, 50).mirror().addBox(0.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        body.addOrReplaceChild("arms", CubeListBuilder.create()
                        .texOffs(40, 32).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(44, 18).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(44, 18).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(0.0F, -9.05F, -1.05F, -0.7505F, 0.0F, 0.0F));

        body.addOrReplaceChild("shoulder_growth_left", CubeListBuilder.create()
                        .texOffs(28, 48).addBox(0.0F, -5.0F, -2.5F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.4F, -12.0F, -4.25F, 0.0F, -0.3927F, 0.0F));

        body.addOrReplaceChild("shoulder_growth_right", CubeListBuilder.create()
                        .texOffs(38, 35).addBox(0.0F, -4.5F, -2.3F, 0.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.2F, -3.8F, 7.25F, 0.0F, -0.7854F, 0.0F));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(0, 18).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, -12.0F, 0.0F));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(0, 18).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, -12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(StellarisMobRenderState state) {
        this.resetAllPoses();

        if (state.dying) {
            this.deathAnimation.apply(state.parasiteAffectedVillagerDeathAnimationState, state.ageInTicks, 1.0F);
            this.groundDeathAnimation(state.deathProgress);
            return;
        }

        this.idleAnimation.apply(state.parasiteAffectedVillagerIdleAnimationState, state.ageInTicks, 1.0F);
        this.walkAnimation.apply(state.parasiteAffectedVillagerWalkAnimationState, state.ageInTicks, 1.0F);
        this.attackAnimation.apply(state.parasiteAffectedVillagerAttackAnimationState, state.ageInTicks, 1.0F);

        this.applyLook(state);
    }

    private void resetAllPoses() {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }

    private void groundDeathAnimation(float deathProgress) {
        float death = Mth.clamp(deathProgress, 0.0F, 1.0F);
        float easedDeath = death * death * (3.0F - 2.0F * death);

        this.modelRoot.y = MODEL_ROOT_BASE_Y - DEATH_GROUNDING_EXTRA_Y * easedDeath;
    }

    private void applyLook(StellarisMobRenderState state) {
        this.head.yRot += state.headYaw * Mth.DEG_TO_RAD * 0.45F;
        this.head.xRot += state.headPitch * Mth.DEG_TO_RAD * 0.35F;
    }
}
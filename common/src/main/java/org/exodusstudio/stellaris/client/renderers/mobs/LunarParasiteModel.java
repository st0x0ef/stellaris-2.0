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
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class LunarParasiteModel extends EntityModel<StellarisMobRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("mob_lunar_parasite"), "main");

    private final ModelPart parasiteRoot;
    private final ModelPart mainSegment;
    private final ModelPart segment3;
    private final ModelPart segment2;
    private final ModelPart segment1;
    private final ModelPart mouth;

    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation moveAnimation;
    private final KeyframeAnimation infectAnimation;
    private final KeyframeAnimation attachedAnimation;

    public LunarParasiteModel(ModelPart root) {
        super(root);

        this.parasiteRoot = root.getChild("root");
        this.mainSegment = this.parasiteRoot.getChild("main_segment");
        this.segment3 = this.parasiteRoot.getChild("segment_3");
        this.segment2 = this.parasiteRoot.getChild("segment_2");
        this.segment1 = this.parasiteRoot.getChild("segment_1");
        this.mouth = this.parasiteRoot.getChild("mouth");

        this.idleAnimation = LunarParasiteAnimations.IDLE.bake(root);
        this.moveAnimation = LunarParasiteAnimations.MOVE.bake(root);
        this.infectAnimation = LunarParasiteAnimations.INFECT.bake(root);
        this.attachedAnimation = LunarParasiteAnimations.ATTACHED.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, -0.75F));

        PartDefinition mainSegment = root.addOrReplaceChild("main_segment", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -1.7658F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 19).addBox(-4.0F, -0.7658F, -2.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 22).addBox(3.0F, -0.7658F, -2.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.2342F, -1.0F));

        mainSegment.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(20, 22).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.45F, 1.4342F, 0.0F, 0.0F, 0.0F, 0.3927F));
        mainSegment.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 23).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.35F, 3.5842F, 0.0F, 0.0F, 0.0F, 0.7854F));
        mainSegment.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(20, 4).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.35F, 3.5842F, 0.0F, 0.0F, 0.0F, -0.7854F));
        mainSegment.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(20, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.45F, 1.4342F, 0.0F, 0.0F, 0.0F, -0.3927F));
        mainSegment.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(10, 26).addBox(0.0F, 0.0F, -2.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.35F, -2.6658F, 0.0F, 0.0F, 0.0F, 0.3927F));
        mainSegment.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(24, 16).addBox(0.0F, -1.0F, -2.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.35F, -2.6658F, 0.0F, 0.0F, 0.0F, 0.3927F));
        mainSegment.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(26, 8).addBox(0.0F, 0.0F, -2.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.35F, -2.6658F, 0.0F, 0.0F, 0.0F, -0.3927F));
        mainSegment.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 16).addBox(0.0F, -1.0F, -2.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.35F, -2.6658F, 0.0F, 0.0F, 0.0F, -0.3927F));

        root.addOrReplaceChild("segment_3", CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, -1.025F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, -1.0F, 5.0F));

        root.addOrReplaceChild("segment_2", CubeListBuilder.create().texOffs(16, 10).addBox(-1.0F, -1.0207F, -0.9833F, 2.0F, 3.0F, 3.0F, new CubeDeformation(-0.02F))
                .texOffs(20, 8).addBox(0.0F, -1.9587F, 0.5167F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.9793F, 3.9833F));

        PartDefinition segment1 = root.addOrReplaceChild("segment_1", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -1.781F, -0.95F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, -3.219F, 0.95F));

        segment1.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(24, 26).addBox(0.0F, 0.0F, -2.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.35F, -2.681F, 2.05F, 0.0F, 0.0F, 0.3927F));
        segment1.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(18, 26).addBox(0.0F, 0.0F, -2.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.35F, -2.681F, 2.05F, 0.0F, 0.0F, -0.3927F));
        segment1.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 31).mirror().addBox(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.45F, -0.581F, 1.55F, 0.0F, 0.0F, -0.3927F));
        segment1.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 31).mirror().addBox(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.45F, 1.419F, 1.55F, 0.0F, 0.0F, -0.3927F));
        segment1.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(0, 31).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.45F, 1.419F, 1.55F, 0.0F, 0.0F, 0.3927F));
        segment1.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 31).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.45F, -0.581F, 1.55F, 0.0F, 0.0F, 0.3927F));

        root.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(6, 27).addBox(-2.5F, -2.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 13).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 0).addBox(1.5F, -2.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 19).addBox(-1.5F, -1.0F, 0.275F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(22, 8).addBox(-0.5F, -1.25F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(22, 9).addBox(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 8).addBox(-1.75F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 9).addBox(-0.5F, 1.25F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(30, 6).addBox(-1.5F, 2.0F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -3.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(StellarisMobRenderState state) {
        this.resetAllPoses();

        this.idleAnimation.apply(state.lunarParasiteIdleAnimationState, state.ageInTicks, 1.0F);
        this.moveAnimation.apply(state.lunarParasiteMoveAnimationState, state.ageInTicks, 1.0F);
        this.infectAnimation.apply(state.lunarParasiteInfectAnimationState, state.ageInTicks, 1.0F);
        this.attachedAnimation.apply(state.lunarParasiteAttachedAnimationState, state.ageInTicks, 1.0F);
    }

    private void resetAllPoses() {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }
}
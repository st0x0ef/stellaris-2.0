package org.exodusstudio.stellaris.client.renderers.pumpjack;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class PumpjackModel extends Model<PumpjackRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("pumpjack"), "main");

    private final KeyframeAnimation workingAnimation;

    public PumpjackModel(ModelPart root) {
        super(root, RenderTypes::entityCutoutCull);
        this.workingAnimation = PumpjackAnimations.WORKING.bake(root);
    }

    @Override
    public void setupAnim(PumpjackRenderState state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.workingAnimation.apply(state.animationMillis, 1.0F);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition drill = partdefinition.addOrReplaceChild("drill", CubeListBuilder.create().texOffs(60, 58).addBox(-1.5F, -12.5F, -1.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(62, 52).addBox(-1.5F, -10.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.25F))
                .texOffs(60, 50).addBox(-2.5F, -7.5F, -2.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 31.0F, -22.0F));

        drill.addOrReplaceChild("drill_arm", CubeListBuilder.create().texOffs(48, 57).addBox(0.5F, -17.5F, -2.0F, 1.0F, 19.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offset(-2.0F, -12.0F, 1.0F));

        PartDefinition arm = partdefinition.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(0, 45).addBox(-0.5F, -3.0F, -14.5F, 1.0F, 3.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -1.0F, -5.5F));

        arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 45).addBox(1.5F, -2.0F, -12.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -2.0F, -4.5F, -0.3927F, 0.0F, 0.0F));

        PartDefinition handle = arm.addOrReplaceChild("handle", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 18.5F));

        handle.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(23, 22).addBox(-1.0F, -0.5F, -1.8F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(25, 0).addBox(-7.0F, -0.5F, -1.8F, 7.0F, 2.0F, 2.0F, new CubeDeformation(-0.5F))
                .texOffs(23, 18).addBox(-9.0F, -0.5F, -1.8F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.0F, 1.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition engine = partdefinition.addOrReplaceChild("engine", CubeListBuilder.create().texOffs(30, 61).addBox(-1.0F, -15.0F, -1.0F, 1.0F, 16.0F, 2.0F, new CubeDeformation(-0.25F))
                .texOffs(24, 61).addBox(7.0F, -15.0F, -1.0F, 1.0F, 16.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-5.0F, 18.0F, 15.0F, 0.0873F, 0.0F, 0.0F));

        engine.addOrReplaceChild("supporters", CubeListBuilder.create().texOffs(54, 55).addBox(0.0F, -14.0F, -1.0F, 1.0F, 14.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(42, 57).addBox(8.0F, -14.0F, -1.0F, 1.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -6.0F, 0.0F));

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -1.0F, -12.0F, 16.0F, 1.0F, 44.0F, new CubeDeformation(0.0F))
                .texOffs(42, 45).addBox(-1.0F, -11.0F, -11.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(36, 4).addBox(-3.0F, -9.0F, -9.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(18, 31).addBox(-1.0F, -11.0F, 21.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -9.0F, 23.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

        main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 29).addBox(-9.0F, -2.0F, 0.0F, 10.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -24.0F, 1.0F, -0.7854F, 0.0F, 0.0F));

        main.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(34, 16).addBox(-2.0F, -24.5F, -1.0F, 2.0F, 25.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 45).addBox(-8.0F, -24.5F, -1.0F, 2.0F, 25.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -1.0F, -7.0F, -0.3927F, 0.0F, 0.0F));

        main.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(8, 45).addBox(-2.0F, -24.5F, -1.0F, 2.0F, 25.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 45).addBox(4.0F, -24.5F, -1.0F, 2.0F, 25.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -1.0F, 12.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition engine_part = main.addOrReplaceChild("engine_part", CubeListBuilder.create().texOffs(76, 16).addBox(-7.0F, 14.0F, 17.0F, 7.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -24.0F, -1.0F));

        engine_part.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(76, 0).addBox(-8.0F, -5.0F, 26.0F, 9.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}

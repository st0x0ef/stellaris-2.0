package org.exodusstudio.stellaris.client.renderer.blocks.flag;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;


public class FlagHeadModel extends SkullModelBase {
    private final ModelPart root;
    protected final ModelPart head;

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocationUtils.id("flag_head"), "main");

    public FlagHeadModel(ModelPart modelPart) {
        super(modelPart);
        this.root = modelPart;
        this.head = modelPart.getChild("head");
    }

    public static MeshDefinition createHeadModel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(8, 8).addBox(-3.0F, -11.0F, 3.980F, 8.0F, 8.0F, 0.020F).texOffs(0, 8).addBox(-3.0F, -11.0F, 4.002F, 8.0F, 8.0F, 0.020F), PartPose.ZERO);
        return meshdefinition;
    }

    public static LayerDefinition createHumanoidHeadLayer() {
        MeshDefinition meshdefinition = createHeadModel();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.getChild("head").addOrReplaceChild("hat", CubeListBuilder.create().texOffs(40, 8).addBox(-3.0F, -11.0F, 3.970F, 8.0F, 8.0F, 0.020F).texOffs(32, 8).addBox(-3.0F, -11.0F, 4.020F, 8.0F, 8.0F, 0.020F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createMobHeadLayer() {
        MeshDefinition meshdefinition = createHeadModel();
        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    @Override
    public void setupAnim(float mouthAnimation, float yRot, float xRot) {
        this.head.yRot = yRot * ((float) Math.PI / 180F);
        this.head.xRot = xRot * ((float) Math.PI / 180F);

    }

}
package org.exodusstudio.stellaris.client.renderers.blender;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.function.Function;

public class BlenderBlockModel extends Model<BlenderBlockRenderer.BlenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("blender"), "main");

    private final ModelPart main;
    private final ModelPart blade;

    public BlenderBlockModel(ModelPart root) {
        this(root, RenderTypes::entityCutoutCull);
    }

    private BlenderBlockModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);

        this.main = root.getChild("main");
        this.blade = this.main.getChild("blade");
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -16.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(8, 32).addBox(-15.0F, -27.0F, 1.0F, 14.0F, 12.0F, 14.0F, new CubeDeformation(-1.0F))
                .texOffs(6, 34).addBox(-2.0F, -26.0F, 7.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(14, 34).mirror().addBox(-16.0F, -26.0F, 7.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(64, 0).addBox(-16.0F, -28.0F, 0.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

        PartDefinition blade = main.addOrReplaceChild("blade", CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -10.0F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -16.0F, 8.0F));

        PartDefinition cube_r1 = blade.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(-8, 3).addBox(-4.0F, 28.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -29.0F, 0.0F, 0.0F, -2.0071F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
    @Override
    public void setupAnim(BlenderBlockRenderer.BlenderState state) {
        blade.yRot = state.bladeRotation / 100f;
    }



}

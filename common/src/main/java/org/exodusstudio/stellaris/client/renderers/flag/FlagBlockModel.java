package org.exodusstudio.stellaris.client.renderers.flag;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.function.Function;

public class FlagBlockModel extends Model<BlockEntityRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("flag"), "main");

    private final ModelPart flag;
    private final ModelPart pole;
    private final ModelPart base;

    public FlagBlockModel(ModelPart root) {
        this(root, RenderTypes::entityCutoutCull);
    }

    private FlagBlockModel(ModelPart root, Function<Identifier, RenderType> function) {
        super(root, function);

        this.flag = root.getChild("flag");
        this.pole = root.getChild("pole");
        this.base = root.getChild("base");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 35).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 4).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(40, 16).addBox(-2.0F, -11.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition pole = partdefinition.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(10, 0).addBox(-22.0F, -46.0F, -1.0F, 23.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 35).addBox(-2.0F, -48.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 22).addBox(-2.0F, -35.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 4).addBox(-1.0F, -45.0F, -1.0F, 2.0F, 44.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition flag = partdefinition.addOrReplaceChild("flag", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition flag_content_r1 = flag.addOrReplaceChild("flag_content   ", CubeListBuilder.create().texOffs(43, 36).addBox(0.0F, -20.0F, -1.0F, 10.0F, 20.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(5, 4).addBox(0.0F, -20.0F, -0.5F, 10.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-21.0F, -44.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}

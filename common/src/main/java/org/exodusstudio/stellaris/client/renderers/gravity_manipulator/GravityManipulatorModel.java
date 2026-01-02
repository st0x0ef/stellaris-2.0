package org.exodusstudio.stellaris.client.renderers.gravity_manipulator;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.function.Function;

public class GravityManipulatorModel extends Model<BlockEntityRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("gravity_manipulator"), "main");
    private final ModelPart GravityCenter;

    public GravityManipulatorModel(ModelPart root) {
        this(root, RenderTypes::entityCutout);
    }

    private GravityManipulatorModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);

        this.GravityCenter = root.getChild("GravityCenter");
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition GravityCenter = partdefinition.addOrReplaceChild("GravityCenter", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition Core_r1 = GravityCenter.addOrReplaceChild("Core_r1", CubeListBuilder.create().texOffs(86, 35).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.6981F, 0.0F));

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(48, 5).addBox(5.0F, -28.0F, -8.0F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(64, 40).addBox(5.0F, -25.0F, -8.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(64, 40).addBox(-8.0F, -25.0F, -8.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(64, 40).addBox(-8.0F, -25.0F, 5.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(64, 40).addBox(5.0F, -25.0F, 5.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(74, 57).addBox(-1.0F, -22.0F, -1.0F, 2.0F, 20.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 30).mirror().addBox(-5.0F, -28.0F, -8.0F, 10.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(48, 30).addBox(-5.0F, -28.0F, 5.0F, 10.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(87, 5).addBox(-8.0F, -28.0F, -8.0F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-8.0F, -13.0F, -8.0F, 16.0F, 13.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public void animateItemCore(float partialTick) {
        GravityCenter.yRot += partialTick;
    }

    public void animateBlockCore(float partialTick, double gravity) {
        float amplitude = (float) (gravity / Stellaris.CONFIG.gravityConfig.maxGravityManipulatorValue);

        GravityCenter.yRot += partialTick * amplitude / 10f;

        float time = (System.currentTimeMillis() % 2000L) / 1000f * (float) Math.PI * amplitude;
        GravityCenter.y = 5.0F + (float) Math.sin(time) * amplitude;
    }
}

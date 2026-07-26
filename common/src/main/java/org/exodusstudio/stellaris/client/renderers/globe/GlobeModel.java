package org.exodusstudio.stellaris.client.renderers.globe;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class GlobeModel extends Model<BlockEntityRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("globe"), "main");

    /** Model-space Y of the planet pivot (in pixels; divide by 16 for block units). */
    public static final float PLANET_PIVOT_Y = 14.0F;

    private final ModelPart stand;
    private final ModelPart planet;

    public GlobeModel(ModelPart root) {
        super(root, RenderTypes::entityCutoutCull);
        this.stand = root.getChild("stand");
        this.planet = root.getChild("planet");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Static holder. The spinning planet is a separate root child so its rotation can be
        // carried by the PoseStack (snapshotted per submit) instead of a shared ModelPart field.
        partdefinition.addOrReplaceChild("stand", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-7.0F, -16.0F, 1.0F, 8.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(-4.0F, -1.0F, -2.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 35).addBox(-3.0F, -5.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -4.0F, 0.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.0F, 24.0F, -1.0F));

        // Sphere centered on its own pivot (0, 14, 0) so a Y rotation spins it in place.
        partdefinition.addOrReplaceChild("planet", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, PLANET_PIVOT_Y, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public ModelPart stand() {
        return this.stand;
    }

    public ModelPart planet() {
        return this.planet;
    }
}

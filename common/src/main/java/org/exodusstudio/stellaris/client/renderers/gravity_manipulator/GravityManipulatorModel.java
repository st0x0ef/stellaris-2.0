package org.exodusstudio.stellaris.client.renderers.gravity_manipulator;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.function.Function;

public class GravityManipulatorModel extends Model<GravityManipulatorRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("gravity_manipulator"), "main");

    public static final float CORE_BASE_Y = 5.0F;

    private static final double CORE_SPIN_PER_TICK = 0.005;
    private static final double CORE_BOB_PER_TICK = Math.PI / 20.0;
    private static final double TAU = Math.PI * 2.0;

    private final ModelPart GravityCenter;

    public GravityManipulatorModel(ModelPart root) {
        this(root, RenderTypes::entityCutoutCull);
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

    @Override
    public void setupAnim(GravityManipulatorRenderState state) {
        GravityCenter.yRot = state.coreYRot;
        GravityCenter.y = state.coreY;
    }

    public void animateItemCore(float partialTick) {
        GravityCenter.yRot += partialTick;
    }

    public static void animateCore(GravityManipulatorRenderState state, double ticks, double gravity) {
        double amplitude = gravity / Stellaris.CONFIG.gravityConfig.maxGravityManipulatorValue;

        state.coreYRot = (float) ((ticks * amplitude * CORE_SPIN_PER_TICK) % TAU);
        state.coreY = CORE_BASE_Y + (float) (Math.sin(ticks * amplitude * CORE_BOB_PER_TICK) * amplitude);
    }

    public static void restCore(GravityManipulatorRenderState state) {
        state.coreYRot = 0.0F;
        state.coreY = CORE_BASE_Y;
    }
}

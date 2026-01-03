package org.exodusstudio.stellaris.client.renderers.rockets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public class RocketRenderer extends EntityRenderer<RocketEntity, RocketRenderState> {
    private final RocketModel model;
    public static final RenderType RENDER_TYPE;
    public static final Identifier ROCKET_TEXTURE =  IdentifierUtils.texture("entity/rocket/default");

    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new RocketModel(context.bakeLayer(RocketModel.LAYER_LOCATION));

    }

    @Override
    public void extractRenderState(RocketEntity entity, RocketRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.modules = entity.getEntityData().get(RocketEntity.ROCKET_MODULES).getModules();
    }

    @Override
    public @NotNull RocketRenderState createRenderState() {
        return new RocketRenderState();
    }

    @Override
    public void submit(RocketRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);

        poseStack.pushPose();
        poseStack.translate(0.0D, -0.3D, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(0.8f, 0.8f, 0.8f);


        this.model.setDefaultModel();

        RenderingContext renderingContext = new RenderingContext(poseStack, renderState.lightCoords, this.model, ROCKET_TEXTURE);

        renderState.preRenderModules(renderingContext);

        RenderType renderType = renderState.getRenderType(renderingContext);

        nodeCollector.submitModelPart(this.model.root(), poseStack, renderType, renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);

        renderState.renderModules(renderingContext);

        poseStack.popPose();
    }

    @Override
    protected AABB getBoundingBoxForCulling(RocketEntity minecraft) {
        return minecraft.getBoundingBox().inflate(0.5f);
    }

    public static RenderType getRenderType(Identifier Identifier) {
        return RenderTypes.entityCutoutNoCull(Identifier);
    }

    static {
        RENDER_TYPE = getRenderType(ROCKET_TEXTURE);
    }

    public static class RenderingContext {
        public final PoseStack poseStack;
        public final int packedLight;
        public final RocketModel model;
        public Identifier texture;

        public RenderingContext(PoseStack poseStack, int packedLight, RocketModel model, Identifier texture) {
            this.poseStack = poseStack;
            this.packedLight = packedLight;
            this.model = model;
            this.texture = texture;
        }

    }
}

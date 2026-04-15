package org.exodusstudio.stellaris.client.renderers.lander;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.entities.LanderEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public class LanderRenderer extends EntityRenderer<LanderEntity, EntityRenderState> {
    private final LanderModel model;
    public static final RenderType RENDER_TYPE;
    public static final Identifier LANDER_TEXTURE =  IdentifierUtils.texture("entity/lander");

    public LanderRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new LanderModel(context.bakeLayer(LanderModel.LAYER_LOCATION));

    }

    @Override
    public @NotNull EntityRenderState createRenderState() {
        return new EntityRenderState();
    }


    @Override
    public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(1f, 1f, 1f);

        nodeCollector.submitModelPart(this.model.root(), poseStack, RENDER_TYPE, renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);


        poseStack.popPose();
    }

    @Override
    protected AABB getBoundingBoxForCulling(LanderEntity minecraft) {
        return minecraft.getBoundingBox().inflate(0.5f);
    }

    public static RenderType getRenderType(Identifier Identifier) {
        return RenderTypes.entityCutoutNoCull(Identifier);
    }



    static {
        RENDER_TYPE = getRenderType(LANDER_TEXTURE);
    }

}

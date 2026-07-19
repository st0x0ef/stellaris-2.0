package org.exodusstudio.stellaris.client.renderers.entity.vehicle.rover;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public class RoverRenderer extends EntityRenderer<RoverEntity, RoverRenderState> {

    public static final Identifier TEXTURE = IdentifierUtils.texture("entity/vehicle/rover");
    public static final RenderType RENDER_TYPE = RenderTypes.entityCutout(TEXTURE);
    private final RoverModel model;

    public RoverRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new RoverModel(renderManagerIn.bakeLayer(RoverModel.LAYER_LOCATION));
    }

    @Override
    public @NotNull RoverRenderState createRenderState() {
        return new RoverRenderState();
    }

    @Override
    public void extractRenderState(RoverEntity entity, RoverRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isForward = entity.isForward();
        state.isBackward = entity.isBackward();
        state.xRot = entity.getXRot();
        state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.deltaMovement = entity.getDeltaMovement();
        state.direction = entity.getDirection();
        state.ageInTicks = entity.tickCount + partialTick;
    }

    @Override
    public void submit(RoverRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - renderState.yRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        this.model.setupAnim(renderState);

        nodeCollector.submitModelPart(this.model.root(), poseStack, RENDER_TYPE, renderState.lightCoords,
                OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }

    @Override
    protected AABB getBoundingBoxForCulling(RoverEntity entity) {
        return entity.getBoundingBox().inflate(4);
    }
}

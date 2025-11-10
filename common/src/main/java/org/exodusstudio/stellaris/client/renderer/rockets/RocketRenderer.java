package org.exodusstudio.stellaris.client.renderer.rockets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EndCrystalModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.client.models.rockets.RocketModel;
import org.exodusstudio.stellaris.client.models.rockets.RocketModelState;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.registries.EntityDataSerializersRegistry;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class RocketRenderer extends EntityRenderer<RocketEntity, RocketModelState> {
    private RocketModel model;
    public static final RenderType RENDER_TYPE;
    public static final ResourceLocation ROCKET_TEXTURE =  ResourceLocationUtils.texture("entity/rocket/default");

    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new RocketModel(context.bakeLayer(RocketModel.LAYER_LOCATION));

    }

    @Override
    public void extractRenderState(RocketEntity entity, RocketModelState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.modules = entity.getEntityData().get(RocketEntity.ROCKET_MODULES).getModules();
    }

    @Override
    public RocketModelState createRenderState() {
        return new RocketModelState();
    }

    @Override
    public void render(RocketModelState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(renderState, poseStack, bufferSource, packedLight);

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RENDER_TYPE);

        this.model.setDefaultModel();

        renderState.preRenderModules(poseStack, bufferSource, packedLight, this.model, vertexConsumer);

        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        renderState.renderModules(poseStack, bufferSource, packedLight, this.model, vertexConsumer);

        poseStack.popPose();
    }

    @Override
    protected AABB getBoundingBoxForCulling(RocketEntity minecraft) {
        return minecraft.getBoundingBox().inflate(0.5f);
    }

    static {
        RENDER_TYPE = RenderType.entityCutoutNoCull(ROCKET_TEXTURE);
    }
}

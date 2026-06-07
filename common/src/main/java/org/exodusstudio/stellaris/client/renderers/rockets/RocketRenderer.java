package org.exodusstudio.stellaris.client.renderers.rockets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.client.renderers.rockets.models.RocketModel;
import org.exodusstudio.stellaris.client.renderers.rockets.models.RocketModelRegistry;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.items.modules.rocket.RocketModelModuleItem;
import org.exodusstudio.stellaris.common.items.modules.rocket.RocketSkinModuleItem;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class RocketRenderer extends EntityRenderer<RocketEntity, RocketRenderState> {
    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(RocketEntity entity, RocketRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.modules = entity.getEntityData().get(RocketEntity.ROCKET_MODULES).getModules();
        reusedState.rocketStart = entity.getEntityData().get(RocketEntity.ROCKET_START);
    }

    @Override
    public @NotNull RocketRenderState createRenderState() {
        return new RocketRenderState();
    }

    public boolean isShaking(RocketRenderState renderState) {
        return renderState.rocketStart;
    }

    @Override
    public void submit(RocketRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);

        poseStack.pushPose();
        poseStack.translate(0.0D, -0.3D, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(0.8f, 0.8f, 0.8f);

        if (this.isShaking(renderState)) {
            if (!Minecraft.getInstance().isPaused()) {
                Random random = new Random();
                double shakeDirection1 = (random.nextDouble() * (random.nextBoolean() ? 1 : -1)) / 50;
                double shakeDirection2 = (random.nextDouble() * (random.nextBoolean() ? 1 : -1)) / 50;
                double shakeDirection3 = (random.nextDouble() * (random.nextBoolean() ? 1 : -1)) / 50;
                poseStack.translate(shakeDirection1, shakeDirection2, shakeDirection3);
            }
        }

        boolean rocketModelPresent = false;
        for (RocketModule module : renderState.modules) {
            if (module.getRocketFeature() == RocketModule.RocketFeature.MODEL) {
                rocketModelPresent = true;
            }
        }

        RenderingContext renderingContext = new RenderingContext(renderState.modules, poseStack, renderState.lightCoords);
        RenderType renderType = getRenderType(renderingContext);

        if  (!rocketModelPresent) {
            RocketModel defaultModel = RocketModelRegistry.create("tiny", Minecraft.getInstance().getEntityModels());
            renderingContext.setRocketModel(defaultModel);
            nodeCollector.submitModelPart(defaultModel.root(), poseStack, renderType, renderingContext.packedLight, OverlayTexture.NO_OVERLAY, null);
        }

        renderState.preRenderModules(nodeCollector, poseStack, renderingContext, renderType);
        renderState.renderModules(renderingContext);

        poseStack.popPose();
    }


    @Override
    protected AABB getBoundingBoxForCulling(RocketEntity minecraft) {
        return minecraft.getBoundingBox().inflate(0.5f);
    }

    public static RenderType getRenderType(RenderingContext context) {
        String model = "tiny";
        String skin = "default";

        for (RocketModule module : context.rocketModules) {
            if (module instanceof RocketModelModuleItem<?> rocketModelModuleItem) {
                model = rocketModelModuleItem.getModelName();
            } else if (module instanceof RocketSkinModuleItem rocketSkinModuleItem) {
                skin = rocketSkinModuleItem.getSkinName();
            }
        }

        Identifier texture = IdentifierUtils.texture("entity/rocket/" + model + "/" + skin);
        return RenderTypes.entityCutout(texture);
    }

    public static class RenderingContext {
        public final List<RocketModule> rocketModules;
        public final PoseStack poseStack;
        public final int packedLight;
        public RocketModel rocketModel;

        public RenderingContext(List<RocketModule> rocketModules, PoseStack poseStack, int packedLight) {
            this.rocketModules = rocketModules;
            this.poseStack = poseStack;
            this.packedLight = packedLight;
        }

        public void setRocketModel(RocketModel rocketModel) {
            this.rocketModel = rocketModel;
        }
    }
}

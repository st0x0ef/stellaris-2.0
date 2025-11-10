package org.exodusstudio.stellaris.common.items.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.client.models.rockets.RocketModel;
import org.exodusstudio.stellaris.client.models.rockets.RocketModelState;

public abstract class RocketModule extends Item {

    public RocketModule(Properties properties) {
        super(properties);
    }


    /**
     * This method is fired before rendering the rocket model.
     * @param renderState the current rocket model state
     * @param poseStack t
     * @param bufferSource
     * @param packedLight
     * @param model used for stellaris own module that are directly into the rocket model
     */
    public void preRenderModel(RocketModelState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, RocketModel model, VertexConsumer vertexConsumer) {

    }

    /**
     * Render this module on the rocket.
     * @param renderState the current rocket model state
     * @param poseStack t
     * @param bufferSource
     * @param packedLight
     * @param model used for stellaris own module that are directly into the rocket model
     */
    public void renderModule(RocketModelState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, RocketModel model) {

    }
}

package org.exodusstudio.stellaris.common.items;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.client.models.rockets.RocketModel;
import org.exodusstudio.stellaris.client.models.rockets.RocketModelState;

public abstract class RocketModule extends Item {

    public RocketModule(Properties properties) {
        super(properties);
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

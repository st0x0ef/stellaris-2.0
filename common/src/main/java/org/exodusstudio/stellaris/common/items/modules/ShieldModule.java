package org.exodusstudio.stellaris.common.items.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.exodusstudio.stellaris.client.models.rockets.RocketModel;
import org.exodusstudio.stellaris.client.models.rockets.RocketModelState;

public class ShieldModule extends RocketModule {

    public ShieldModule(Properties properties) {
        super(properties);
    }

    @Override
    public void preRenderModel(RocketModelState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, RocketModel model, VertexConsumer vertexConsumer) {
        model.shield1.visible = true;
        model.shield2.visible = true;
    }

}

package org.exodusstudio.stellaris.client.models.rockets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.exodusstudio.stellaris.common.items.modules.RocketModule;

import java.util.List;

public class RocketModelState extends EntityRenderState {

    public List<RocketModule> modules;

    public void preRenderModules(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, RocketModel model, VertexConsumer vertexConsumer) {
        if (modules != null) {
            for (RocketModule module : modules) {
                if(module != null) module.preRenderModel(this, poseStack, bufferSource, packedLight, model, vertexConsumer);
            }
        }
    }

    public void renderModules(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, RocketModel model, VertexConsumer vertexConsumer) {
        if (modules != null) {
            for (RocketModule module : modules) {
                if(module != null) module.renderModule(this, poseStack, bufferSource, packedLight, model);
            }
        }
    }
}

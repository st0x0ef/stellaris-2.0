package org.exodusstudio.stellaris.client.renderers.rockets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;

import java.util.List;

public class RocketRenderState extends EntityRenderState {

    public boolean rocketStart;
    public float bodyRotation;
    public List<RocketModule> modules;

    public void preRenderModules(SubmitNodeCollector nodeCollector, PoseStack poseStack, RocketRenderer.RenderingContext context, RenderType renderType) {
        if (modules != null) {
            for (RocketModule module : modules) {
                if (module != null) module.preRenderModel(nodeCollector, poseStack, context, renderType);
            }
        }
    }

    public void renderModules(RocketRenderer.RenderingContext context) {
        if (modules != null) {
            for (RocketModule module : modules) {
                if(module != null) module.renderModule(context);
            }
        }
    }

    public static RocketRenderState create(List<RocketModule> modules) {
        RocketRenderState state = new RocketRenderState();
        state.modules = modules;
        return state;
    }
}

package org.exodusstudio.stellaris.client.models.rockets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.exodusstudio.stellaris.client.renderer.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.items.modules.RocketModule;

import java.util.List;

public class RocketModelState extends EntityRenderState {

    public List<RocketModule> modules;

    public void preRenderModules(RocketRenderer.RenderingContext context) {
        if (modules != null) {
            for (RocketModule module : modules) {
                if(module != null) module.preRenderModel(context);
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

    public RenderType getRenderType(RocketRenderer.RenderingContext context) {
        var type = RocketRenderer.RENDER_TYPE;
        if (modules != null) {
            for (RocketModule module : modules) {
                if(module != null && module.getRenderType(context) != null) {
                    type = module.getRenderType(context);
                }
            }
        }
        return type;
    }
}

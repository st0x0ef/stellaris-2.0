package org.exodusstudio.stellaris.client.renderers.rockets;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;

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

    public static RocketModelState create(List<RocketModule> modules) {
        RocketModelState state = new RocketModelState();
        state.modules = modules;
        return state;
    }
}

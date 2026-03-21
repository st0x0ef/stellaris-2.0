package org.exodusstudio.stellaris.client.renderers.rockets;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;

import java.util.List;

public class RocketRenderState extends EntityRenderState {

    public boolean rocketStart;
    public float bodyRotation;
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

    public static RocketRenderState create(List<RocketModule> modules) {
        RocketRenderState state = new RocketRenderState();
        state.modules = modules;
        return state;
    }
}

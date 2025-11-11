package org.exodusstudio.stellaris.common.items.modules;

import org.exodusstudio.stellaris.client.renderer.rockets.RocketRenderer;

public class ShieldModule extends RocketModule {

    public ShieldModule(Properties properties) {
        super(properties);
    }

    @Override
    public void preRenderModel(RocketRenderer.RenderingContext renderContext) {
        renderContext.model.shield1.visible = true;
        renderContext.model.shield2.visible = true;
    }

}

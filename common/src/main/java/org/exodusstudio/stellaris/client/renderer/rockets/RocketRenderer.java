package org.exodusstudio.stellaris.client.renderer.rockets;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.exodusstudio.stellaris.client.models.rockets.RocketModelState;
import org.exodusstudio.stellaris.common.entities.RocketEntity;

public class RocketRenderer extends EntityRenderer<RocketEntity, RocketModelState> {


    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public RocketModelState createRenderState() {
        return new RocketModelState();
    }
}

package org.exodusstudio.stellaris.client.renderer.rockets;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.exodusstudio.stellaris.client.models.rockets.RocketModelState;
import org.exodusstudio.stellaris.common.entities.Rocket;

public class RocketRenderer extends EntityRenderer<Rocket, RocketModelState> {


    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public RocketModelState createRenderState() {
        return new RocketModelState();
    }
}

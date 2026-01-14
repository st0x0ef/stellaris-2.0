package org.exodusstudio.stellaris.common.items.modules.rocket;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;

public class ShieldModule extends Item implements RocketModule {

    public ShieldModule(Properties properties) {
        super(properties);
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.OTHER;
    }

    @Override
    public void preRenderModel(RocketRenderer.RenderingContext renderContext) {
        renderContext.model.shield1.visible = true;
        renderContext.model.shield2.visible = true;
    }

    @Override
    public MutableComponent displayName() {
        return Component.literal("Shield Module");
    }
}

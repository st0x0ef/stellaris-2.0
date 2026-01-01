package org.exodusstudio.stellaris.common.items.modules;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class GalaxySkinModule extends Item implements RocketModule {

    public static final ResourceLocation SKIN_TEXTURE = ResourceLocationUtils.texture("entity/rocket/galaxy");

    public GalaxySkinModule(Properties properties) {
        super(properties);
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.SKIN;
    }

    @Override
    public RenderType getRenderType(RocketRenderer.RenderingContext context) {
        return RocketRenderer.getRenderType(SKIN_TEXTURE);
    }

    @Override
    public MutableComponent displayName() {
        return Component.literal("Galaxy Skin Module");
    }
}

package org.exodusstudio.stellaris.common.items.modules;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.client.renderer.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class GalaxySkinModule extends RocketModule {

    public static final ResourceLocation SKIN_TEXTURE =  ResourceLocationUtils.texture("entity/rocket/galaxy");


    public GalaxySkinModule(Properties properties) {
        super(properties);
    }

    @Override
    public RenderType getRenderType(RocketRenderer.RenderingContext context) {
        return RocketRenderer.getRenderType(SKIN_TEXTURE);
    }
}

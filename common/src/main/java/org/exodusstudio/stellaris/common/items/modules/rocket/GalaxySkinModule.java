package org.exodusstudio.stellaris.common.items.modules.rocket;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class GalaxySkinModule extends Item implements RocketModule {

    public static final Identifier SKIN_TEXTURE = IdentifierUtils.texture("entity/rocket/galaxy");

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
}

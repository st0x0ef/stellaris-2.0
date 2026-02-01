package org.exodusstudio.stellaris.common.items.modules.rocket;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;

public class RocketSkinModuleItem extends Item implements RocketModule {

    private final Identifier SKIN_TEXTURE;

    public RocketSkinModuleItem(Properties properties, MutableComponent displayName, final Identifier skinTexture) {
        super(properties);
        this.SKIN_TEXTURE = skinTexture;
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

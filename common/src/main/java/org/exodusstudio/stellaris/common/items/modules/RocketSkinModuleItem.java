package org.exodusstudio.stellaris.common.items.modules;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;

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

    @Override
    public MutableComponent displayName() {
        return Component.literal(SKIN_TEXTURE.getPath()); //TODO remove this
    }

}

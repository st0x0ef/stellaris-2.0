package org.exodusstudio.stellaris.common.items.modules;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.client.renderer.rockets.RocketRenderer;
import org.jetbrains.annotations.Nullable;

public abstract class RocketModule extends Item {

    public RocketModule(Properties properties) {
        super(properties);
    }


    /**
     * This method is fired before rendering the rocket model.
     * @param context the current rendering context
     */
    public void preRenderModel(RocketRenderer.RenderingContext context) {

    }

    /**
     * Render this module on the rocket.
     * @param context the current rendering context
     */
    public void renderModule(RocketRenderer.RenderingContext context) {

    }

    /**
     * Get the render type for this module.
     * Allow you to change the texture used to render the rocket.
     * If you don't change the texture/render type, set it to null to allow other modules to change it.
     * @param context the current rendering context
     * @return the render type, or null to use the default one
     */
    @Nullable
    public RenderType getRenderType(RocketRenderer.RenderingContext context) {
        return null;
    }
}

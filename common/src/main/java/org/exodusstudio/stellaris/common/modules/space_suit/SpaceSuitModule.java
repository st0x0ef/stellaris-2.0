package org.exodusstudio.stellaris.common.modules.space_suit;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.modules.Module;
import org.jetbrains.annotations.Nullable;

public interface SpaceSuitModule extends Module<SpaceSuitModule> {

    /**
     * Checks if this module is compatible with other modules that are currently in the space suit.
     * @param module the module to check with
     * @return if this module can be fit with the other module
     * */
    default boolean isCompatibleWith(SpaceSuitModule module) {
        return true;
    }


    /**
     * This method is fired before rendering the space suit model.
     * @param context the current rendering context
     */
    
    default void preRenderModel(RocketRenderer.RenderingContext context) {}

    /**
     * Render this module on the space suit.
     * @param context the current rendering context
     */
    
    default void renderModule(RocketRenderer.RenderingContext context) {}

    /**
     * Get the render type for this module.
     * Allows you to change the texture used to render the space suit.
     * If you don't change the texture/render type, set it to null to allow other modules to change it.
     * @param context the current rendering context
     * @return the render type, or null to use the default one
     */
    @Nullable
    default RenderType getRenderType(RocketRenderer.RenderingContext context) {
        return null;
    }

    interface CustomFuelModule extends SpaceSuitModule {

        /**
         * Change the fuel of the rocket with this module.
         * @return The fluid stack representing the fuel.
         */
        Fluid getFuel();
        int getCapacity();
    }

    interface OxygenModule extends SpaceSuitModule {

        /**
         * Change the oxygen capacity of the space suit with this module.
         * @return The capacity of the space suit.
         */
        int getCapacity();
    }

    interface OilFinderModule extends SpaceSuitModule {

        /**
         * Change the oil finding capability of the space suit with this module.
         * @return The range of the oil finder.
         */
        int getRange();
    }
}

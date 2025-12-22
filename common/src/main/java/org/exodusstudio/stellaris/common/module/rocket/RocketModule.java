package org.exodusstudio.stellaris.common.module.rocket;

import dev.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.exodusstudio.stellaris.client.renderer.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.module.Module;
import org.jetbrains.annotations.Nullable;

public interface RocketModule extends Module<RocketModule> {

    RocketFeature getRocketFeature();

    /**
     * Checks if this module is compatible with other modules that are currently in the rocket.
     * @param module the module to check with
     * @return if this module can be fit with the other module
     * */
    default boolean isCompatibleWith(RocketModule module) {
        return true;
    }

    /**
     * @param currentTravelDistance current travelable distance with the rocket in one flight.
     * @return what to add/sub to the current distance.
     *
     * @implNote addition and substraction operations are ran before multiplication and division.
     * */
    default long addToTravelDistance(long currentTravelDistance) {
        return 0;
    }

    /**
     * @param currentTravelDistance current travelable distance with the rocket in one flight.
     * @return what to mult/div to the current distance.
     * @implNote multiplication and division operations are ran after addition and substraction.
     * */
    default double multByTravelDistance(long currentTravelDistance) {
        return 1;
    }

    /**
     * This method is fired before rendering the rocket model.
     * @param context the current rendering context
     */
    @Environment(EnvType.CLIENT)
    default void preRenderModel(RocketRenderer.RenderingContext context) {}

    /**
     * Render this module on the rocket.
     * @param context the current rendering context
     */
    @Environment(EnvType.CLIENT)
    default void renderModule(RocketRenderer.RenderingContext context) {}

    /**
     * Get the render type for this module.
     * Allows you to change the texture used to render the rocket.
     * If you don't change the texture/render type, set it to null to allow other modules to change it.
     * @param context the current rendering context
     * @return the render type, or null to use the default one
     */
    @Nullable
    @Environment(EnvType.CLIENT)
    default RenderType getRenderType(RocketRenderer.RenderingContext context) {
        return null;
    }

    // Might not be useful...
    // TODO we need to see how thing would be implemented in the future
    public enum RocketFeature {
        SKIN,
        TANK,
        MOTOR, //Fuel Type
        OTHER
    }

    interface CustomFuelModule extends RocketModule {

        /**
         * Change the fuel of the rocket with this module.
         * @return The fluid stack representing the fuel.
         */
        FluidStack getFuel();

    }

}

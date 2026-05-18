package org.exodusstudio.stellaris.common.modules.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.modules.Module;

public interface RocketModule extends Module<RocketModule> {

    RocketFeature getRocketFeature();

    String getDisplayName();

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
    
    default void preRenderModel(SubmitNodeCollector nodeCollector, PoseStack poseStack, RocketRenderer.RenderingContext context, RenderType renderType) {}

    /**
     * Render this module on the rocket.
     * @param context the current rendering context
     */
    
    default void renderModule(RocketRenderer.RenderingContext context) {}


    // Might not be useful...
    // TODO we need to see how thing would be implemented in the future
    enum RocketFeature {
        SKIN,
        MODEL,
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

    interface AutopilotModule extends RocketModule {

    }
}

package org.exodusstudio.stellaris.common.modules.space_suit;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.modules.Module;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public interface SpaceSuitModule extends Module<SpaceSuitModule> {

    /**
     * Checks if this module is compatible with other modules that are currently in the space suit.
     * @param module the module to check with
     * @return if this module can be fit with the other module
     * */
    default boolean isCompatibleWith(SpaceSuitModule module) {
        return true;
    }

    boolean canBeAppliedToSpaceSuitPart(ItemStack part);


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

    /**
     *
     * @param graphics
     * @param deltaTracker
     * @param player
     * @param stack
     * @param x the x position you use for your first render (increment by texture width/font length)
     * @param y the y position you use for your first render (increment by texture height/font length)
     * @return the y position of the last render (usually var y)
     */
    //@Environment(EnvType.CLIENT)
    default Vector2i renderStackedGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Player player, ItemStack stack, int x, int y) {
        return new Vector2i(0, 0);
    }

    default int renderPriority() {
        return 0;
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

    interface JetModule extends SpaceSuitModule {

        /**
         * Change the jetpack fuel consumption of the space suit with this module.
         *
         * @return The consumption of the jetpack.
         */
        long getConsumptionPerTick();
    }

    interface DamageProtectionModule extends SpaceSuitModule {

        /**
         * Change the armor material of the space suit with this module.
         * @return The armor material equivalent.
         */
        ArmorMaterial getArmorMaterialEquivalent();

        String getMaterialName();
    }
}

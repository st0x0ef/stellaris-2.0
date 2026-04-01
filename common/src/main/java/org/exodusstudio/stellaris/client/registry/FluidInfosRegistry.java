package org.exodusstudio.stellaris.client.registry;

import dev.architectury.fluid.FluidStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to register fluids infos.
 * Very useful for displaying gauge when we don't know which fluid it will have.
 */
public class FluidInfosRegistry {

    public static Map<Identifier, FluidInfos> FLUIDS_INFO = new HashMap<>();

    public static void register(Fluid fluid, FluidInfos fluidInfos, boolean overwrite) {
        boolean alreadyIn = FLUIDS_INFO.containsKey(fluid.arch$registryName());
        if(!alreadyIn || overwrite) {
            FLUIDS_INFO.put(fluid.arch$registryName(), fluidInfos);
        }
        Stellaris.LOG.warn("Already registered fluids infos for {}", fluid.arch$registryName());
    }

    public static void register(Fluid fluid, FluidInfos fluidInfos) {
        register(fluid.arch$registryName(), fluidInfos);
    }

    /**
     * Used to register fluids from other mod when we don't have access to the fluid registry
     * @param fluidLocation
     * @param fluidInfos
     */
    public static void register(Identifier fluidLocation, FluidInfos fluidInfos) {
        FLUIDS_INFO.put(fluidLocation, fluidInfos);
    }


    /**
     * Utility Methods to get fluids infos
     */
    public static Identifier getFluidTexture(FluidStack fluid) {
        return getFluidTexture(fluid.getFluid());
    }

    public static Identifier getFluidTexture(Fluid fluid) {

        if(FLUIDS_INFO.containsKey(fluid.arch$registryName())) {

            return FLUIDS_INFO.get(fluid.arch$registryName()).textureLocation();
        }
        return GUISprites.WATER_OVERLAY;
    }

    public static Component getFluidComponent(Fluid fluid) {
        if(FLUIDS_INFO.containsKey(fluid.arch$registryName())) {

            return FLUIDS_INFO.get(fluid.arch$registryName()).component();
        }
        return Component.literal("Empty");
    }

    public static void init() {
        register(FluidsRegistry.HYDROGEN_STILL.get(),
                new FluidInfos(GUISprites.HYDROGEN_OVERLAY, Component.translatable("fluid.stellaris.hydrogen" )));
        register(FluidsRegistry.FUEL_STILL.get(),
                new FluidInfos(GUISprites.WATER_OVERLAY, Component.translatable("fluid.stellaris.fuel" )));
        register(FluidsRegistry.OXYGEN_STILL.get(),
                new FluidInfos(GUISprites.OXYGEN_OVERLAY, Component.translatable("fluid.stellaris.oxygen" )));
        register(FluidsRegistry.OIL_STILL.get(),
                new FluidInfos(GUISprites.OIL_OVERLAY, Component.translatable("fluid.stellaris.oil" )));

                new FluidInfos(GUISprites.OXYGEN_OVERLAY,  Component.translatable("fluid.stellaris.oxygen" )));
        register(FluidsRegistry.BLUE_LIQUID_STILL.get(),
                new FluidInfos(GUISprites.WATER_OVERLAY,  Component.translatable("fluid.stellaris.blue_liquid" )));

        register(Fluids.WATER,
                new FluidInfos(GUISprites.WATER_OVERLAY,  Component.translatable("fluid.stellaris.water" )));
        register(Fluids.EMPTY,
                new FluidInfos(GUISprites.WATER_OVERLAY,  Component.literal("Empty")));
    }

    public record FluidInfos(Identifier textureLocation, Component component) {

    }
}

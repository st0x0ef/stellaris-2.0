package org.exodusstudio.stellaris.client.registry;

import dev.architectury.fluid.FluidStack;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;

import java.util.HashMap;
import java.util.Map;

public class FluidInfosRegistry {

    public static Map<ResourceLocation, FluidInfos> FLUIDS_INFO = new HashMap<ResourceLocation, FluidInfos>();

    public static void register(Fluid fluid, FluidInfos fluidInfos, boolean overwrite) {
        boolean alreadyIn = FLUIDS_INFO.containsKey(fluid.arch$registryName());
        if(!alreadyIn || overwrite) {
            FLUIDS_INFO.put(fluid.arch$registryName(), fluidInfos);
        }
        Stellaris.LOG.warn("Already registered fluids infos for {}", fluid.arch$registryName());
    }

    public static void register(Fluid fluid, FluidInfos fluidInfos) {
        FLUIDS_INFO.put(fluid.arch$registryName(), fluidInfos);
        Stellaris.LOG.error("Registered infos for {}", fluid.arch$registryName());

    }


    /**
     * Utility Methods to get fluids infos
     */

    public static ResourceLocation getFluidTexture(FluidStack fluid) {
        return getFluidTexture(fluid.getFluid());
    }

    public static ResourceLocation getFluidTexture(Fluid fluid) {

        if(FLUIDS_INFO.containsKey(fluid.arch$registryName())) {

            return FLUIDS_INFO.get(fluid.arch$registryName()).textureLocation();
        }
        return GUISprites.WATER_OVERLAY;
    }

    public static Component getFluidComponent(Fluid fluid) {
        if(FLUIDS_INFO.containsKey(fluid.arch$registryName())) {

            return FLUIDS_INFO.get(fluid.arch$registryName()).component();
        }
        return Component.literal("Null Fluid");
    }

    public static void init() {

        register(FluidsRegistry.HYDROGEN_STILL.get(),
                new FluidInfos(GUISprites.HYDROGEN_OVERLAY,  Component.literal("fluid.stellaris.hydrogen" )));

        register(FluidsRegistry.OXYGEN_STILL.get(),
                new FluidInfos(GUISprites.OXYGEN_OVERLAY,  Component.literal("fluid.stellaris.oxygen" )));

        register(Fluids.WATER,
                new FluidInfos(GUISprites.WATER_OVERLAY,  Component.literal("fluid.stellaris.water" )));



    }

    public record FluidInfos(ResourceLocation textureLocation, Component component) {

    }
}

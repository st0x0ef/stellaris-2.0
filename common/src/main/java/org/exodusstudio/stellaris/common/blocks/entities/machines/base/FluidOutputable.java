package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

/**
 * An interface for block entities that can output fluids. This is used to manage the fluid outputs of a machine.
 */
public interface FluidOutputable extends FluidProvider.BLOCK {


    default List<Fluid> getFluidsOutput() {
        return List.of();
    }

    FluidOutputManager getFluidOutputManager();

    List<UniversalFluidStorage> getOutputFluidsTank();

}

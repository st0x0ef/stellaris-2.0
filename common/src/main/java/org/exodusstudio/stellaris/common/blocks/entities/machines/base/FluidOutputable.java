package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public interface FluidOutputable extends FluidProvider.BLOCK {


    default List<Fluid> getFluidsOutput() {
        return List.of();
    }


}

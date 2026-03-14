package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;

import java.util.List;

public interface FluidOutputable extends FluidProvider.BLOCK {


    default List<UniversalFluidStorage> getIndexedStorages() {
        return List.of();
    }


}

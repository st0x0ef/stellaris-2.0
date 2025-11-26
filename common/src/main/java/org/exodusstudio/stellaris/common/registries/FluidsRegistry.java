package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.Stellaris;


public final class FluidsRegistry {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Stellaris.MOD_ID, Registries.FLUID);


    public static void register() {
        FLUIDS.register();
    }

    private FluidsRegistry() {}
}
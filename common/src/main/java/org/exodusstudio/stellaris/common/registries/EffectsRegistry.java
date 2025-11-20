package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.effects.CorrosionEffect;

public class EffectsRegistry {

    public static final DeferredRegister<MobEffect> EFFECTS = 
            DeferredRegister.create(Stellaris.MOD_ID, Registries.MOB_EFFECT);

    public static final RegistrySupplier<MobEffect> CORROSION = 
            EFFECTS.register("corrosion", CorrosionEffect::new);

    public static void register() {
        EFFECTS.register();
    }
}
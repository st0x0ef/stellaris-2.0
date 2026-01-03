package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;

import static org.exodusstudio.stellaris.common.utils.ResourceLocationUtils.id;

public class StellarisRegistries {
    public static final Registrar<RocketModule> ROCKET_MODULE;

    static {
        ROCKET_MODULE = register("rocket_module", true);
    }

    public static void register() {
        ROCKET_MODULE.key();
    }

    private static <T> Registrar<T> register(String registryId, boolean syncToClients) {
        if (syncToClients)
            return RegistrarManager.get(Stellaris.MOD_ID).<T>builder(id(registryId)).syncToClients().build();

        return RegistrarManager.get(Stellaris.MOD_ID).<T>builder(id(registryId)).build();
    }
}

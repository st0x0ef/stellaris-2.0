package org.exodusstudio.stellaris.common.registries;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rover.RoverModule;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.travel.PlanetRequirement;

import static org.exodusstudio.stellaris.common.utils.IdentifierUtils.id;

public class StellarisRegistries {
    public static final Registrar<RocketModule> ROCKET_MODULES;
    public static final Registrar<RoverModule> ROVER_MODULES;
    public static final Registrar<SpaceSuitModule> SPACE_SUIT_MODULES;
    public static final Registrar<MapCodec<? extends PlanetRequirement>> PLANET_REQUIREMENT_TYPES;

    static {
        ROCKET_MODULES = register("rocket_modules", true);
        ROVER_MODULES = register("rover_modules", true);
        SPACE_SUIT_MODULES = register("space_suit_modules", true);
        PLANET_REQUIREMENT_TYPES = register("planet_requirement_types", false);
    }

    public static void register() {
        ROCKET_MODULES.key();
        ROVER_MODULES.key();
        SPACE_SUIT_MODULES.key();
        PLANET_REQUIREMENT_TYPES.key();
    }

    private static <T> Registrar<T> register(String registryId, boolean syncToClients) {
        if (syncToClients)
            return RegistrarManager.get(Stellaris.MOD_ID).<T>builder(id(registryId)).syncToClients().build();

        return RegistrarManager.get(Stellaris.MOD_ID).<T>builder(id(registryId)).build();
    }
}

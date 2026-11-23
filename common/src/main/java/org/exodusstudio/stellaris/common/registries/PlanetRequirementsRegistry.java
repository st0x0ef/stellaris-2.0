package org.exodusstudio.stellaris.common.registries;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.RegistrySupplier;
import org.exodusstudio.stellaris.common.travel.ModuleRequirement;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class PlanetRequirementsRegistry {
    public static final RegistrySupplier<MapCodec<ModuleRequirement>> MODULE = StellarisRegistries.PLANET_REQUIREMENT_TYPES.register(IdentifierUtils.id("module"), () -> ModuleRequirement.CODEC);

    public static void init() {}
}

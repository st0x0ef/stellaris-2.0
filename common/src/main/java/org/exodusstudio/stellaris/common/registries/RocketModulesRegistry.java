package org.exodusstudio.stellaris.common.registries;


import dev.architectury.registry.registries.RegistrySupplier;
import org.exodusstudio.stellaris.common.items.modules.GalaxySkinModule;
import org.exodusstudio.stellaris.common.items.modules.HydrogenFuelModuleItem;
import org.exodusstudio.stellaris.common.items.modules.ShieldModule;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class RocketModulesRegistry {
    public static final RegistrySupplier<GalaxySkinModule> GALAXY_SKIN = StellarisRegistries.ROCKET_MODULE.register(IdentifierUtils.id("galaxy_skin"), ItemsRegistry.GALAXY_SKIN);
    public static final RegistrySupplier<ShieldModule> SHIELD_MODULE = StellarisRegistries.ROCKET_MODULE.register(IdentifierUtils.id("shield"), ItemsRegistry.SHIELD_MODULE);
    public static final RegistrySupplier<HydrogenFuelModuleItem> HYDROGEN_MOTOR = StellarisRegistries.ROCKET_MODULE.register(IdentifierUtils.id("hydrogen_motor"), ItemsRegistry.HYDROGEN_MOTOR);

    public static void init() {}
}

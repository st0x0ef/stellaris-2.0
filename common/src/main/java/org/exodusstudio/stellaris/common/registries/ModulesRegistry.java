package org.exodusstudio.stellaris.common.registries;


import dev.architectury.registry.registries.RegistrySupplier;
import org.exodusstudio.stellaris.common.items.modules.rocket.HydrogenFuelModuleItem;
import org.exodusstudio.stellaris.common.items.modules.rocket.RocketSkinModuleItem;
import org.exodusstudio.stellaris.common.items.modules.rocket.ShieldModule;
import org.exodusstudio.stellaris.common.items.modules.space_suit.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class ModulesRegistry {
    /** Rocket Modules */
    public static final RegistrySupplier<RocketSkinModuleItem> GALAXY_SKIN = StellarisRegistries.ROCKET_MODULES.register(IdentifierUtils.id("galaxy_skin"), ItemsRegistry.GALAXY_SKIN);
    public static final RegistrySupplier<ShieldModule> SHIELD_MODULE = StellarisRegistries.ROCKET_MODULES.register(IdentifierUtils.id("shield"), ItemsRegistry.SHIELD_MODULE);
    public static final RegistrySupplier<HydrogenFuelModuleItem> HYDROGEN_MOTOR = StellarisRegistries.ROCKET_MODULES.register(IdentifierUtils.id("hydrogen_motor"), ItemsRegistry.HYDROGEN_MOTOR);

    /** Space Suit Modules */
    public static final RegistrySupplier<OxygenModuleItem> OXYGEN_MODULE_T1 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("oxygen_module_tier_1"), ItemsRegistry.SPACE_SUIT_OXYGEN_MODULE_T1);
    public static final RegistrySupplier<OxygenModuleItem> OXYGEN_MODULE_T2 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("oxygen_module_tier_2"), ItemsRegistry.SPACE_SUIT_OXYGEN_MODULE_T2);
    public static final RegistrySupplier<OxygenModuleItem> OXYGEN_MODULE_T3 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("oxygen_module_tier_3"), ItemsRegistry.SPACE_SUIT_OXYGEN_MODULE_T3);

    public static final RegistrySupplier<OilFinderModuleItem> OIL_FINDER_MODULE_T1 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("oil_finder_module_tier_1"), ItemsRegistry.SPACE_SUIT_OIL_FINDER_MODULE_T1);
    public static final RegistrySupplier<OilFinderModuleItem> OIL_FINDER_MODULE_T2 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("oil_finder_module_tier_2"), ItemsRegistry.SPACE_SUIT_OIL_FINDER_MODULE_T2);
    public static final RegistrySupplier<OilFinderModuleItem> OIL_FINDER_MODULE_T3 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("oil_finder_module_tier_3"), ItemsRegistry.SPACE_SUIT_OIL_FINDER_MODULE_T3);

    public static final RegistrySupplier<TankModuleItem> DIESEL_TANK_MODULE_T1 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("diesel_tank_module_tier_1"), ItemsRegistry.SPACE_SUIT_DIESEL_TANK_MODULE_T1);
    public static final RegistrySupplier<TankModuleItem> DIESEL_TANK_MODULE_T2 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("diesel_tank_module_tier_2"), ItemsRegistry.SPACE_SUIT_DIESEL_TANK_MODULE_T2);
    public static final RegistrySupplier<TankModuleItem> DIESEL_TANK_MODULE_T3 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("diesel_tank_module_tier_3"), ItemsRegistry.SPACE_SUIT_DIESEL_TANK_MODULE_T3);

    public static final RegistrySupplier<TankModuleItem> HYDROGEN_TANK_MODULE_T1 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("hydrogen_tank_module_tier_1"), ItemsRegistry.SPACE_SUIT_HYDROGEN_TANK_MODULE_T1);
    public static final RegistrySupplier<TankModuleItem> HYDROGEN_TANK_MODULE_T2 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("hydrogen_tank_module_tier_2"), ItemsRegistry.SPACE_SUIT_HYDROGEN_TANK_MODULE_T2);
    public static final RegistrySupplier<TankModuleItem> HYDROGEN_TANK_MODULE_T3 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("hydrogen_tank_module_tier_3"), ItemsRegistry.SPACE_SUIT_HYDROGEN_TANK_MODULE_T3);

    public static final RegistrySupplier<JetModuleItem> JET_MODULE_T1 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("jet_module_tier_1"), ItemsRegistry.SPACE_SUIT_JET_MODULE_T1);
    public static final RegistrySupplier<JetModuleItem> JET_MODULE_T2 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("jet_module_tier_2"), ItemsRegistry.SPACE_SUIT_JET_MODULE_T2);
    public static final RegistrySupplier<JetModuleItem> JET_MODULE_T3 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("jet_module_tier_3"), ItemsRegistry.SPACE_SUIT_JET_MODULE_T3);

    public static final RegistrySupplier<DamageProtectionModuleItem> DAMAGE_PROTECTION_MODULE_T1 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("damage_protection_module_tier_1"), ItemsRegistry.SPACE_SUIT_DAMAGE_PROTECTION_MODULE_T1);
    public static final RegistrySupplier<DamageProtectionModuleItem> DAMAGE_PROTECTION_MODULE_T2 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("damage_protection_module_tier_2"), ItemsRegistry.SPACE_SUIT_DAMAGE_PROTECTION_MODULE_T2);
    public static final RegistrySupplier<DamageProtectionModuleItem> DAMAGE_PROTECTION_MODULE_T3 = StellarisRegistries.SPACE_SUIT_MODULES.register(IdentifierUtils.id("damage_protection_module_tier_3"), ItemsRegistry.SPACE_SUIT_DAMAGE_PROTECTION_MODULE_T3);

    public static void init() {}
}

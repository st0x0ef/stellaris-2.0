package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.menus.*;
import org.exodusstudio.stellaris.common.menus.engineering_station.RocketStationMenu;
import org.exodusstudio.stellaris.common.menus.engineering_station.EngineUpgradeMenu;
import org.exodusstudio.stellaris.common.menus.laboratory.ResearchMenu;
import org.exodusstudio.stellaris.common.menus.laboratory.VaccineMenu;

public class MenuTypesRegistry {

    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<MainTabletMenu>> TABLET = MENU_TYPE.register("tablet", () -> MenuRegistry.ofExtended(MainTabletMenu::new));
    public static final RegistrySupplier<MenuType<SDCardReaderApplicationMenu>> SD_CARD_READER = MENU_TYPE.register("sd_card_reader", () -> MenuRegistry.ofExtended(SDCardReaderApplicationMenu::create));
    public static final RegistrySupplier<MenuType<WikiApplicationMenu>> WIKI = MENU_TYPE.register("wiki", () -> MenuRegistry.ofExtended(WikiApplicationMenu::create));

    public static final RegistrySupplier<MenuType<SolarPanelMenu>> SOLAR_PANEL = MENU_TYPE.register("solar_panel", () -> MenuRegistry.ofExtended(SolarPanelMenu::create));
    public static final RegistrySupplier<MenuType<CoalGeneratorMenu>> COAL_GENERATOR = MENU_TYPE.register("coal_generator", () -> MenuRegistry.ofExtended(CoalGeneratorMenu::create));
    public static final RegistrySupplier<MenuType<DieselGeneratorMenu>> DIESEL_GENERATOR = MENU_TYPE.register("diesel_generator", () -> MenuRegistry.ofExtended(DieselGeneratorMenu::create));

    public static final RegistrySupplier<MenuType<PowerBankMenu>> POWER_BANK_MENU = MENU_TYPE.register("power_bank", () -> MenuRegistry.ofExtended(PowerBankMenu::create));
    public static final RegistrySupplier<MenuType<RocketMenu>> ROCKET_MENU = MENU_TYPE.register("rocket_menu", () -> MenuRegistry.ofExtended(RocketMenu::create));

    public static final RegistrySupplier<MenuType<ElectrolyzerMenu>> ELECTROLYZER = MENU_TYPE.register("electrolyzer", () -> MenuRegistry.ofExtended(ElectrolyzerMenu::create));
    public static final RegistrySupplier<MenuType<VacuumatorMenu>> VACUUMATOR = MENU_TYPE.register("vacuumator", () -> MenuRegistry.ofExtended(VacuumatorMenu::create));

    public static final RegistrySupplier<MenuType<EngineUpgradeMenu>> ENGINE_UPGRADE = MENU_TYPE.register("engine_upgrade", () -> MenuRegistry.ofExtended(EngineUpgradeMenu::create));
    public static final RegistrySupplier<MenuType<RocketStationMenu>> ROCKET_STATION = MENU_TYPE.register("rocket_station", () -> MenuRegistry.ofExtended(RocketStationMenu::create));

    public static final RegistrySupplier<MenuType<PumpjackMenu>> PUMPJACK = MENU_TYPE.register("pumpjack", () -> MenuRegistry.ofExtended(PumpjackMenu::create));
    public static final RegistrySupplier<MenuType<FuelRefineryMenu>> FUEL_REFINERY = MENU_TYPE.register("fuel_refinery", () -> MenuRegistry.ofExtended(FuelRefineryMenu::create));

    public static final RegistrySupplier<MenuType<GravityManipulatorMenu>> GRAVITY_MANIPULATOR = MENU_TYPE.register("gravity_manipulator", () -> MenuRegistry.ofExtended(GravityManipulatorMenu::create));
    public static final RegistrySupplier<MenuType<OxygenDistributorMenu>> OXYGEN_DISTRIBUTOR = MENU_TYPE.register("oxygen_distributor", () -> MenuRegistry.ofExtended(OxygenDistributorMenu::create));
    public static final RegistrySupplier<MenuType<FluidTankMenu>> FLUID_TANK_MENU = MENU_TYPE.register("fluid_tank", () -> MenuRegistry.ofExtended(FluidTankMenu::create));
    public static final RegistrySupplier<MenuType<LanderMenu>> LANDER_MENU = MENU_TYPE.register("lander_menu", () -> MenuRegistry.ofExtended(LanderMenu::create));

    public static final RegistrySupplier<MenuType<VaccineMenu>> LABORATORY_VACCINE = MENU_TYPE.register("laboratory_vaccine", () -> MenuRegistry.ofExtended(VaccineMenu::create));
    public static final RegistrySupplier<MenuType<ResearchMenu>> LABORATORY_RESEARCH = MENU_TYPE.register("laboratory_research", () -> MenuRegistry.ofExtended(ResearchMenu::create));

}


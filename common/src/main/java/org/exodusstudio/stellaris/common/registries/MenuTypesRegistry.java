package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.menus.*;

public class MenuTypesRegistry {

    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<MainTabletMenu>> TABLET = MENU_TYPE.register("tablet", () -> MenuRegistry.ofExtended(MainTabletMenu::new));
    public static final RegistrySupplier<MenuType<SDCardReaderApplicationMenu>> SD_CARD_READER = MENU_TYPE.register("sd_card_reader", () -> MenuRegistry.ofExtended(SDCardReaderApplicationMenu::create));

    public static final RegistrySupplier<MenuType<SolarPanelMenu>> SOLAR_PANEL = MENU_TYPE.register("solar_panel", () -> MenuRegistry.ofExtended(SolarPanelMenu::create));
    public static final RegistrySupplier<MenuType<CoalGeneratorMenu>> COAL_GENERATOR = MENU_TYPE.register("coal_generator", () -> MenuRegistry.ofExtended(CoalGeneratorMenu::create));
    public static final RegistrySupplier<MenuType<PowerBankMenu>> POWER_BANK_MENU = MENU_TYPE.register("power_bank", () -> MenuRegistry.ofExtended(PowerBankMenu::create));
    public static final RegistrySupplier<MenuType<RocketMenu>> ROCKET_MENU = MENU_TYPE.register("rocket_menu", () -> MenuRegistry.ofExtended(RocketMenu::create));

    public static final RegistrySupplier<MenuType<ElectrolyzerMenu>> ELECTROLYZER = MENU_TYPE.register("electrolyzer", () -> MenuRegistry.ofExtended(ElectrolyzerMenu::create));
    public static final RegistrySupplier<MenuType<VacuumatorMenu>> VACUUMATOR = MENU_TYPE.register("vacuumator", () -> MenuRegistry.ofExtended(VacuumatorMenu::create));
    public static final RegistrySupplier<MenuType<UpgradeStationMenu>> ROCKET_UPGRADE = MENU_TYPE.register("rocket_upgrade", () -> MenuRegistry.ofExtended(UpgradeStationMenu::create));

    public static final RegistrySupplier<MenuType<RocketStationMenu>> ROCKET_STATION = MENU_TYPE.register("rocket_station", () -> MenuRegistry.ofExtended(RocketStationMenu::createFromBuffer));

    public static final RegistrySupplier<MenuType<GravityManipulatorMenu>> GRAVITY_MANIPULATOR = MENU_TYPE.register("gravity_manipulator", () -> MenuRegistry.ofExtended(GravityManipulatorMenu::create));
}


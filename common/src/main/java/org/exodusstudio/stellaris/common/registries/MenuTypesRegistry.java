package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.menus.SolarPanelMenu;

public class MenuTypesRegistry {

    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU = MENU_TYPE.register("solar_panel", () -> MenuRegistry.ofExtended(SolarPanelMenu::create));
}

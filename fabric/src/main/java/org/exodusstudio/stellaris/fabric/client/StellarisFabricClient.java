package org.exodusstudio.stellaris.fabric.client;

import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import org.exodusstudio.stellaris.client.screens.CoalGeneratorScreen;
import org.exodusstudio.stellaris.client.screens.PowerBankScreen;
import org.exodusstudio.stellaris.client.screens.SolarPanelScreen;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public final class StellarisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerScreens();
    }

    private void registerScreens() {
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.POWER_BANK_MENU.get(), PowerBankScreen::new);
    }
}

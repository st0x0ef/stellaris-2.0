package org.exodusstudio.stellaris.fabric.client;

import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import org.exodusstudio.stellaris.client.screens.SolarPanelScreen;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public final class StellarisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerScreens();
    }

    private void registerScreens() {
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
    }
}

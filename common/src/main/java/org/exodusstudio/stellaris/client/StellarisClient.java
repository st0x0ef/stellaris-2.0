package org.exodusstudio.stellaris.client;

import dev.architectury.registry.menu.MenuRegistry;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class StellarisClient {

    public static void initClient() {
        registerScreens();
    }

    private static void registerScreens() {
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.TABLET.get(), MainTabletScreen::new);
    }


}

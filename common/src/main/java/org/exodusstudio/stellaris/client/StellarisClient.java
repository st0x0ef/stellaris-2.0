package org.exodusstudio.stellaris.client;

import dev.architectury.registry.menu.MenuRegistry;
import fr.tathan.exoconfig.platform.PlatformClientHelper;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.registry.FluidInfosRegistry;
import org.exodusstudio.stellaris.client.screens.ElectrolyzerScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class StellarisClient {

    public static void initClient() {
        ApplicationRegistry.init();

        FluidInfosRegistry.init();
        registerScreens();

        PlatformClientHelper.registerConfigScreen(Stellaris.MOD_ID, Stellaris.CONFIG);
    }

    //TODO make this loader abstract
    private static void registerScreens() {
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.ELECTROLYZER.get(), ElectrolyzerScreen::new);
    }
}

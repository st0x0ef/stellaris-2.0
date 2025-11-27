package org.exodusstudio.stellaris.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.StellarisClient;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.exodusstudio.stellaris.client.screens.CoalGeneratorScreen;
import org.exodusstudio.stellaris.client.screens.PowerBankScreen;
import org.exodusstudio.stellaris.client.screens.SolarPanelScreen;
import org.exodusstudio.stellaris.client.screens.VacuumatorScreen;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

@EventBusSubscriber(modid = Stellaris.MOD_ID, value = Dist.CLIENT)
public class StellarisNeoforgeClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(StellarisClient::initClient);
    }

    @SubscribeEvent
    public static void registerScreen(RegisterMenuScreensEvent event) {
        event.register(MenuTypesRegistry.TABLET_MENU.get(), MainTabletScreen::new);
        event.register(MenuTypesRegistry.SD_CARD_READER_MENU.get(), SDCardReaderApplicationScreen::new);

        event.register(MenuTypesRegistry.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
        event.register(MenuTypesRegistry.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(MenuTypesRegistry.POWER_BANK_MENU.get(), PowerBankScreen::new);
        event.register(MenuTypesRegistry.VACUUMATOR.get(), VacuumatorScreen::new);
    }
}

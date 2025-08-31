package org.exodusstudio.stellaris.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.SolarPanelScreen;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

@EventBusSubscriber(modid = Stellaris.MOD_ID, value = Dist.CLIENT)
public class StellarisNeoforgeClient {
    @SubscribeEvent
    public static void registerScreen(RegisterMenuScreensEvent event) {
        event.register(MenuTypesRegistry.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
    }
}

package org.exodusstudio.stellaris.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import fr.tathan.exoconfig.common.loader.ConfigsRegistry;
import fr.tathan.exoconfig.platform.PlatformClientHelper;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.overlays.FadeOverlay;
import org.exodusstudio.stellaris.client.overlays.RocketTimerOverlay;
import org.exodusstudio.stellaris.client.registry.FluidInfosRegistry;
import org.exodusstudio.stellaris.client.registry.KeyMappingsRegistry;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;

public class StellarisClient {

    public static ClientConfig CLIENT_CONFIG;


    public static void initClient() {
        CLIENT_CONFIG = ConfigsRegistry.getInstance().registerConfig(new ClientConfig(), CLIENT_CONFIG);
        ApplicationRegistry.init();

        FluidInfosRegistry.init();

        registerOverlays();

        KeyMappingsRegistry.init();
        ClientTickEvent.CLIENT_POST.register(KeyMappingsRegistry::clientTick);
        
        PlatformClientHelper.registerConfigScreen(Stellaris.MOD_ID, Stellaris.CONFIG);
        PlatformClientHelper.registerConfigScreen(Stellaris.MOD_ID, CLIENT_CONFIG);
    }

    public static void registerOverlays() {
        ClientGuiEvent.RENDER_HUD.register(RocketTimerOverlay::render);
        ClientGuiEvent.RENDER_HUD.register(FadeOverlay::render);
    }
}

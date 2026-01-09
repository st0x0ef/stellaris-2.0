package org.exodusstudio.stellaris.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import fr.tathan.exoconfig.platform.PlatformClientHelper;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.overlays.RocketTimerOverlay;
import org.exodusstudio.stellaris.client.registry.FluidInfosRegistry;
import org.exodusstudio.stellaris.client.registry.KeyMappingsRegistry;
import org.exodusstudio.stellaris.client.renderers.jet_suit.JetSuitModel;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.platform.ArmorPlatform;

public class StellarisClient {

    public static void initClient() {
        ApplicationRegistry.init();

        FluidInfosRegistry.init();

        registerOverlays();
        registerArmors();

        KeyMappingsRegistry.init();
        ClientTickEvent.CLIENT_POST.register(KeyMappingsRegistry::clientTick);

        PlatformClientHelper.registerConfigScreen(Stellaris.MOD_ID, Stellaris.CONFIG);
    }

    public static void registerOverlays() {
        ClientGuiEvent.RENDER_HUD.register(RocketTimerOverlay::render);
    }

    public static void registerArmors() {
        ArmorPlatform.registerArmor(JetSuitModel.LAYER_LOCATION, JetSuitModel::new, JetSuitModel.TEXTURE,
                ItemsRegistry.JETSUIT_BOOTS.get(), ItemsRegistry.JETSUIT_LEGGINGS.get(),
                ItemsRegistry.JETSUIT_HELMET.get(), ItemsRegistry.JETSUIT_SUIT.get());
    }
}

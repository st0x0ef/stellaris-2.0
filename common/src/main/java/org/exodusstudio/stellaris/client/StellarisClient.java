package org.exodusstudio.stellaris.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import fr.tathan.exoconfig.platform.PlatformClientHelper;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.overlays.SpaceSuitOverlay;
import org.exodusstudio.stellaris.client.overlays.RocketTimerOverlay;
import org.exodusstudio.stellaris.client.registry.FluidInfosRegistry;
import org.exodusstudio.stellaris.client.registry.KeyMappingsRegistry;
import org.exodusstudio.stellaris.client.renderers.space_suit.SpaceSuitModel;
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
        ClientGuiEvent.RENDER_HUD.register(SpaceSuitOverlay::render);
    }

    public static void registerArmors() {
        ArmorPlatform.registerArmor(SpaceSuitModel.LAYER_LOCATION, SpaceSuitModel::new, SpaceSuitModel.TEXTURE,
                ItemsRegistry.SPACE_SUIT_BOOTS.get(), ItemsRegistry.SPACE_SUIT_LEGGINGS.get(),
                ItemsRegistry.SPACE_SUIT_HELMET.get(), ItemsRegistry.SPACE_SUIT_CHESTPLATE.get());
    }
}

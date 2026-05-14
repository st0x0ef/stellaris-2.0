package org.exodusstudio.stellaris.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import fr.tathan.exoconfig.common.loader.ConfigsRegistry;
import fr.tathan.exoconfig.platform.PlatformClientHelper;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.effects.ParasiteCameraShake;
import org.exodusstudio.stellaris.client.events.ClientEvents;
import org.exodusstudio.stellaris.client.overlays.FadeOverlay;
import org.exodusstudio.stellaris.client.overlays.LanderOverlay;
import org.exodusstudio.stellaris.client.overlays.RocketTimerOverlay;
import org.exodusstudio.stellaris.client.registry.FluidInfosRegistry;
import org.exodusstudio.stellaris.client.registry.KeyMappingsRegistry;
import org.exodusstudio.stellaris.client.renderers.space_suit.SpaceSuitModel;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.WoodTypesRegister;
import org.exodusstudio.stellaris.platform.ArmorPlatform;

public class StellarisClient {

    public static ClientConfig CLIENT_CONFIG;

    public static void initClient() {
        CLIENT_CONFIG = ConfigsRegistry.getInstance().registerConfig(new ClientConfig(), CLIENT_CONFIG);
        ApplicationRegistry.init();

        FluidInfosRegistry.init();

        WoodTypesRegister.register();

        registerRenderLayers();
        registerOverlays();
        registerArmors();

        KeyMappingsRegistry.init();
        ClientTickEvent.CLIENT_POST.register(KeyMappingsRegistry::clientTick);
        ClientTickEvent.CLIENT_POST.register(ParasiteCameraShake::clientTick);

        ClientEvents.init();
        PlatformClientHelper.registerConfigScreen(Stellaris.MOD_ID, Stellaris.CONFIG);
        PlatformClientHelper.registerConfigScreen(Stellaris.MOD_ID, CLIENT_CONFIG);
    }

    public static void registerRenderLayers() {
        RenderTypeRegistry.register(ChunkSectionLayer.CUTOUT, BlocksRegistry.PUMPJACK_PROXY.get());
    }

    public static void registerOverlays() {
        ClientGuiEvent.RENDER_HUD.register(RocketTimerOverlay::render);
        ClientGuiEvent.RENDER_HUD.register(FadeOverlay::render);
        ClientGuiEvent.RENDER_HUD.register(LanderOverlay::render);
    }

    public static void registerArmors() {
        ArmorPlatform.registerArmor(
                SpaceSuitModel.LAYER_LOCATION,
                SpaceSuitModel::new,
                SpaceSuitModel.TEXTURE,
                ItemsRegistry.SPACE_SUIT_BOOTS.get(),
                ItemsRegistry.SPACE_SUIT_LEGGINGS.get(),
                ItemsRegistry.SPACE_SUIT_HELMET.get(),
                ItemsRegistry.SPACE_SUIT_CHESTPLATE.get()
        );
    }
}

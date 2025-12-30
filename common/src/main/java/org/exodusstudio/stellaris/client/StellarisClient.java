package org.exodusstudio.stellaris.client;

import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import fr.tathan.exoconfig.client.screen.ConfigScreen;
import fr.tathan.exoconfig.platform.PlatformClientHelper;
import net.minecraft.server.packs.PackType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.data.wiki.WikiPacks;
import org.exodusstudio.stellaris.client.models.rockets.RocketModel;
import org.exodusstudio.stellaris.client.renderer.rockets.RocketRenderer;
import org.exodusstudio.stellaris.client.screens.RocketScreen;
import org.exodusstudio.stellaris.client.screens.rocket_station.RocketStationScreen;
import org.exodusstudio.stellaris.client.screens.rocket_station.RocketUpgraderScreen;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;

import org.exodusstudio.stellaris.client.registry.FluidInfosRegistry;
import org.exodusstudio.stellaris.client.screens.ElectrolyzerScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class StellarisClient {



    public static void initClient() {
        Platform.getMod(Stellaris.MOD_ID).registerConfigurationScreen(previous -> new ConfigScreen<>(previous, Stellaris.CONFIG));
        registerPack();
        registerLayers();
        registerRenderers();
        ApplicationRegistry.init();

        FluidInfosRegistry.init();
        registerScreens();



    }

    //TODO make this loader abstract
    private static void registerScreens() {
        //MenuRegistry.registerScreenFactory(MenuTypesRegistry.TABLET.get(), MainTabletScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.ROCKET_MENU.get(), RocketScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.ROCKET_UPGRADE.get(), RocketUpgraderScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.ROCKET_STATION.get(), RocketStationScreen::new);

    }

    private  static void registerRenderers()
    {
        EntityRendererRegistry.register(EntityTypesRegistry.ROCKET, RocketRenderer::new);
    }
    private static void registerLayers() {
        EntityModelLayerRegistry.register(RocketModel.LAYER_LOCATION, RocketModel::createBodyLayer);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.ELECTROLYZER.get(), ElectrolyzerScreen::new);
    }

    private static void registerPack() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new WikiPacks.WikiEntryPack(), ResourceLocationUtils.id("wiki/entries"));

        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new WikiPacks.EntryInfoPack(), ResourceLocationUtils.id("wiki/infos"));
    }

}

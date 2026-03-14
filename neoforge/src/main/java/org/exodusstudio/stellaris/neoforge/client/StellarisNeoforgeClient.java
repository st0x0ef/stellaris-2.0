package org.exodusstudio.stellaris.neoforge.client;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.StellarisClient;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketModel;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockModel;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.flag.FlagHeadModel;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorModel;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.client.screens.*;
import org.exodusstudio.stellaris.client.screens.rocket_station.RocketStationScreen;
import org.exodusstudio.stellaris.client.screens.rocket_station.RocketUpgraderScreen;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.menus.WikiApplicationMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

@EventBusSubscriber(modid = Stellaris.MOD_ID, value = Dist.CLIENT)
public class StellarisNeoforgeClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(StellarisClient::initClient);
    }

    @SubscribeEvent
    public static void registerScreen(RegisterMenuScreensEvent event) {
        event.register(MenuTypesRegistry.TABLET.get(), MainTabletScreen::new);
        event.register(MenuTypesRegistry.SD_CARD_READER.get(), SDCardReaderApplicationScreen::new);
        event.register(MenuTypesRegistry.WIKI.get(), WikiApplicationScreen::new);

        event.register(MenuTypesRegistry.SOLAR_PANEL.get(), SolarPanelScreen::new);
        event.register(MenuTypesRegistry.COAL_GENERATOR.get(), CoalGeneratorScreen::new);
        event.register(MenuTypesRegistry.DIESEL_GENERATOR.get(), DieselGeneratorScreen::new);
        event.register(MenuTypesRegistry.POWER_BANK_MENU.get(), PowerBankScreen::new);
        event.register(MenuTypesRegistry.VACUUMATOR.get(), VacuumatorScreen::new);
        event.register(MenuTypesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorScreen::new);
        event.register(MenuTypesRegistry.ELECTROLYZER.get(), ElectrolyzerScreen::new);
        event.register(MenuTypesRegistry.ROCKET_STATION.get(), RocketStationScreen::new);
        event.register(MenuTypesRegistry.ROCKET_UPGRADE.get(), RocketUpgraderScreen::new);
        event.register(MenuTypesRegistry.OXYGEN_DISTRIBUTOR.get(), OxygenDistributorScreen::new);
        event.register(MenuTypesRegistry.PUMPJACK.get(), PumpjackScreen::new);
        event.register(MenuTypesRegistry.FUEL_REFINERY.get(), FuelRefineryScreen::new);

        event.register(MenuTypesRegistry.ROCKET_MENU.get(), RocketScreen::new);
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer((BlockEntityType<GravityManipulatorBlockEntity>)BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegistry.FLAG.get(), FlagBlockRenderer::new);

        event.registerEntityRenderer(EntityTypesRegistry.ROCKET.get(), RocketRenderer::new);
    }

    @SubscribeEvent
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GravityManipulatorModel.LAYER_LOCATION, GravityManipulatorModel::createBodyLayer);
        event.registerLayerDefinition(FlagHeadModel.HUMANOID_LAYER_LOCATION, FlagHeadModel::createHumanoidHeadLayer);
        event.registerLayerDefinition(FlagHeadModel.MOB_LAYER_LOCATION, FlagHeadModel::createMobHeadLayer);
        event.registerLayerDefinition(FlagBlockModel.LAYER_LOCATION, FlagBlockModel::createBodyLayer);
        event.registerLayerDefinition(RocketModel.LAYER_LOCATION, RocketModel::createBodyLayer);
    }
}

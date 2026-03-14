package org.exodusstudio.stellaris.fabric.client;

import dev.architectury.registry.client.gui.MenuScreenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketModel;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockModel;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.flag.FlagHeadModel;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorModel;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.client.screens.*;
import org.exodusstudio.stellaris.client.screens.rocket_station.RocketStationScreen;
import org.exodusstudio.stellaris.client.screens.rocket_station.RocketUpgraderScreen;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.planets.PlanetSelectionAppScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public final class StellarisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StellarisClient.initClient();
        registerScreens();
        registerEntityRenderer();
        registerEntityModelLayer();
    }

    private void registerScreens() {
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.TABLET.get(), MainTabletScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.SD_CARD_READER.get(), SDCardReaderApplicationScreen::new);

        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.SOLAR_PANEL.get(), SolarPanelScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.COAL_GENERATOR.get(), CoalGeneratorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.DIESEL_GENERATOR.get(), DieselGeneratorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.POWER_BANK_MENU.get(), PowerBankScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.VACUUMATOR.get(), VacuumatorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ELECTROLYZER.get(), ElectrolyzerScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ROCKET_STATION.get(), RocketStationScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ROCKET_UPGRADE.get(), RocketUpgraderScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.OXYGEN_DISTRIBUTOR.get(), OxygenDistributorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.PUMPJACK.get(), PumpjackScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.FUEL_REFINERY.get(), FuelRefineryScreen::new);

        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ROCKET_MENU.get(), RocketScreen::new);
    }

    @SuppressWarnings("unchecked")
    private void registerEntityRenderer() {
        BlockEntityRenderers.register((BlockEntityType<GravityManipulatorBlockEntity>)BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorBlockRenderer::new);
        BlockEntityRenderers.register(BlockEntitiesRegistry.FLAG.get(), FlagBlockRenderer::new);

        EntityRendererRegistry.register(EntityTypesRegistry.ROCKET.get(), RocketRenderer::new);
    }

    public static void registerEntityModelLayer() {
        EntityModelLayerRegistry.registerModelLayer(GravityManipulatorModel.LAYER_LOCATION, GravityManipulatorModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(FlagHeadModel.HUMANOID_LAYER_LOCATION, FlagHeadModel::createHumanoidHeadLayer);
        EntityModelLayerRegistry.registerModelLayer(FlagHeadModel.MOB_LAYER_LOCATION, FlagHeadModel::createMobHeadLayer);
        EntityModelLayerRegistry.registerModelLayer(FlagBlockModel.LAYER_LOCATION, FlagBlockModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(RocketModel.LAYER_LOCATION, RocketModel::createBodyLayer);
    }
}

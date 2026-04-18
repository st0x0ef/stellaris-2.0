package org.exodusstudio.stellaris.fabric.client;

import dev.architectury.registry.client.gui.MenuScreenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.client.registry.BoatModelLayerRegistry;
import org.exodusstudio.stellaris.client.renderers.lander.LanderModel;
import org.exodusstudio.stellaris.client.renderers.lander.LanderRenderer;
import org.exodusstudio.stellaris.client.renderers.rockets.models.BigRocketModel;
import org.exodusstudio.stellaris.client.renderers.rockets.models.SmallRocketModel;
import org.exodusstudio.stellaris.client.renderers.space_suit.SpaceSuitModel;
import org.exodusstudio.stellaris.client.renderers.rockets.models.TinyRocketModel;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockModel;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.flag.FlagHeadModel;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorModel;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.client.screens.*;
import org.exodusstudio.stellaris.client.screens.engineering_station.RocketStationScreen;
import org.exodusstudio.stellaris.client.screens.engineering_station.EngineUpgraderScreen;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.menus.WikiApplicationMenu;
import org.exodusstudio.stellaris.common.registries.*;

public final class StellarisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StellarisClient.initClient();
        registerScreens();
        registerEntityRenderer();
        registerRenderLayers();
        registerEntityModelLayer();
    }

    private void registerRenderLayers() {
        BlockRenderLayerMap.putBlock(BlocksRegistry.LUNAR_SAPLING.block().get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(BlocksRegistry.LUNAR_DOOR.block().get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(BlocksRegistry.LUNAR_TRAPDOOR.block().get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(BlocksRegistry.MOON_VINES.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(BlocksRegistry.MOON_VINES_PLANT.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(BlocksRegistry.ASTRUM_VITREUS_BLOCK.block().get(), ChunkSectionLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(BlocksRegistry.ASTRUM_VITREUS_CLUSTER.block().get(), ChunkSectionLayer.CUTOUT);
    }

    private void registerScreens() {
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.TABLET.get(), MainTabletScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.SD_CARD_READER.get(), SDCardReaderApplicationScreen::new);
        MenuScreenRegistry.registerScreenFactory(
                MenuTypesRegistry.WIKI.get(),
                (MenuScreenRegistry.ScreenFactory<WikiApplicationMenu, WikiApplicationScreen>) WikiApplicationScreen::new
        );

        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.SOLAR_PANEL.get(), SolarPanelScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.COAL_GENERATOR.get(), CoalGeneratorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.DIESEL_GENERATOR.get(), DieselGeneratorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.POWER_BANK_MENU.get(), PowerBankScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.VACUUMATOR.get(), VacuumatorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ELECTROLYZER.get(), ElectrolyzerScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ROCKET_STATION.get(), RocketStationScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ENGINE_UPGRADE.get(), EngineUpgraderScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.OXYGEN_DISTRIBUTOR.get(), OxygenDistributorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.PUMPJACK.get(), PumpjackScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.FUEL_REFINERY.get(), FuelRefineryScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.FLUID_TANK_MENU.get(), FluidTankScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.LANDER_MENU.get(), LanderScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ROCKET_MENU.get(), RocketScreen::new);
    }

    @SuppressWarnings("unchecked")
    private void registerEntityRenderer() {
        BlockEntityRenderers.register((BlockEntityType<GravityManipulatorBlockEntity>)BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorBlockRenderer::new);
        BlockEntityRenderers.register(BlockEntitiesRegistry.MOD_SIGN.get(), SignRenderer::new);
        BlockEntityRenderers.register(BlockEntitiesRegistry.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
        BlockEntityRenderers.register(BlockEntitiesRegistry.FLAG.get(), FlagBlockRenderer::new);

        EntityRendererRegistry.register(EntityTypesRegistry.LANDER.get(), LanderRenderer::new);
        EntityRendererRegistry.register(EntityTypesRegistry.ROCKET.get(), RocketRenderer::new);
        EntityRendererRegistry.register(EntityTypesRegistry.LUNAR_BOAT.get(), (c) -> new BoatRenderer(c, BoatModelLayerRegistry.LUNAR_BOAT));
        EntityRendererRegistry.register(EntityTypesRegistry.LUNAR_CHEST_BOAT.get(), (c) -> new BoatRenderer(c, BoatModelLayerRegistry.LUNAR_CHEST_BOAT));
    }

    public static void registerEntityModelLayer() {
        EntityModelLayerRegistry.registerModelLayer(GravityManipulatorModel.LAYER_LOCATION, GravityManipulatorModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(FlagHeadModel.HUMANOID_LAYER_LOCATION, FlagHeadModel::createHumanoidHeadLayer);
        EntityModelLayerRegistry.registerModelLayer(FlagHeadModel.MOB_LAYER_LOCATION, FlagHeadModel::createMobHeadLayer);
        EntityModelLayerRegistry.registerModelLayer(FlagBlockModel.LAYER_LOCATION, FlagBlockModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(LanderModel.LAYER_LOCATION, LanderModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(SpaceSuitModel.LAYER_LOCATION, SpaceSuitModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(BoatModelLayerRegistry.LUNAR_BOAT, BoatModel::createBoatModel);
        EntityModelLayerRegistry.registerModelLayer(BoatModelLayerRegistry.LUNAR_CHEST_BOAT, BoatModel::createChestBoatModel);

        EntityModelLayerRegistry.registerModelLayer(TinyRocketModel.LAYER_LOCATION, TinyRocketModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(SmallRocketModel.LAYER_LOCATION, SmallRocketModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(BigRocketModel.LAYER_LOCATION, BigRocketModel::createBodyLayer);
    }
}

package org.exodusstudio.stellaris.neoforge.client;

import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.client.registry.BoatModelLayerRegistry;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockModel;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.flag.FlagHeadModel;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorModel;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketModel;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.client.renderers.space_suit.SpaceSuitModel;
import org.exodusstudio.stellaris.client.screens.*;
import org.exodusstudio.stellaris.client.screens.engineering_station.EngineUpgraderScreen;
import org.exodusstudio.stellaris.client.screens.engineering_station.RocketStationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

@EventBusSubscriber(modid = Stellaris.MOD_ID, value = Dist.CLIENT)
public class StellarisNeoforgeClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            StellarisClient.initClient();
            registerRenderLayers();
        });
    }
    private static void registerRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.LUNAR_SAPLING.block().get(), ChunkSectionLayer.CUTOUT);
        ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.LUNAR_DOOR.block().get(), ChunkSectionLayer.CUTOUT);
        ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.LUNAR_TRAPDOOR.block().get(), ChunkSectionLayer.CUTOUT);
        ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.MOON_VINES.get(), ChunkSectionLayer.CUTOUT);
        ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.MOON_VINES_PLANT.get(), ChunkSectionLayer.CUTOUT);
        ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.ASTRUM_VITREUS_BLOCK.block().get(), ChunkSectionLayer.TRANSLUCENT);
        ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.ASTRUM_VITREUS_CLUSTER.block().get(), ChunkSectionLayer.CUTOUT);
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
        event.register(MenuTypesRegistry.OXYGEN_DISTRIBUTOR.get(), OxygenDistributorScreen::new);
        event.register(MenuTypesRegistry.PUMPJACK.get(), PumpjackScreen::new);
        event.register(MenuTypesRegistry.FUEL_REFINERY.get(), FuelRefineryScreen::new);

        event.register(MenuTypesRegistry.ROCKET_STATION.get(), RocketStationScreen::new);
        event.register(MenuTypesRegistry.ENGINE_UPGRADE.get(), EngineUpgraderScreen::new);

        event.register(MenuTypesRegistry.ROCKET_MENU.get(), RocketScreen::new);
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer((BlockEntityType<GravityManipulatorBlockEntity>)BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegistry.MOD_SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegistry.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegistry.FLAG.get(), FlagBlockRenderer::new);

        event.registerEntityRenderer(EntityTypesRegistry.ROCKET.get(), RocketRenderer::new);
        event.registerEntityRenderer(EntityTypesRegistry.LUNAR_BOAT.get(), (c) -> new BoatRenderer(c, BoatModelLayerRegistry.LUNAR_BOAT));
        event.registerEntityRenderer(EntityTypesRegistry.LUNAR_CHEST_BOAT.get(), (c) -> new BoatRenderer(c, BoatModelLayerRegistry.LUNAR_CHEST_BOAT));
    }

    @SubscribeEvent
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GravityManipulatorModel.LAYER_LOCATION, GravityManipulatorModel::createBodyLayer);
        event.registerLayerDefinition(FlagHeadModel.HUMANOID_LAYER_LOCATION, FlagHeadModel::createHumanoidHeadLayer);
        event.registerLayerDefinition(FlagHeadModel.MOB_LAYER_LOCATION, FlagHeadModel::createMobHeadLayer);
        event.registerLayerDefinition(FlagBlockModel.LAYER_LOCATION, FlagBlockModel::createBodyLayer);
        event.registerLayerDefinition(RocketModel.LAYER_LOCATION, RocketModel::createBodyLayer);
        event.registerLayerDefinition(SpaceSuitModel.LAYER_LOCATION, SpaceSuitModel::createBodyLayer);
        event.registerLayerDefinition(BoatModelLayerRegistry.LUNAR_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(BoatModelLayerRegistry.LUNAR_CHEST_BOAT, BoatModel::createChestBoatModel);

    }
}

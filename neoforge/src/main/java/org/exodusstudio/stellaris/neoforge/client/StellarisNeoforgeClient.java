package org.exodusstudio.stellaris.neoforge.client;

import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterDebugRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.exodusstudio.stellaris.client.debug.OxygenDebugRenderer;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.client.registry.BoatModelLayerRegistry;
import org.exodusstudio.stellaris.client.renderers.entity.vehicle.rover.RoverModel;
import org.exodusstudio.stellaris.client.renderers.entity.vehicle.rover.RoverRenderer;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockModel;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.flag.FlagHeadModel;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorModel;
import org.exodusstudio.stellaris.client.renderers.lander.LanderModel;
import org.exodusstudio.stellaris.client.renderers.lander.LanderRenderer;
import org.exodusstudio.stellaris.client.renderers.mobs.*;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.client.renderers.rockets.models.BigRocketModel;
import org.exodusstudio.stellaris.client.renderers.rockets.models.SmallRocketModel;
import org.exodusstudio.stellaris.client.renderers.rockets.models.TinyRocketModel;
import org.exodusstudio.stellaris.client.renderers.space_suit.SpaceSuitModel;
import org.exodusstudio.stellaris.client.screens.*;
import org.exodusstudio.stellaris.client.screens.engineering_station.EngineUpgraderScreen;
import org.exodusstudio.stellaris.client.screens.engineering_station.RocketStationScreen;
import org.exodusstudio.stellaris.client.screens.engineering_station.SpaceStationPlannerScreen;
import org.exodusstudio.stellaris.client.screens.laboratory.ResearchScreen;
import org.exodusstudio.stellaris.client.screens.laboratory.VaccineScreen;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.planets.PlanetSelectionAppScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

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
        event.register(MenuTypesRegistry.OXYGEN_DISTRIBUTOR.get(), OxygenDistributorScreen::new);
        event.register(MenuTypesRegistry.PUMPJACK.get(), PumpjackScreen::new);
        event.register(MenuTypesRegistry.FUEL_REFINERY.get(), FuelRefineryScreen::new);
        event.register(MenuTypesRegistry.ROCKET_STATION.get(), RocketStationScreen::new);
        event.register(MenuTypesRegistry.ENGINE_UPGRADE.get(), EngineUpgraderScreen::new);
        event.register(MenuTypesRegistry.LANDER_MENU.get(), LanderScreen::new);
        event.register(MenuTypesRegistry.ROCKET_MENU.get(), RocketScreen::new);
        event.register(MenuTypesRegistry.ROVER_MENU.get(), RoverScreen::new);
        event.register(MenuTypesRegistry.LABORATORY_VACCINE.get(), VaccineScreen::new);
        event.register(MenuTypesRegistry.LABORATORY_RESEARCH.get(), ResearchScreen::new);
        event.register(MenuTypesRegistry.SPACE_STATION_PLANNER.get(), SpaceStationPlannerScreen::new);
        event.register(MenuTypesRegistry.PLANET_SELECTION_MENU.get(), PlanetSelectionAppScreen::new);
        event.register(MenuTypesRegistry.CARGO_UNLOADER.get(), CargoUnloaderScreen::new);
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer((BlockEntityType<GravityManipulatorBlockEntity>)BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegistry.MOD_SIGN.get(), StandingSignRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegistry.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegistry.FLAG.get(), FlagBlockRenderer::new);
        event.registerEntityRenderer(EntityTypesRegistry.LANDER.get(), LanderRenderer::new);
        event.registerEntityRenderer(EntityTypesRegistry.ROCKET.get(), RocketRenderer::new);
        event.registerEntityRenderer(EntityTypesRegistry.ROVER.get(), RoverRenderer::new);

        event.registerEntityRenderer(EntityTypesRegistry.LUNAR_BOAT.get(), (c) -> new BoatRenderer(c, BoatModelLayerRegistry.LUNAR_BOAT));
        event.registerEntityRenderer(EntityTypesRegistry.LUNAR_CHEST_BOAT.get(), (c) -> new BoatRenderer(c, BoatModelLayerRegistry.LUNAR_CHEST_BOAT));
        event.registerEntityRenderer(EntityTypesRegistry.BLUE_FISH.get(), (c) -> new StellarisMobRenderer<>(c, new BlueFishModel(c.bakeLayer(BlueFishModel.LAYER_LOCATION)), IdentifierUtils.texture("entity/mob_blue_fish"), 0.72F, 1.35F, 0.18F));
        event.registerEntityRenderer(EntityTypesRegistry.LUNAR_PARASITE.get(), (c) -> new StellarisMobRenderer<>(c, new LunarParasiteModel(c.bakeLayer(LunarParasiteModel.LAYER_LOCATION)), IdentifierUtils.texture("entity/mob_lunar_parasite"), 0.82F, 1.45F, 0.25F));
        event.registerEntityRenderer(EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER.get(), (c) -> new StellarisMobRenderer<>(c, new ParasiteAffectedVillagerModel(c.bakeLayer(ParasiteAffectedVillagerModel.LAYER_LOCATION)), IdentifierUtils.texture("entity/mob_parasite_affected_villager"), 0.94F, 1.5F, 0.45F));
        event.registerEntityRenderer(EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER_EVOLVED.get(), (c) -> new StellarisMobRenderer<>(c, new EvolvedParasiteAffectedVillagerModel(c.bakeLayer(EvolvedParasiteAffectedVillagerModel.LAYER_LOCATION)), IdentifierUtils.texture("entity/mob_parasite_affected_villager_evolved"), 1.05F, 1.58F, 0.65F));
        event.registerEntityRenderer(EntityTypesRegistry.LUNA_SHADOW.get(), (c) ->
                new StellarisMobRenderer<>(
                        c,
                        new LunaShadowModel(c.bakeLayer(LunaShadowModel.LAYER_LOCATION)),
                        IdentifierUtils.texture("entity/mob_luna_shadow"),
                        0.82F,
                        1.80F,
                        0.65F
                )
        );
    }

    @SubscribeEvent
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GravityManipulatorModel.LAYER_LOCATION, GravityManipulatorModel::createBodyLayer);
        event.registerLayerDefinition(FlagHeadModel.HUMANOID_LAYER_LOCATION, FlagHeadModel::createHumanoidHeadLayer);
        event.registerLayerDefinition(FlagHeadModel.MOB_LAYER_LOCATION, FlagHeadModel::createMobHeadLayer);
        event.registerLayerDefinition(FlagBlockModel.LAYER_LOCATION, FlagBlockModel::createBodyLayer);
        event.registerLayerDefinition(LanderModel.LAYER_LOCATION, LanderModel::createBodyLayer);
        event.registerLayerDefinition(BlueFishModel.LAYER_LOCATION, BlueFishModel::createBodyLayer);
        event.registerLayerDefinition(LunarParasiteModel.LAYER_LOCATION, LunarParasiteModel::createBodyLayer);
        event.registerLayerDefinition(ParasiteAffectedVillagerModel.LAYER_LOCATION, ParasiteAffectedVillagerModel::createBodyLayer);
        event.registerLayerDefinition(EvolvedParasiteAffectedVillagerModel.LAYER_LOCATION, EvolvedParasiteAffectedVillagerModel::createBodyLayer);
        event.registerLayerDefinition(LunaShadowModel.LAYER_LOCATION, LunaShadowModel::createBodyLayer);

        event.registerLayerDefinition(SpaceSuitModel.LAYER_LOCATION, SpaceSuitModel::createBodyLayer);
        event.registerLayerDefinition(BoatModelLayerRegistry.LUNAR_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(BoatModelLayerRegistry.LUNAR_CHEST_BOAT, BoatModel::createChestBoatModel);


        event.registerLayerDefinition(TinyRocketModel.LAYER_LOCATION, TinyRocketModel::createBodyLayer);
        event.registerLayerDefinition(SmallRocketModel.LAYER_LOCATION, SmallRocketModel::createBodyLayer);
        event.registerLayerDefinition(BigRocketModel.LAYER_LOCATION, BigRocketModel::createBodyLayer);
        event.registerLayerDefinition(RoverModel.LAYER_LOCATION, RoverModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerDebugRenderers(RegisterDebugRenderersEvent event) {
        event.register(OxygenDebugRenderer.INSTANCE);
    }

    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        event.register(new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/hydrogen_still")),
                new Material(IdentifierUtils.id("block/fluids/hydrogen_flow")),
                null,
                null
        ), FluidsRegistry.HYDROGEN_STILL, FluidsRegistry.HYDROGEN_FLOWING);

        event.register(new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/oil_still")),
                new Material(IdentifierUtils.id("block/fluids/oil_flow")),
                null,
                null
        ), FluidsRegistry.OIL_STILL, FluidsRegistry.FLOWING_OIL);

        event.register(new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/fuel_still")),
                new Material(IdentifierUtils.id("block/fluids/fuel_flow")),
                null,
                null
        ), FluidsRegistry.FUEL_STILL, FluidsRegistry.FUEL_FLOWING);

        event.register(new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/astrum_liquidus_still")),
                new Material(IdentifierUtils.id("block/fluids/astrum_liquidus_flow")),
                null,
                null
        ), FluidsRegistry.ASTRUM_LIQUIDUS_STILL, FluidsRegistry.ASTRUM_LIQUIDUS_FLOWING);

        event.register(new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/blue_liquid_still")),
                new Material(IdentifierUtils.id("block/fluids/blue_liquid_flow")),
                null,
                null
        ), FluidsRegistry.BLUE_LIQUID_STILL, FluidsRegistry.BLUE_LIQUID_FLOWING);

        event.register(new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/oxygen_still")),
                new Material(IdentifierUtils.id("block/fluids/oxygen_flow")),
                null,
                null
        ), FluidsRegistry.OXYGEN_STILL, FluidsRegistry.OXYGEN_FLOWING);

        event.register(new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/diesel_still")),
                new Material(IdentifierUtils.id("block/fluids/diesel_flow")),
                null,
                null
        ), FluidsRegistry.DIESEL_STILL, FluidsRegistry.FLOWING_DIESEL);
    }
}

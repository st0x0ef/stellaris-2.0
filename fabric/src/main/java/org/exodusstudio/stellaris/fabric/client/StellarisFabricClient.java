package org.exodusstudio.stellaris.fabric.client;

import dev.architectury.registry.client.gui.MenuScreenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.client.registry.BoatModelLayerRegistry;
import org.exodusstudio.stellaris.client.renderers.entity.vehicle.rover.RoverRenderer;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.globe.GlobeBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.lander.LanderRenderer;
import org.exodusstudio.stellaris.client.renderers.launchpad.RocketLaunchPadBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.mobs.*;
import org.exodusstudio.stellaris.client.renderers.mobs.starcrawler.StarCrawlerRenderer;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.client.renderers.space_farm.SpaceFarmRenderer;
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
import org.exodusstudio.stellaris.common.blocks.entities.RocketLaunchPadBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public final class StellarisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StellarisClient.initClient();
        registerScreens();
        registerEntityRenderer();
        registerFluidRenderers();
    }

    private void registerScreens() {
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.TABLET.get(), MainTabletScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.SD_CARD_READER.get(), SDCardReaderApplicationScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.WIKI.get(), WikiApplicationScreen::new);

        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.SKY_PANEL.get(), SkyPanelScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.COAL_GENERATOR.get(), CoalGeneratorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.DIESEL_GENERATOR.get(), DieselGeneratorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.POWER_BANK_MENU.get(), PowerBankScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.VACUUMATOR.get(), VacuumatorScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.BLENDER.get(), BlenderScreen::new);
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
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ROVER_MENU.get(), RoverScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.ANTENNA.get(), AntennaScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.SPACE_STATION_PLANNER.get(), SpaceStationPlannerScreen::new);

        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.PLANET_SELECTION_MENU.get(), PlanetSelectionAppScreen::new);

        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.LABORATORY_VACCINE.get(), VaccineScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.LABORATORY_RESEARCH.get(), ResearchScreen::new);
        MenuScreenRegistry.registerScreenFactory(MenuTypesRegistry.CARGO_UNLOADER.get(), CargoUnloaderScreen::new);
    }

    @SuppressWarnings("unchecked")
    private void registerEntityRenderer() {
        StellarisClient.registerEntityModelLayer();

        BlockEntityRenderers.register((BlockEntityType<GravityManipulatorBlockEntity>)BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorBlockRenderer::new);
        BlockEntityRenderers.register(BlockEntitiesRegistry.MOD_SIGN.get(), StandingSignRenderer::new);
        BlockEntityRenderers.register(BlockEntitiesRegistry.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
        BlockEntityRenderers.register(BlockEntitiesRegistry.FLAG.get(), FlagBlockRenderer::new);
        BlockEntityRenderers.register(BlockEntitiesRegistry.GLOBE.get(), GlobeBlockRenderer::new);
        BlockEntityRenderers.register((BlockEntityType<RocketLaunchPadBlockEntity>)BlockEntitiesRegistry.ROCKET_LAUNCH_PAD.get(), RocketLaunchPadBlockRenderer::new);
        BlockEntityRenderers.register(BlockEntitiesRegistry.SPACE_FARM.get(), SpaceFarmRenderer::new);

        EntityRenderers.register(EntityTypesRegistry.LANDER.get(), LanderRenderer::new);
        EntityRenderers.register(EntityTypesRegistry.ROCKET.get(), RocketRenderer::new);
        EntityRenderers.register(EntityTypesRegistry.ROVER.get(), RoverRenderer::new);
        EntityRenderers.register(EntityTypesRegistry.LUNAR_BOAT.get(), (c) -> new BoatRenderer(c, BoatModelLayerRegistry.LUNAR_BOAT));
        EntityRenderers.register(EntityTypesRegistry.LUNAR_CHEST_BOAT.get(), (c) -> new BoatRenderer(c, BoatModelLayerRegistry.LUNAR_CHEST_BOAT));
        EntityRenderers.register(EntityTypesRegistry.BLUE_FISH.get(), (c) -> new StellarisMobRenderer<>(c, new BlueFishModel(c.bakeLayer(BlueFishModel.LAYER_LOCATION)), IdentifierUtils.texture("entity/mob_blue_fish"), 0.72F, 1.35F, 0.18F));
        EntityRenderers.register(EntityTypesRegistry.LUNAR_PARASITE.get(), (c) -> new StellarisMobRenderer<>(c, new LunarParasiteModel(c.bakeLayer(LunarParasiteModel.LAYER_LOCATION)), IdentifierUtils.texture("entity/mob_lunar_parasite"), 0.82F, 1.45F, 0.25F));
        EntityRenderers.register(EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER.get(), (c) -> new StellarisMobRenderer<>(c, new ParasiteAffectedVillagerModel(c.bakeLayer(ParasiteAffectedVillagerModel.LAYER_LOCATION)), IdentifierUtils.texture("entity/mob_parasite_affected_villager"), 0.94F, 1.5F, 0.45F));
        EntityRenderers.register(EntityTypesRegistry.PARASITE_AFFECTED_VILLAGER_EVOLVED.get(), (c) -> new StellarisMobRenderer<>(c, new EvolvedParasiteAffectedVillagerModel(c.bakeLayer(EvolvedParasiteAffectedVillagerModel.LAYER_LOCATION)), IdentifierUtils.texture("entity/mob_parasite_affected_villager_evolved"), 1.05F, 1.58F, 0.65F));
        EntityRenderers.register(EntityTypesRegistry.LUNA_SHADOW.get(), (c) ->
                new StellarisMobRenderer<>(
                        c,
                        new LunaShadowModel(c.bakeLayer(LunaShadowModel.LAYER_LOCATION)),
                        IdentifierUtils.texture("entity/mob_luna_shadow"),
                        0.82F,
                        1.80F,
                        0.65F
                )
        );
        EntityRenderers.register(EntityTypesRegistry.STAR_CRAWLER.get(), StarCrawlerRenderer::new);
        EntityRenderers.register(EntityTypesRegistry.ALIEN.get(), AlienRenderer::new);
    }

    private void registerFluidRenderers() {
        FluidRenderingRegistry.register(FluidsRegistry.OIL_STILL.get(), FluidsRegistry.FLOWING_OIL.get(), new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/oil_still")),
                new Material(IdentifierUtils.id("block/fluids/oil_flow")),
                null,
                null));

        FluidRenderingRegistry.register(FluidsRegistry.HYDROGEN_STILL.get(), FluidsRegistry.HYDROGEN_FLOWING.get(), new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/hydrogen_still")),
                new Material(IdentifierUtils.id("block/fluids/hydrogen_flow")),
                null,
                null));

        FluidRenderingRegistry.register(FluidsRegistry.FUEL_STILL.get(), FluidsRegistry.FUEL_FLOWING.get(), new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/fuel_still")),
                new Material(IdentifierUtils.id("block/fluids/fuel_flow")),
                null,
                null));

        FluidRenderingRegistry.register(FluidsRegistry.ASTRUM_LIQUIDUS_STILL.get(), FluidsRegistry.ASTRUM_LIQUIDUS_FLOWING.get(), new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/astrum_liquidus_still")),
                new Material(IdentifierUtils.id("block/fluids/astrum_liquidus_flow")),
                null,
                null));

        FluidRenderingRegistry.register(FluidsRegistry.BLUE_LIQUID_STILL.get(), FluidsRegistry.BLUE_LIQUID_FLOWING.get(), new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/blue_liquid_still")),
                new Material(IdentifierUtils.id("block/fluids/blue_liquid_flow")),
                null,
                null));

        FluidRenderingRegistry.register(FluidsRegistry.OXYGEN_STILL.get(), FluidsRegistry.OXYGEN_FLOWING.get(), new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/oxygen_still")),
                new Material(IdentifierUtils.id("block/fluids/oxygen_flow")),
                null,
                null));

        FluidRenderingRegistry.register(FluidsRegistry.DIESEL_STILL.get(), FluidsRegistry.FLOWING_DIESEL.get(), new FluidModel.Unbaked(
                new Material(IdentifierUtils.id("block/fluids/diesel_still")),
                new Material(IdentifierUtils.id("block/fluids/diesel_flow")),
                null,
                null));
    }
}

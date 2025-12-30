package org.exodusstudio.stellaris.fabric.client;

import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.client.renderers.blocks.gravity_manipulator.GravityManipulatorBlockRenderer;
import org.exodusstudio.stellaris.client.renderers.blocks.gravity_manipulator.GravityManipulatorModel;
import org.exodusstudio.stellaris.client.screens.*;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
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
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.TABLET.get(), MainTabletScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.SD_CARD_READER.get(), SDCardReaderApplicationScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.SOLAR_PANEL.get(), SolarPanelScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.COAL_GENERATOR.get(), CoalGeneratorScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.POWER_BANK_MENU.get(), PowerBankScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.VACUUMATOR.get(), VacuumatorScreen::new);
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorScreen::new);
    }

    @SuppressWarnings("unchecked")
    private void registerEntityRenderer() {
        BlockEntityRenderers.register((BlockEntityType<GravityManipulatorBlockEntity>)BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorBlockRenderer::new);
    }

    public static void registerEntityModelLayer() {
        EntityModelLayerRegistry.registerModelLayer(GravityManipulatorModel.LAYER_LOCATION, GravityManipulatorModel::createBodyLayer);
    }
}

package org.exodusstudio.stellaris.fabric.client;

import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.client.renderers.blocks.gravity_manipulator.GravityManipulatorRenderer;
import org.exodusstudio.stellaris.client.screens.*;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public final class StellarisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StellarisClient.initClient();
        registerScreens();
        registerEntityRenderer();
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

    private void registerEntityRenderer() {
        BlockEntityRenderers.register(BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), GravityManipulatorRenderer::new);

    }
}

package org.exodusstudio.stellaris.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import fr.tathan.exoconfig.platform.PlatformClientHelper;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.models.rockets.RocketModel;
import org.exodusstudio.stellaris.client.registry.FluidInfosRegistry;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;

public class StellarisClient {

    public static void initClient() {
        ApplicationRegistry.init();

        FluidInfosRegistry.init();

        registerLayers();
        registerRenderers();

        PlatformClientHelper.registerConfigScreen(Stellaris.MOD_ID, Stellaris.CONFIG);
    }

    private  static void registerRenderers() {
        EntityRendererRegistry.register(EntityTypesRegistry.ROCKET, RocketRenderer::new);
    }

    private static void registerLayers() {
        EntityModelLayerRegistry.register(RocketModel.LAYER_LOCATION, RocketModel::createBodyLayer);
    }
}

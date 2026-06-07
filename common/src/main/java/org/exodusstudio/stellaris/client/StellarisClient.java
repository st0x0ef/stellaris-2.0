package org.exodusstudio.stellaris.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import fr.tathan.exoconfig.common.loader.ConfigsRegistry;
import fr.tathan.exoconfig.platform.PlatformClientHelper;
import net.minecraft.client.model.object.boat.BoatModel;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.debug.OxygenDebugRenderer;
import org.exodusstudio.stellaris.client.effects.ParasiteCameraShake;
import org.exodusstudio.stellaris.client.events.ClientEvents;
import org.exodusstudio.stellaris.client.overlays.FadeOverlay;
import org.exodusstudio.stellaris.client.overlays.LanderOverlay;
import org.exodusstudio.stellaris.client.overlays.RocketTimerOverlay;
import org.exodusstudio.stellaris.client.registry.BoatModelLayerRegistry;
import org.exodusstudio.stellaris.client.registry.FluidInfosRegistry;
import org.exodusstudio.stellaris.client.registry.KeyMappingsRegistry;
import org.exodusstudio.stellaris.client.renderers.flag.FlagBlockModel;
import org.exodusstudio.stellaris.client.renderers.flag.FlagHeadModel;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorModel;
import org.exodusstudio.stellaris.client.renderers.lander.LanderModel;
import org.exodusstudio.stellaris.client.renderers.mobs.*;
import org.exodusstudio.stellaris.client.renderers.rockets.models.BigRocketModel;
import org.exodusstudio.stellaris.client.renderers.rockets.models.SmallRocketModel;
import org.exodusstudio.stellaris.client.renderers.rockets.models.TinyRocketModel;
import org.exodusstudio.stellaris.client.renderers.space_suit.SpaceSuitModel;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.WoodTypesRegister;
import org.exodusstudio.stellaris.platform.ArmorPlatform;

public class StellarisClient {

    public static ClientConfig CLIENT_CONFIG;

    public static void initClient() {
        CLIENT_CONFIG = ConfigsRegistry.getInstance().registerConfig(new ClientConfig(), CLIENT_CONFIG);
        ApplicationRegistry.init();

        FluidInfosRegistry.init();

        WoodTypesRegister.register();

        registerOverlays();
        registerArmors();

        KeyMappingsRegistry.init();
        ClientTickEvent.CLIENT_POST.register(KeyMappingsRegistry::clientTick);
        ClientTickEvent.CLIENT_POST.register(ParasiteCameraShake::clientTick);
        ClientTickEvent.CLIENT_POST.register(OxygenDebugRenderer::clientTick);

        ClientEvents.init();
        PlatformClientHelper.registerConfigScreen(Stellaris.MOD_ID, Stellaris.CONFIG);
        PlatformClientHelper.registerConfigScreen(Stellaris.MOD_ID, CLIENT_CONFIG);
    }

    public static void registerOverlays() {
        ClientGuiEvent.RENDER_HUD.register(RocketTimerOverlay::render);
        ClientGuiEvent.RENDER_HUD.register(FadeOverlay::render);
        ClientGuiEvent.RENDER_HUD.register(LanderOverlay::render);
    }

    public static void registerArmors() {
        ArmorPlatform.registerArmor(
                SpaceSuitModel.LAYER_LOCATION,
                SpaceSuitModel::new,
                SpaceSuitModel.TEXTURE,
                ItemsRegistry.SPACE_SUIT_BOOTS.get(),
                ItemsRegistry.SPACE_SUIT_LEGGINGS.get(),
                ItemsRegistry.SPACE_SUIT_HELMET.get(),
                ItemsRegistry.SPACE_SUIT_CHESTPLATE.get()
        );
    }

    public static void registerEntityModelLayer() {
        EntityModelLayerRegistry.register(GravityManipulatorModel.LAYER_LOCATION, GravityManipulatorModel::createBodyLayer);
        EntityModelLayerRegistry.register(FlagHeadModel.HUMANOID_LAYER_LOCATION, FlagHeadModel::createHumanoidHeadLayer);
        EntityModelLayerRegistry.register(FlagHeadModel.MOB_LAYER_LOCATION, FlagHeadModel::createMobHeadLayer);
        EntityModelLayerRegistry.register(FlagBlockModel.LAYER_LOCATION, FlagBlockModel::createBodyLayer);
        EntityModelLayerRegistry.register(LanderModel.LAYER_LOCATION, LanderModel::createBodyLayer);
        EntityModelLayerRegistry.register(BlueFishModel.LAYER_LOCATION, BlueFishModel::createBodyLayer);
        EntityModelLayerRegistry.register(LunarParasiteModel.LAYER_LOCATION, LunarParasiteModel::createBodyLayer);
        EntityModelLayerRegistry.register(ParasiteAffectedVillagerModel.LAYER_LOCATION, ParasiteAffectedVillagerModel::createBodyLayer);
        EntityModelLayerRegistry.register(EvolvedParasiteAffectedVillagerModel.LAYER_LOCATION, EvolvedParasiteAffectedVillagerModel::createBodyLayer);
        EntityModelLayerRegistry.register(LunaShadowModel.LAYER_LOCATION, LunaShadowModel::createBodyLayer);

        EntityModelLayerRegistry.register(SpaceSuitModel.LAYER_LOCATION, SpaceSuitModel::createBodyLayer);
        EntityModelLayerRegistry.register(BoatModelLayerRegistry.LUNAR_BOAT, BoatModel::createBoatModel);
        EntityModelLayerRegistry.register(BoatModelLayerRegistry.LUNAR_CHEST_BOAT, BoatModel::createChestBoatModel);

        EntityModelLayerRegistry.register(TinyRocketModel.LAYER_LOCATION, TinyRocketModel::createBodyLayer);
        EntityModelLayerRegistry.register(SmallRocketModel.LAYER_LOCATION, SmallRocketModel::createBodyLayer);
        EntityModelLayerRegistry.register(BigRocketModel.LAYER_LOCATION, BigRocketModel::createBodyLayer);
    }
}

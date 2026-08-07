package org.exodusstudio.stellaris;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.ToNumberPolicy;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.ReloadListenerRegistry;
import fr.tathan.exoconfig.common.loader.ConfigsRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationData;
import org.exodusstudio.stellaris.common.data.wiki.MarkdownData;
import org.exodusstudio.stellaris.common.data.wiki.WikiEntryPack;
import org.exodusstudio.stellaris.common.config.CommonConfig;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.data.SdCardData;
import org.exodusstudio.stellaris.common.data.assistant.AssistantData;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationData;
import org.exodusstudio.stellaris.common.data.wiki.WikiPacks;

import org.exodusstudio.stellaris.common.events.Events;
import org.exodusstudio.stellaris.common.network.NetworkRegistry;
import org.exodusstudio.stellaris.common.network.packets.SyncPlanetsPacket;
import org.exodusstudio.stellaris.common.network.packets.SyncSDCards;
import org.exodusstudio.stellaris.common.network.packets.SyncWiki;
import org.exodusstudio.stellaris.common.registries.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public final class Stellaris {
    public static final String MOD_ID = "stellaris";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .setStrictness(Strictness.LENIENT)
            .create();

    public static CommonConfig CONFIG;

    public static void init() {
        StellarisRegistries.register();
        CONFIG = ConfigsRegistry.getInstance().registerConfig(new CommonConfig(), CONFIG);

        RecipesRegistry.register();

        NetworkRegistry.init();
        FluidsRegistry.init();
        FeaturesRegistry.register();
        EffectsRegistry.register();
        EntityDataSerializersRegistry.register();
        AdvancementTriggerRegistry.TRIGGER_TYPES.register();
        DataComponentsRegistry.DATA_COMPONENT_TYPE.register();
        EntityTypesRegistry.ENTITY_TYPE.register();
        EntityAttributesRegistry.register();
        BlocksRegistry.BLOCKS.register();
        BlockEntitiesRegistry.BLOCK_ENTITY_TYPE.register();
        ProcessorsRegistry.STRUCTURE_PROCESSORS.register();
        ItemsRegistry.ITEMS.register();
        ModulesRegistry.init();
        CreativeTabsRegistry.register();
        StatsRegistry.STATS.register();
        MenuTypesRegistry.MENU_TYPE.register();
        MenuProviderRegistry.register();
        SoundRegistry.SOUNDS.register();

        ArgumentsTypesRegistry.init();
        CommandsRegistry.init();
        CapabilitiesRegistry.init();
        BiomeModificationsRegistry.register();
        Events.init();

        onAddReloadListenerEvent();
    }

    public static void onAddReloadListenerEvent() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new PlanetsData(), IdentifierUtils.id(PlanetsData.ID));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new SdCardData(), IdentifierUtils.id(SdCardData.ID));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new SpaceStationData(), IdentifierUtils.id(SpaceStationData.ID));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new AssistantData(), IdentifierUtils.id(AssistantData.ID));

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new WikiMarkdownData(), IdentifierUtils.id(WikiMarkdownData.ID));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new WikiEntryPack(), IdentifierUtils.id(WikiEntryPack.ID));
    }

    public static void onDatapackSyncEvent(ServerPlayer player, boolean joined) {
        if (joined) {
            NetworkManager.sendToPlayer(player, new SyncWiki(WikiMarkdownData.ENTRY_PAGES, WikiEntryPack.ENTRIES));
            NetworkManager.sendToPlayer(player, new SyncSDCards(SdCardData.SD_CARDS));
            NetworkManager.sendToPlayer(player, new SyncPlanetsPacket(new ArrayList<>(PlanetsData.PLANETS)));
        }
    }
}

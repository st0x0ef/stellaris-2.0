package org.exodusstudio.stellaris;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.ToNumberPolicy;
import fr.tathan.exoconfig.common.loader.ConfigsRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.exodusstudio.stellaris.client.data.wiki.WikiPacks;
import org.exodusstudio.stellaris.common.config.CommonConfig;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.events.Events;
import org.exodusstudio.stellaris.common.network.NetworkRegistry;
import org.exodusstudio.stellaris.common.registries.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

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

        NetworkRegistry.init();
        FluidsRegistry.init();

        EffectsRegistry.register();
        EntityDataSerializersRegistry.register();
        DataComponentsRegistry.DATA_COMPONENT_TYPE.register();
        EntityTypesRegistry.ENTITY_TYPE.register();
        BlocksRegistry.BLOCKS.register();
        BlockEntitiesRegistry.BLOCK_ENTITY_TYPE.register();
        ItemsRegistry.ITEMS.register();
        RocketModulesRegistry.init();
        CreativeTabsRegistry.register();
        SDCardsRegistry.register();
        StatsRegistry.STATS.register();
        MenuTypesRegistry.MENU_TYPE.register();
        ArgumentsTypesRegistry.init();
        CommandsRegistry.init();
        CapabilitiesRegistry.init();
        BiomeModificationsRegistry.register();

        Events.init();

        RecipesRegistry.register();
    }

    public static void onAddReloadListenerEvent(BiConsumer<Identifier, PreparableReloadListener> registry) {
        registry.accept(IdentifierUtils.id(PlanetsData.ID), new PlanetsData());

        registry.accept(IdentifierUtils.id("wiki/entries"), new WikiPacks.WikiEntryPack());
        registry.accept(IdentifierUtils.id("wiki/infos"), new WikiPacks.EntryInfoPack());
    }
}

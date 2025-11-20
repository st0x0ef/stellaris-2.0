package org.exodusstudio.stellaris;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.Strictness;
import fr.tathan.exoconfig.common.loader.ConfigsRegistry;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.config.CommonConfig;
import org.exodusstudio.stellaris.common.network.NetworkRegistry;
import org.exodusstudio.stellaris.common.registries.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        CONFIG = ConfigsRegistry.getInstance().registerConfig(new CommonConfig(), CONFIG);

        NetworkRegistry.init();

        // Keep above the blocks and item registries, please, or it will crash when adding fluids
        FluidsRegistry.register();
        EffectsRegistry.register();
        DataComponentsRegistry.DATA_COMPONENT_TYPE.register();
        BlocksRegistry.BLOCKS.register();
        BlockEntitiesRegistry.BLOCK_ENTITY_TYPE.register();
        ItemsRegistry.ITEMS.register();
        CreativeTabsRegistry.register();
        MenuTypesRegistry.MENU_TYPE.register();
        CommandsRegistry.register();
        ApplicationRegistry.init();
        CapabilitiesRegistry.init();

    }
}

package org.exodusstudio.stellaris;

import org.exodusstudio.stellaris.common.network.NetworkRegistry;
import org.exodusstudio.stellaris.common.registries.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Stellaris {
    public static final String MOD_ID = "stellaris";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        NetworkRegistry.init();
        
        BlocksRegistry.BLOCKS.register();
        BlockEntitiesRegistry.BLOCK_ENTITY_TYPE.register();
        ItemsRegistry.ITEMS.register();
        CreativeTabsRegistry.register();
        MenuTypesRegistry.MENU_TYPE.register();
        CapabilitiesRegistry.init();
    }
}

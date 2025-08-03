package org.exodusstudio.stellaris;

import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.CreativeTabsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Stellaris {
    public static final String MOD_ID = "stellaris";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        BlocksRegistry.BLOCKS.register();
        ItemsRegistry.ITEMS.register();
        CreativeTabsRegistry.register();
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}

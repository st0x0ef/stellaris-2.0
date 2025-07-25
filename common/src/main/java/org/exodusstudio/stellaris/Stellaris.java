package org.exodusstudio.stellaris;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.registries.BlocksRegistry;
import org.exodusstudio.stellaris.registries.ItemsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public final class Stellaris {
    public static final String MOD_ID = "stellaris";
    public static final Logger LOG = LoggerFactory.getLogger("Stellaris");
    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static void init() {

        ItemsRegistry.ITEMS.key();
        BlocksRegistry.BLOCKS.key();
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}

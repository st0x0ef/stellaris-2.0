package org.exodusstudio.stellaris.fabric;

import net.fabricmc.api.ModInitializer;

import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.registries.BlocksRegistry;

public final class StellarisFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Stellaris.init();
    }
}

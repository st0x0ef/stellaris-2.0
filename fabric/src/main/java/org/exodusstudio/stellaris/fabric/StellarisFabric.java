package org.exodusstudio.stellaris.fabric;

import net.fabricmc.api.ModInitializer;

import org.exodusstudio.stellaris.Stellaris;

public final class StellarisFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Stellaris.init();
    }
}

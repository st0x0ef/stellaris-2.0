package org.exodusstudio.stellaris.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import org.exodusstudio.stellaris.client.StellarisClient;

public final class StellarisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StellarisClient.initClient();
    }
}

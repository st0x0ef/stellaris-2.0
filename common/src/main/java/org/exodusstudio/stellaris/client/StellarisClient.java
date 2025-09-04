package org.exodusstudio.stellaris.client;

import dev.architectury.platform.Platform;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.ConfigScreen;

public class StellarisClient {
    public static void initClient() {
        Platform.getMod(Stellaris.MOD_ID).registerConfigurationScreen(ConfigScreen::new);
    }
}

package org.exodusstudio.stellaris;

import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Stellaris {
    public static final String MOD_ID = "stellaris";
    public static final Logger LOG = LoggerFactory.getLogger("Stellaris");

    public static void init() {
        ApplicationRegistry.init();

    }
}

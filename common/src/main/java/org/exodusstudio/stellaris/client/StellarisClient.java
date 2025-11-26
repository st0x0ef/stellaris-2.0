package org.exodusstudio.stellaris.client;

import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import fr.tathan.exoconfig.client.screen.ConfigScreen;
import net.minecraft.server.packs.PackType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.data.wiki.WikiPack;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class StellarisClient {

    public static void initClient() {
        Platform.getMod(Stellaris.MOD_ID).registerConfigurationScreen(previous -> new ConfigScreen<>(previous, Stellaris.CONFIG));
        registerPack();
        ApplicationRegistry.init();
    }

    private static void registerPack() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new WikiPack(), ResourceLocationUtils.id("wiki"));
    }
}

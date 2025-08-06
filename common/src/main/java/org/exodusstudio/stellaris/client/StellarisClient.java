package org.exodusstudio.stellaris.client;

import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.server.packs.PackType;
import org.exodusstudio.stellaris.client.data.wiki.TabletPack;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class StellarisClient {

    public static void initClient() {
        registerScreens();
        registerPack();
        ApplicationRegistry.init();
    }

    private static void registerScreens() {
        MenuRegistry.registerScreenFactory(MenuTypesRegistry.TABLET.get(), MainTabletScreen::new);
    }

    private static void registerPack() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new TabletPack(), ResourceLocationUtils.id("wiki"));
    }


}

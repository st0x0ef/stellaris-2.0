package org.exodusstudio.stellaris.client.screen.tablet.application;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.Stellaris;

public class ApplicationRegistry {


    public static final Registrar<ApplicationFactory> TABLET_APPLICATION =
            RegistrarManager.get(Stellaris.MOD_ID)
                    .<ApplicationFactory>builder(ResourceLocation.parse("stellaris:applications")) // The type for builder should match the Registrar
                    .syncToClients()
                    .build();

    public static RegistrySupplier<ApplicationFactory> WIKI1 = TABLET_APPLICATION.register(
            ResourceLocation.parse("stellaris:applications/wiki"),
            () -> new ApplicationFactory(
                    Component.translatable("application.stellaris.wiki.name"),
                    Component.translatable("application.stellaris.wiki.description"),
                    ResourceLocation.fromNamespaceAndPath("stellaris", "textures/gui/application/wiki_icon.png"),
                    WikiApplicationScreen::new
            )
    );

    public static void init() {
        TABLET_APPLICATION.key();
    }

}

package org.exodusstudio.stellaris.client.screen.tablet.application;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.menu.application.AbstractApplicationMenu;
import org.exodusstudio.stellaris.common.menu.application.WikiApplicationMenu;

public class ApplicationRegistry {

    public static final Registrar<AbstractApplicationScreen<? extends AbstractApplicationMenu<?>>> TABLET_APPLICATION  = RegistrarManager.get(Stellaris.MOD_ID).<AbstractApplicationScreen<?>>builder(
                ResourceLocation.parse("stellaris:applications"))
                .syncToClients()
                .build();


    public static final RegistrySupplier<AbstractApplicationScreen<WikiApplicationMenu>> SUIT_MODULE_SCREEN = TABLET_APPLICATION
            .register(ResourceLocation.parse("wiki"), WikiApplicationScreen::new);

    public static void init() {
        TABLET_APPLICATION.key();
    }

}

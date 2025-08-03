package org.exodusstudio.stellaris.client.screen.tablet.application;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.menu.application.AbstractApplicationMenu;
import org.exodusstudio.stellaris.common.menu.application.WikiApplicationMenu;

import java.util.HashSet;
import java.util.function.Supplier;

public class ApplicationRegistry {


    public static final Registrar<AbstractApplicationScreen<? extends AbstractApplicationMenu>> TABLET_APPLICATION =
            RegistrarManager.get(Stellaris.MOD_ID)
                    .<AbstractApplicationScreen<? extends AbstractApplicationMenu>>builder(ResourceLocation.parse("stellaris:applications"))
                    .syncToClients()
                    .build();

    // Registering WikiApplicationScreen.
    // Since WikiApplicationScreen extends AbstractApplicationScreen<WikiMenu>, and WikiMenu extends AbstractApplicationMenu,
    // WikiApplicationScreen is compatible with AbstractApplicationScreen<? extends AbstractApplicationMenu>.
    public static RegistrySupplier<WikiApplicationScreen> WIKI1 = TABLET_APPLICATION.register(
            ResourceLocation.parse("stellaris:applications/wiki"),
            WikiApplicationScreen::new
    );

    public static void init() {
        TABLET_APPLICATION.key();
    }

}

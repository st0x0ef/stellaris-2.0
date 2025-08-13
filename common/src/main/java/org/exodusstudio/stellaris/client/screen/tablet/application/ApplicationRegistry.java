package org.exodusstudio.stellaris.client.screen.tablet.application;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screen.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;

import java.util.function.Function;

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
                    WikiApplicationScreen::create
            )
    );

    public static void init() {
        TABLET_APPLICATION.key();
    }

    public static class ApplicationFactory {

        private final Component name;
        private final Component description;
        private final ResourceLocation iconLocation;
        private final Function<MenuHolder, Screen> screenFactory; // Function that takes Player and returns a Screen

        public ApplicationFactory(Component name, Component description, ResourceLocation iconLocation, Function<MenuHolder, Screen> screenFactory) {
            this.name = name;
            this.description = description;
            this.iconLocation = iconLocation;
            this.screenFactory = screenFactory;
        }

        public Component getName() { return name; }
        public Component getDescription() { return description; }
        public ResourceLocation getIconLocation() { return iconLocation; }

        public Screen createScreen(MenuHolder screen) {
            return screenFactory.apply(screen);
        }
    }

    /**
     * Holder for the menu and inventory to be used in the application screens.
     * @param menu
     * @param inventory
     */
    public record MenuHolder(MainTabletMenu menu, Inventory inventory) {}

}

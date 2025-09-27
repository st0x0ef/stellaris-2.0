package org.exodusstudio.stellaris.client.screens.tablet.application;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.stats.StatsApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class ApplicationRegistry {


    public static final Registrar<ApplicationFactory> TABLET_APPLICATION =
            RegistrarManager.get(Stellaris.MOD_ID)
                    .<ApplicationFactory>builder(ResourceLocation.parse("stellaris:applications")) // The type for builder should match the Registrar
                    .syncToClients()
                    .build();

    public static RegistrySupplier<ApplicationFactory> WIKI1 = TABLET_APPLICATION.register(
            ResourceLocation.parse("stellaris:applications/wiki"),
            () -> new ApplicationFactory<MainTabletMenu>(
                    Component.translatable("application.stellaris.wiki.name"),
                    Component.translatable("application.stellaris.wiki.description"),
                    ResourceLocation.fromNamespaceAndPath("stellaris", "textures/gui/application/wiki_icon.png"),
                    WikiApplicationScreen::create,
                    null
            )
    );

    public static RegistrySupplier<ApplicationFactory> SD_CARD_READER = TABLET_APPLICATION.register(
            ResourceLocation.parse("stellaris:applications/sd_card_reader"),
            () -> new ApplicationFactory<>(
                    Component.translatable("application.stellaris.sd_card_reader.name"),
                    Component.translatable("application.stellaris.sd_card_reader.description"),
                    ResourceLocation.fromNamespaceAndPath("stellaris", "textures/gui/application/sd_card_reader_icon.png"),
                    SDCardReaderApplicationScreen::create,
                    null
            )
    );

    public static RegistrySupplier<ApplicationFactory> STATS = TABLET_APPLICATION.register(
            ResourceLocation.parse("stellaris:applications/stats"),
            () -> new ApplicationFactory<>(
                    Component.translatable("application.stellaris.stats.name"),
                    Component.translatable("application.stellaris.stats.description"),
                    ResourceLocation.fromNamespaceAndPath("stellaris", "textures/gui/application/stats_icon.png"),
                    StatsApplicationScreen::create,
                    null
            )
    );

    public static void init() {
        TABLET_APPLICATION.key();
    }

    public static class ApplicationFactory<T extends AbstractContainerMenu> {

        private final MutableComponent name;
        private final MutableComponent description;
        private final ResourceLocation iconLocation;
        private final Function<MenuHolder<T>, Screen> screenFactory;

        public ApplicationFactory(MutableComponent name, MutableComponent description, ResourceLocation iconLocation,
                                   @Nullable Function<MenuHolder<T>, Screen> screenFactory, @Nullable Consumer<MenuHolder<T>> menuOperation) {
            this.name = name;
            this.description = description;
            this.iconLocation = iconLocation;
            this.screenFactory = screenFactory;
        }

        public MutableComponent getName() { return name; }
        public MutableComponent getDescription() { return description; }
        public ResourceLocation getIconLocation() { return iconLocation; }

        public Screen createScreen(MenuHolder<T> screen) {
            return screenFactory.apply(screen);
        }
    }

    /**
     * Holder for the menu and inventory to be used in the application screens.
     * @param menu
     * @param inventory
     */
    public record MenuHolder<T extends AbstractContainerMenu>(T menu, Inventory inventory, MainTabletScreen mainTabletScreen) {}

}

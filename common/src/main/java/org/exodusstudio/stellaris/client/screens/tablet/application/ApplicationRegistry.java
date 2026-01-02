package org.exodusstudio.stellaris.client.screens.tablet.application;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.stats.StatsApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ApplicationRegistry {


    public static final Registrar<ApplicationFactory<?>> TABLET_APPLICATION =
            RegistrarManager.get(Stellaris.MOD_ID)
                    .<ApplicationFactory<?>>builder(Identifier.parse("stellaris:applications")) // The type for builder should match the Registrar
                    .syncToClients()
                    .build();

    public static RegistrySupplier<ApplicationFactory<MainTabletMenu>> WIKI = TABLET_APPLICATION.register(
            Identifier.parse("stellaris:applications/wiki"),
            () -> new ApplicationFactory<>(
                    Component.translatable("application.stellaris.wiki.name"),
                    Component.translatable("application.stellaris.wiki.description"),
                    IdentifierUtils.id("icon/wiki_app"),
                    IdentifierUtils.id("icon/wiki_app_hover"),
                    WikiApplicationScreen::create
            )
    );

    public static RegistrySupplier<ApplicationFactory<MainTabletMenu>> SD_CARD_READER = TABLET_APPLICATION.register(
            Identifier.parse("stellaris:applications/sd_card_reader"),
            () -> new ApplicationFactory<>(
                    Component.translatable("application.stellaris.sd_card_reader.name"),
                    Component.translatable("application.stellaris.sd_card_reader.description"),
                    IdentifierUtils.id("icon/wiki_app"),
                    IdentifierUtils.id("icon/wiki_app_hover"),
                    SDCardReaderApplicationScreen::create
            )
    );

    public static RegistrySupplier<ApplicationFactory<MainTabletMenu>> STATS = TABLET_APPLICATION.register(
            Identifier.parse("stellaris:applications/stats"),
            () -> new ApplicationFactory<>(
                    Component.translatable("application.stellaris.stats.name"),
                    Component.translatable("application.stellaris.stats.description"),
                    IdentifierUtils.id("icon/wiki_app"),
                    IdentifierUtils.id("icon/wiki_app_hover"),
                    StatsApplicationScreen::create
            )
    );

    public static void init() {
        TABLET_APPLICATION.key();
    }

    public record ApplicationFactory<T extends AbstractContainerMenu>(MutableComponent name,
                                                                      MutableComponent description,
                                                                      Identifier iconLocation,
                                                                      Identifier iconHoverLocation,
                                                                      Function<MenuHolder<T>, Screen> screenFactory) {

            public ApplicationFactory(MutableComponent name, MutableComponent description, Identifier iconLocation, Identifier iconHoverLocation,
                                      @Nullable Function<MenuHolder<T>, Screen> screenFactory) {
                this.name = name;
                this.description = description;
                this.iconLocation = iconLocation;
                this.screenFactory = screenFactory;
                this.iconHoverLocation = iconHoverLocation;
            }

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

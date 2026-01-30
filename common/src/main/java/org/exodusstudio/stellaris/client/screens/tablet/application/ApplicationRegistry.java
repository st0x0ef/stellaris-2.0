package org.exodusstudio.stellaris.client.screens.tablet.application;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.stats.StatsApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class ApplicationRegistry {

    private static HashMap<Identifier, ApplicationFactory<? extends AbstractContainerMenu>> applications = new HashMap<>();

    public static HashMap<Identifier, ApplicationFactory<? extends AbstractContainerMenu>> getApplications() {
        return applications;
    }

    public static <T extends AbstractContainerMenu> ApplicationFactory<T> register(Identifier id, ApplicationFactory<T> factory) {
        applications.put(id, factory);
        return factory;
    }

    public static ApplicationFactory<MainTabletMenu> WIKI = register(
            Identifier.parse("stellaris:applications/wiki"),
            new ApplicationFactory<>(
                    Component.translatable("application.stellaris.wiki.name"),
                    Component.translatable("application.stellaris.wiki.description"),
                    IdentifierUtils.id("icon/wiki_app"),
                    IdentifierUtils.id("icon/wiki_app_hover"),
                    WikiApplicationScreen::create
            )
    );

    public static ApplicationFactory<MainTabletMenu> SD_CARD_READER = register(
            Identifier.parse("stellaris:applications/sd_card_reader"),
            new ApplicationFactory<>(
                    Component.translatable("application.stellaris.sd_card_reader.name"),
                    Component.translatable("application.stellaris.sd_card_reader.description"),
                    IdentifierUtils.id("icon/sd_card_reader_app"),
                    IdentifierUtils.id("icon/sd_card_reader_app_hover"),
                    SDCardReaderApplicationScreen::create
            )
    );

    public static ApplicationFactory<MainTabletMenu> STATS = register(
            Identifier.parse("stellaris:applications/stats"),
            new ApplicationFactory<>(
                    Component.translatable("application.stellaris.stats.name"),
                    Component.translatable("application.stellaris.stats.description"),
                    IdentifierUtils.id("icon/stats_app"),
                    IdentifierUtils.id("icon/stats_app_hover"),

                    StatsApplicationScreen::create
            )
    );

    public static void init() {

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

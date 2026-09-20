package org.exodusstudio.stellaris.client.screens.tablet.application;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.planets.PlanetSelectionAppScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.stats.StatsApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ApplicationRegistry {

    private static final HashMap<Identifier, ApplicationFactory<? extends AbstractContainerMenu>> applications = new HashMap<>();
    public static final List<Class<? extends AbstractContainerScreen<?>>> applications_menus = new ArrayList<>(Set.of(MainTabletScreen.class));

    public static HashMap<Identifier, ApplicationFactory<? extends AbstractContainerMenu>> getApplications() {
        return applications;
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractContainerMenu> ApplicationFactory<T> register(Identifier id, ApplicationFactory<T> factory, @Nullable Class<? extends Screen> screenClass) {
        applications.put(id, factory);

        //Check if the class is a subclass of AbstractContainerMenu and add it to the list if it is
        if (screenClass != null && AbstractContainerScreen.class.isAssignableFrom(screenClass)) {
            applications_menus.add((Class<? extends AbstractContainerScreen<?>>) screenClass);
        }

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
            ),
            WikiApplicationScreen.class
    );

    public static ApplicationFactory<MainTabletMenu> SD_CARD_READER = register(
            Identifier.parse("stellaris:applications/sd_card_reader"),
            new ApplicationFactory<>(
                    Component.translatable("application.stellaris.sd_card_reader.name"),
                    Component.translatable("application.stellaris.sd_card_reader.description"),
                    IdentifierUtils.id("icon/sd_card_reader_app"),
                    IdentifierUtils.id("icon/sd_card_reader_app_hover"),
                    SDCardReaderApplicationScreen::create
            ),
            SDCardReaderApplicationScreen.class
    );

    public static ApplicationFactory<MainTabletMenu> PLANET_SELECTION = register(
            Identifier.parse("stellaris:applications/planet_selection"),
            new ApplicationFactory<>(
                    Component.translatable("application.stellaris.planet_selection.name"),
                    Component.translatable("application.stellaris.planet_selection.description"),
                    IdentifierUtils.id("icon/planet_selection_app"),
                    IdentifierUtils.id("icon/planet_selection_app_hover"),
                    PlanetSelectionAppScreen::create
            ),
            PlanetSelectionAppScreen.class
    );

    public static ApplicationFactory<MainTabletMenu> STATS = register(
            Identifier.parse("stellaris:applications/stats"),
            new ApplicationFactory<>(
                    Component.translatable("application.stellaris.stats.name"),
                    Component.translatable("application.stellaris.stats.description"),
                    IdentifierUtils.id("icon/stats_app"),
                    IdentifierUtils.id("icon/stats_app_hover"),
                    StatsApplicationScreen::create
            ),
            StatsApplicationScreen.class
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

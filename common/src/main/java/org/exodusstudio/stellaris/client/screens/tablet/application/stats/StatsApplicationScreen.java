package org.exodusstudio.stellaris.client.screens.tablet.application.stats;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;

public class StatsApplicationScreen extends Screen {

    private final MainTabletScreen mainTabletScreen;

    private ScrollableContainer container;

    public StatsApplicationScreen(MainTabletScreen mainTabletScreen) {
        super(Component.literal("Stats"));
        this.mainTabletScreen = mainTabletScreen;
    }

    public static StatsApplicationScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {
        return new StatsApplicationScreen(menuHolder.mainTabletScreen());
    }

    @Override
    protected void init() {
        super.init();

        setupScrollableContainer();
    }

    private void setupScrollableContainer() {
        container = new ScrollableContainer(this.width / 2 - 136, this.height / 2 - 75, 272, 149, Component.empty());
    }

}

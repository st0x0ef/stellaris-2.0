package org.exodusstudio.stellaris.client.screens.tablet.application.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.client.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screens.components.wiki.WikiEntryButton;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Wiki Application Screen
 * This screen displays a list of wiki entries and allows navigation between them.
 */
public class WikiApplicationScreen extends Screen {


    /** Variables */
    public static ArrayList<WikiEntry> ENTRIES = new ArrayList<>();


    public static Map<ResourceLocation, EntryInfo> ENTRY_COMPONENTS = new HashMap<>();

    public ScrollableContainer scrollableContainer;

    public MainTabletScreen mainTabletScreen;

    public WikiApplicationScreen(MainTabletScreen mainTabletScreen) {
        super(Component.literal("Wiki"));
        this.mainTabletScreen = mainTabletScreen;
    }

    public static WikiApplicationScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {
        return new WikiApplicationScreen(menuHolder.mainTabletScreen());
    }

    @Override
    protected void init() {
        super.init();

        if(!ENTRIES.isEmpty()) {
            setupButtons();
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ApplicationScreen.BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.mainTabletScreen.getImageWidth(), this.mainTabletScreen.getImageHeight(), this.mainTabletScreen.getImageWidth(),this.mainTabletScreen.getImageHeight());
    }


    private void setupButtons() {
        setupScrollableContainer();
    }


    private void setupScrollableContainer() {

        this.scrollableContainer = new ScrollableContainer(this.getLeftPos() + 10, this.getTopPos() + 25, this.mainTabletScreen.getImageWidth() -20, this.mainTabletScreen.getImageHeight() - 40, Component.empty());

        int height = 5;
        for (WikiEntry entry : ENTRIES) {
            WikiEntryButton button = new WikiEntryButton((this.width / 2) - 60, this.scrollableContainer.getY() + height, 120, 20, entry,
                    button1 -> {
                        this.minecraft.setScreen(new WikiEntryScreen(this.mainTabletScreen, entry));
                    });
            this.scrollableContainer.addChild(this, button);
            height += 25;
        }

        scrollableContainer.setContentHeight(height*4);
        this.addRenderableWidget(scrollableContainer);
    }

    public static @Nullable EntryInfo getEntryInfo(ResourceLocation resourceLocation) {
        return ENTRY_COMPONENTS.getOrDefault(resourceLocation, null);
    }


    public int getLeftPos() {
        return this.mainTabletScreen.getLeftPos();
    }
    public int getTopPos() {
        return this.mainTabletScreen.getTopPos();
    }


}

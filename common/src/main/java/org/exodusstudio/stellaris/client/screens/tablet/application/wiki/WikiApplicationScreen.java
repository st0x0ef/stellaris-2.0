package org.exodusstudio.stellaris.client.screens.tablet.application.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.client.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
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

    public int currentPage = 0;

    public TexturedButton nextEntryButton;
    public TexturedButton prevEntryButton;
    public TexturedButton entryButton;


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
        this.prevEntryButton = new TexturedButton(this.getLeftPos() + 40, (this.height /2) - 10, 20, 20,
                WikiEntryScreen.BACK_ARROW,
                WikiEntryScreen.BACK_ARROW_HOVER,
                button -> previousEntry());

        this.nextEntryButton = new TexturedButton(this.getLeftPos() + 190, (this.height /2) - 10, 20, 20,
                WikiEntryScreen.NEXT_ARROW,
                WikiEntryScreen.NEXT_ARROW_HOVER,
                button -> nextEntry());

        this.addRenderableWidget(nextEntryButton);
        this.addRenderableWidget(prevEntryButton);

        setupScrollableContainer();

    }


    private void setupScrollableContainer() {

        this.entryButton = new TexturedButton((this.width / 2) - 45, (this.height /2) - 45, 90, 90,
                ENTRIES.get(currentPage).icon(),
                ENTRIES.get(currentPage).hoverIcon(),
                button -> {
                    this.minecraft.setScreen(new WikiEntryScreen(this.mainTabletScreen, ENTRIES.get(this.currentPage)));
                });

        this.scrollableContainer = new ScrollableContainer(this.getLeftPos() + 10, this.getTopPos() + 10, this.mainTabletScreen.getImageWidth() -20, this.mainTabletScreen.getImageHeight() - 20, this.entryButton);

        this.addWidget(entryButton);
        this.addRenderableWidget(scrollableContainer);
    }

    public void nextEntry() {
        if (ENTRIES.size() > 0) {
            currentPage++;
            if (currentPage >= ENTRIES.size()) {
                currentPage = 0;
            }
        }
    }

    public void previousEntry() {
        if (ENTRIES.size() > 0) {
            currentPage--;
            if (currentPage < 0) {
                currentPage = ENTRIES.size() - 1;
            }
        }
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

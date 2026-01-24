package org.exodusstudio.stellaris.client.screens.tablet.application.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.data.wiki.WikiPacks;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.components.wiki.WikiEntryButton;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.components.wiki.WikiInfoButton;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wiki Application Screen
 * This screen displays a list of wiki entries and allows navigation between them.
 */
public class WikiApplicationScreen extends Screen {

    /** Textures */
    public static final Identifier BACK_ARROW = IdentifierUtils.guiTexture("tablet/back_page");
    public static final Identifier BACK_ARROW_HOVER = IdentifierUtils.guiTexture("tablet/back_page_hover");
    public static final Identifier NEXT_ARROW = IdentifierUtils.guiTexture("tablet/next_page");
    public static final Identifier NEXT_ARROW_HOVER = IdentifierUtils.guiTexture("tablet/next_page_hover");
    public static final Identifier BUTTON_TEXTURE = IdentifierUtils.guiTexture("tablet/button");
    public static final Identifier BUTTON_HOVERED_TEXTURE = IdentifierUtils.guiTexture("tablet/button_click");


    /** Variables */


    public ScrollableContainer scrollableContainer;

    public MainTabletScreen mainTabletScreen;

    public WikiEntry currentEntry;

    //The list of the infos for the currentEntry
    public List<EntryInfo> INFOS;
    public int currentInfosPage = 0;

    public ArrayList<ArrayList<WikiInfoButton>> ENTRY_BUTTONS = new ArrayList<>();


    /** Navigation Buttons */
    public TexturedButton nextButton;
    public TexturedButton backButton;


    public WikiApplicationScreen(MainTabletScreen mainTabletScreen, @Nullable WikiEntry currentEntry) {
        super(Component.literal("Wiki"));
        this.mainTabletScreen = mainTabletScreen;
        this.currentEntry = currentEntry;

    }

    public static WikiApplicationScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {
        return new WikiApplicationScreen(menuHolder.mainTabletScreen(), null);
    }

    @Override
    protected void init() {
        super.init();

        if(!WikiPacks.ENTRIES.isEmpty()) {
            setupScrollableContainer();
            setupNavigationButtons();

            if(currentEntry != null) {
                switchEntry(currentEntry, this.currentInfosPage);
            }
        }
    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ApplicationScreen.BLANCK_BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.mainTabletScreen.getImageWidth(), this.mainTabletScreen.getImageHeight(), this.mainTabletScreen.getImageWidth(),this.mainTabletScreen.getImageHeight());
        updateNavigationButtons();

        if(currentEntry == null) {
            guiGraphics.drawCenteredString(this.font, "Select an entry", this.width / 2 + 40, this.height / 2 - 5, Utils.getMinecraftColor("white"));
        } else if (INFOS == null || INFOS.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "This entry is empty ;(", this.width / 2 + 40, this.height / 2 - 5, Utils.getMinecraftColor("white"));
        }
    }


    private void setupScrollableContainer() {
        this.scrollableContainer = new ScrollableContainer(this.getLeftPos() + 30, this.getTopPos() + 40, 100, this.mainTabletScreen.getImageHeight() - 70, Component.empty())
                .setBackground(IdentifierUtils.guiTexture("tablet/tablet_entries_background"));

        int height = 5;
        for (WikiEntry entry : WikiPacks.ENTRIES) {
            WikiEntryButton button = new WikiEntryButton(this.scrollableContainer.getX() + 5, this.scrollableContainer.getY() + height, 90, 20, entry,
                    button1 -> switchEntry(entry, 0)).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button"));
            this.scrollableContainer.addChild(this, button);
            height += 25;
        }

        scrollableContainer.setContentHeight(height);
        this.addRenderableWidget(scrollableContainer);
    }


    private void setupNavigationButtons() {
        int offsetX = 78;
        int offsetY = 30;

        this.nextButton = new TexturedButton(this.scrollableContainer.getRight() + offsetX + 28 , (this.height / 2) + offsetY, 20, 20,
                NEXT_ARROW, NEXT_ARROW_HOVER, button -> changePage(true));

        this.backButton = new TexturedButton(this.scrollableContainer.getRight() + offsetX - 28 , (this.height / 2) + offsetY, 20, 20,
                BACK_ARROW, BACK_ARROW_HOVER, button -> changePage(false));

        this.addRenderableWidget(nextButton);
        this.addRenderableWidget(backButton);
    }

    private void updateNavigationButtons() {
        this.nextButton.visible = currentInfosPage < ENTRY_BUTTONS.size() - 1 && currentEntry != null;
        this.backButton.visible = currentInfosPage > 0  && currentEntry != null;
    }


    public static @Nullable EntryInfo getEntryInfo(Identifier Identifier) {
        return WikiPacks.ENTRY_COMPONENTS.getOrDefault(Identifier, null);
    }

    /**
     * Collect the infos for the given entry
     * @param entry
     * @return List of EntryInfo for the given entry
     */
    public List<EntryInfo> getInfosForEntry(WikiEntry entry) {
        List<EntryInfo> infos = new ArrayList<>();

        WikiPacks.ENTRY_COMPONENTS.forEach((key, info) -> {
            if(info.entryId().equals(entry.id())) {
                infos.add(info);
            }
        });
        return infos;
    }


    /**
     * Change the current page of info buttons
     * @param next
     */
    public void changePage(boolean next) {
        if (next) {
            if (currentInfosPage == ENTRY_BUTTONS.size() - 1) {
                currentInfosPage = 0;
            }
            else {
                currentInfosPage++;
            }
        }
        else {
            if (currentInfosPage == 0) {
                currentInfosPage = ENTRY_BUTTONS.size() - 1;
            }
            else {
                currentInfosPage--;
            }
        }
        switchEntry(this.currentEntry, currentInfosPage);
    }


    /**
     * Used to switch the current entry and update the info buttons
     * @param entry
     */
    public void switchEntry(WikiEntry entry, int currentInfosPage) {
        this.currentEntry = entry;

        if(entry != null) {
            this.INFOS = getInfosForEntry(entry);
            this.currentInfosPage = currentInfosPage;
            showInfosButton(false);
            ENTRY_BUTTONS.clear();
            setupInfosButton();
        }
    }

    /**
     * Only show the info buttons of the current page
     * @param visible
     */
    public void showInfosButton(boolean visible) {
        if(!ENTRY_BUTTONS.isEmpty()) ENTRY_BUTTONS.get(currentInfosPage).forEach(button -> button.visible = visible);
    }

    /**
     * Set up the info buttons on the right side of the screen
     */
    private void setupInfosButton() {
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger column = new AtomicInteger(0);

        var PAGES_BUTTONS = new ArrayList<WikiInfoButton>();
        INFOS.forEach((infos) -> {
            WikiInfoButton entryButton = new WikiInfoButton(this.scrollableContainer.getRight() + 30 + (column.get() * 30), this.getTopPos() + 60 + (row.get() * 30), 20, 20, (b) -> this.minecraft.setScreen(new WikiEntryScreen(this.mainTabletScreen, WikiState.fromWiki(this), infos)), infos)
                    .tex(BUTTON_TEXTURE, BUTTON_HOVERED_TEXTURE);

            if (column.get() == 3) {
                column.set(0);
                row.getAndIncrement();
            }
            else {
                column.getAndIncrement();
            }
            PAGES_BUTTONS.add(entryButton);

            if (PAGES_BUTTONS.size() % 8 == 0) {
                column.set(0);
                row.set(0);
            }
            ClientUtils.addButtonToList(ENTRY_BUTTONS, entryButton, 8);
            entryButton.visible = false;
            this.addRenderableWidget(entryButton);
        });
        showInfosButton(true);
    }

    @Override
    public void resize(int width, int height) {
        this.mainTabletScreen.resize(width, height);
        super.resize(width, height);
    }

    public int getLeftPos() {
        return this.mainTabletScreen.getLeftPos();
    }

    public int getTopPos() {
        return this.mainTabletScreen.getTopPos();
    }



    public record WikiState(WikiEntry currentEntry, int currentInfoPage) {

        public WikiApplicationScreen toScreen(MainTabletScreen mainTabletScreen) {
            var screen = new WikiApplicationScreen(mainTabletScreen, currentEntry);
            screen.currentInfosPage = currentInfoPage;

            return screen;
        }

        public static WikiState fromWiki(WikiApplicationScreen screen) {
            return new WikiState(screen.currentEntry, screen.currentInfosPage);
        }

    }

}

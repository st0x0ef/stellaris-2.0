package org.exodusstudio.stellaris.client.screen.tablet.application.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screen.components.TexturedButton;
import org.exodusstudio.stellaris.client.screen.components.WikiButton;
import org.exodusstudio.stellaris.client.screen.components.WikiEntryWidget;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WikiEntryScreen extends ApplicationScreen {

    /** Textures */
    //Navigation
    public static final ResourceLocation SMALL_BACK_ARROW = ResourceLocationUtils.guiTexture("tablet/small_back_arrow");
    public static final ResourceLocation SMALL_NEXT_ARROW = ResourceLocationUtils.guiTexture("tablet/small_next_arrow");
    public static final ResourceLocation SMALL_BACK_ARROW_HOVER = ResourceLocationUtils.guiTexture("tablet/small_back_arrow_hover");
    public static final ResourceLocation SMALL_NEXT_ARROW_HOVER = ResourceLocationUtils.guiTexture("tablet/small_next_arrow_hover");
    public static final ResourceLocation BACK_ARROW = ResourceLocationUtils.guiTexture("tablet/back_page");
    public static final ResourceLocation BACK_ARROW_HOVER = ResourceLocationUtils.guiTexture("tablet/back_page_hover");
    public static final ResourceLocation NEXT_ARROW = ResourceLocationUtils.guiTexture("tablet/next_page");
    public static final ResourceLocation NEXT_ARROW_HOVER = ResourceLocationUtils.guiTexture("tablet/next_page_hover");

    public static final ResourceLocation MENU_BACKGROUND_LIGHT = ResourceLocationUtils.guiTexture("tablet/tablet_background_light");


    public static final ResourceLocation SMALL_HOME_BUTTON = ResourceLocationUtils.guiTexture("tablet/small_home_button");
    public static final ResourceLocation SMALL_HOME_BUTTON_HOVER = ResourceLocationUtils.guiTexture("tablet/small_home_button_hover");

    public static final ResourceLocation HOME_BUTTON = ResourceLocationUtils.guiTexture("tablet/main_page");
    public static final ResourceLocation HOME_BUTTON_HOVER = ResourceLocationUtils.guiTexture("tablet/main_page_hover");
    public static final ResourceLocation BUTTON_TEXTURE = ResourceLocationUtils.guiTexture("tablet/button");
    public static final ResourceLocation BUTTON_HOVERED_TEXTURE = ResourceLocationUtils.guiTexture("tablet/button_click");

    /** Constants */
    private final int imageHeight = 162;
    private final int imageWidth = 250;

    /** Variables */
    //Null is the main page
    public WikiEntry.EntryInfo currentPage = null;

    //Use for pagination
    public ArrayList<ArrayList<WikiButton>> ENTRY_BUTTONS = new ArrayList<>();
    public int currentEntryPage = 0;

    public List<WikiEntry.EntryInfo> ENTRIES;


    public WikiEntry currentEntry;

    public TexturedButton nextButton;
    public TexturedButton backButton;
    public TexturedButton homeButton;
    public WikiEntryWidget widget;


    protected WikiEntryScreen(MainTabletMenu menu, Inventory inventory, WikiEntry currentEntry) {
        super(menu, inventory, currentEntry.getTitle());
        this.currentEntry = currentEntry;
        this.ENTRIES = currentEntry.components();

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.widget.visible = currentPage != null;
        this.showEntryButton(currentPage == null);

    }

    @Override
    protected void init() {

        StringWidget titleWidget = new StringWidget(this.getLeftPos() + 15, this.getTopPos() + 10, Component.literal(this.title.getString()), this.font);
        this.addWidget(titleWidget);

        //Setup entry buttons
        setupEntryButtons();


        //Setup the main widget
        this.widget = new WikiEntryWidget(this.getLeftPos() + 15, this.getTopPos() + 40, 215, 96, null, this);
        this.widget.visible = false;
        this.addRenderableWidget(this.widget);

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        if (currentPage == null) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        } else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MENU_BACKGROUND_LIGHT, this.getLeftPos(), this.getTopPos(), 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        }
    }

    private void setupEntryButtons() {
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger column = new AtomicInteger(0);

        var PAGES_BUTTONS = new ArrayList<WikiButton>();
        ENTRIES.forEach((infos) -> {
            WikiButton entryButton = new WikiButton(this.getLeftPos() + 68 + (column.get() * 30), this.getTopPos() + 60 + (row.get() * 30), 20, 20, (b) -> changeInfo(infos), infos)
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
        showEntryButton(true);
    }

    public void changeInfo(WikiEntry.EntryInfo info) {
        Stellaris.LOG.error("Changing info to: {}", info.title());
        widget.setInfo(info);
        currentPage = info;
    }

    //Make all buttons invisible
    public void removeAllButtons() {
        for (ArrayList<WikiButton> entryButton : ENTRY_BUTTONS) {
            entryButton.forEach(button -> button.visible = false);
        }
    }

    //Show the buttons of the current page
    public void showEntryButton(boolean visible) {
        if(!ENTRY_BUTTONS.isEmpty()) ENTRY_BUTTONS.get(currentEntryPage).forEach(button -> button.visible = visible);
    }


    public void changePage(boolean next) {
        if (currentPage != null) {
            WikiEntry.EntryInfo info = getNextInfo(next);
            changeInfo(info);
            return;
        }

        if (next) {
            if (currentEntryPage == ENTRY_BUTTONS.size() - 1) {
                currentEntryPage = 0;
            }
            else {
                currentEntryPage++;
            }
        }
        else {
            if (currentEntryPage == 0) {
                currentEntryPage = ENTRY_BUTTONS.size() - 1;
            }
            else {
                currentEntryPage--;
            }
        }
        removeAllButtons();
        showEntryButton(true);
    }



    public WikiEntry.EntryInfo getNextInfo(boolean forward) {
        int currentIndex = -1;

        for (int i = 0; i < this.ENTRIES.size(); i++) {
            if (this.ENTRIES.get(i).equals(currentPage)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            if (!this.ENTRIES.isEmpty()) {
                return forward ? this.ENTRIES.getFirst() : this.ENTRIES.getLast();
            }
            else {
                return null;
            }
        }

        int nextIndex = forward ? (currentIndex + 1) % this.ENTRIES.size() : (currentIndex - 1 + this.ENTRIES.size()) % this.ENTRIES.size(); // The "+ infos.size()" is to avoid negative modulo results

        return this.ENTRIES.get(nextIndex);
    }



}

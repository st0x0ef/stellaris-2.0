package org.exodusstudio.stellaris.client.screens.tablet.application.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.client.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.components.WikiButton;
import org.exodusstudio.stellaris.client.screens.components.WikiInfos;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WikiEntryScreen
 * This screen displays a specific wiki entry and allows navigation between infos.
 */
public class WikiEntryScreen extends Screen {

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
    public EntryInfo currentPage = null;

    //Use for pagination
    public ArrayList<ArrayList<WikiButton>> ENTRY_BUTTONS = new ArrayList<>();
    public int currentEntryPage = 0;

    public List<EntryInfo> INFOS;


    public WikiEntry entry;

    public TexturedButton nextButton;
    public TexturedButton backButton;
    public TexturedButton homeButton;
    public WikiInfos widget;

    public MainTabletScreen tabletScreen;

    protected WikiEntryScreen(MainTabletScreen tabletScreen, WikiEntry entry) {
        super(entry.getTitle());
        this.entry = entry;
        this.tabletScreen = tabletScreen;

        //TODO : get all the infos from the entry
        this.INFOS = getInfosForEntry(entry);

    }

    public List<EntryInfo> getInfosForEntry(WikiEntry entry) {
        List<EntryInfo> infos = new ArrayList<>();

        WikiApplicationScreen.ENTRY_COMPONENTS.forEach((key, info) -> {
            if(info.entryId().equals(entry.id())) {
                infos.add(info);
            }
        });
        return infos;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        //this.widget.visible = currentPage != null;
        this.showEntryButton(currentPage == null);


        var title = this.currentPage == null ? this.entry.getTitle() : Component.literal(this.currentPage.title());
        guiGraphics.drawCenteredString(this.font, title, this.width / 2, this.getTopPos() + 25, Utils.getMinecraftColor("white"));

        boolean showNav = currentPage != null || ENTRY_BUTTONS.size() != 1;
        this.nextButton.visible = showNav;
        this.backButton.visible = showNav;
    }

    @Override
    protected void init() {
        super.init();

        //Setup entry buttons
        setupEntryButtons();

        setupNavigationButtons();


        //Setup the main widget
        this.widget = new WikiInfos(this.getLeftPos() + 33,  this.getTopPos() + 40,187, 85);
        this.addRenderableWidget(this.widget);

    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.tabletScreen.resize(minecraft, width, height);
        this.tabletScreen.init(minecraft, width, height);
        this.currentEntryPage = 0;
        super.resize(minecraft, width, height);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ApplicationScreen.BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

    }

    private void setupNavigationButtons() {
        this.nextButton = new TexturedButton(this.getLeftPos() + 194 - 28, (this.height / 2) + 48, 20, 20,
                NEXT_ARROW, NEXT_ARROW_HOVER, button -> changePage(true));

        this.backButton = new TexturedButton(this.getLeftPos() + 40 + 28, (this.height / 2) + 48, 20, 20,
                BACK_ARROW, BACK_ARROW_HOVER, button -> changePage(false));

        this.homeButton = new TexturedButton((this.width / 2) - 8, (this.height / 2) + 49, 16, 16,
                HOME_BUTTON, HOME_BUTTON_HOVER, button -> goBackScreen());

        this.addRenderableWidget(nextButton);
        this.addRenderableWidget(backButton);
        this.addRenderableWidget(homeButton);
    }

    private void setupEntryButtons() {
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger column = new AtomicInteger(0);

        var PAGES_BUTTONS = new ArrayList<WikiButton>();
        INFOS.forEach((infos) -> {
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

    /**
     * Change the current info displayed in the widget.
     * If info is null, it will display the main page.
     *
     * @param info The new info to display, or null for the main page.
     */
    public void changeInfo(@Nullable EntryInfo info) {
        currentPage = info;
        widget.refresh(this.currentPage);
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
            EntryInfo info = getNextInfo(next);
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



    public EntryInfo getNextInfo(boolean forward) {
        int currentIndex = -1;

        for (int i = 0; i < this.INFOS.size(); i++) {
            if (this.INFOS.get(i).equals(currentPage)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            if (!this.INFOS.isEmpty()) {
                return forward ? this.INFOS.getFirst() : this.INFOS.getLast();
            }
            else {
                return null;
            }
        }

        int nextIndex = forward ? (currentIndex + 1) % this.INFOS.size() : (currentIndex - 1 + this.INFOS.size()) % this.INFOS.size(); // The "+ infos.size()" is to avoid negative modulo results

        return this.INFOS.get(nextIndex);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            goBackScreen();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void goBackScreen() {
        if(this.currentPage != null) {
            this.changeInfo(null);
        } else {
            this.minecraft.setScreen(new WikiApplicationScreen(this.tabletScreen));
        }
    }

    public int getLeftPos() {
        return this.tabletScreen.getLeftPos();
    }

    public int getTopPos() {
        return this.tabletScreen.getTopPos();
    }

}

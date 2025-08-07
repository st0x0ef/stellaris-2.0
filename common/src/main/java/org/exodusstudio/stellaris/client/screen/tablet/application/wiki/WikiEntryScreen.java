package org.exodusstudio.stellaris.client.screen.tablet.application.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screen.components.TexturedButton;
import org.exodusstudio.stellaris.client.screen.components.WikiButton;
import org.exodusstudio.stellaris.client.screen.components.WikiEntryWidget;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public List<WikiEntry.EntryInfo> ENTRIES = new ArrayList<>();

    public int currentEntryPage = 0;

    public WikiEntry entry;

    public TexturedButton nextButton;
    public TexturedButton backButton;
    public TexturedButton homeButton;
    public WikiEntryWidget widget;


    protected WikiEntryScreen(MainTabletScreen mainTablet, WikiEntry entry) {
        super(mainTablet, entry.getTitle());
        this.entry = entry;
        this.ENTRIES = entry.components();
    }

    @Override
    protected void init() {

        this.widget = new WikiEntryWidget(this.getLeftPos() + 15, this.getTopPos() + 40, 215, 96, Component.literal(""), null, this);
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

}

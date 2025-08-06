package org.exodusstudio.stellaris.client.screen.tablet.application.wiki;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.client.screen.components.WikiButton;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.ArrayList;

public class WikiEntryScreen extends ApplicationScreen {

    /** Textures */
    public static final ResourceLocation MENU_BACKGROUND_LIGHT = ResourceLocationUtils.guiTexture("tablet/tablet_background_light");
    public static final ResourceLocation SMALL_BACK_ARROW = ResourceLocationUtils.guiTexture("tablet/small_back_arrow");
    public static final ResourceLocation SMALL_NEXT_ARROW = ResourceLocationUtils.guiTexture("tablet/small_next_arrow");
    public static final ResourceLocation SMALL_BACK_ARROW_HOVER = ResourceLocationUtils.guiTexture("tablet/small_back_arrow_hover");
    public static final ResourceLocation SMALL_NEXT_ARROW_HOVER = ResourceLocationUtils.guiTexture("tablet/small_next_arrow_hover");

    public static final ResourceLocation SMALL_HOME_BUTTON = ResourceLocationUtils.guiTexture("tablet/small_home_button");
    public static final ResourceLocation SMALL_HOME_BUTTON_HOVER = ResourceLocationUtils.guiTexture("tablet/small_home_button_hover");

    public static final ResourceLocation HOME_BUTTON = ResourceLocationUtils.guiTexture("tablet/main_page");
    public static final ResourceLocation HOME_BUTTON_HOVER = ResourceLocationUtils.guiTexture("tablet/main_page_hover");
    public static final ResourceLocation BACK_ARROW = ResourceLocationUtils.guiTexture("tablet/back_page");
    public static final ResourceLocation BACK_ARROW_HOVER = ResourceLocationUtils.guiTexture("tablet/back_page_hover");
    public static final ResourceLocation NEXT_ARROW = ResourceLocationUtils.guiTexture("tablet/next_page");
    public static final ResourceLocation NEXT_ARROW_HOVER = ResourceLocationUtils.guiTexture("tablet/next_page_hover");
    public static final ResourceLocation BUTTON_TEXTURE = ResourceLocationUtils.guiTexture("tablet/button");
    public static final ResourceLocation BUTTON_HOVERED_TEXTURE = ResourceLocationUtils.guiTexture("tablet/button_click");

    /** The Differents Buttons */
    private ArrayList<WikiButton> PAGES_BUTTONS = new ArrayList<>();
    public String currentPage = "main";


    protected WikiEntryScreen(MainTabletScreen mainTablet, Component title) {
        super(mainTablet, title);
    }


}

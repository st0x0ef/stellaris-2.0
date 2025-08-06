package org.exodusstudio.stellaris.client.screen.tablet.application.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screen.components.WikiButton;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WikiApplicationScreen extends ApplicationScreen {


    /** Variables */
    public Player player;

    public static Map<String, WikiEntry> ENTRIES = new HashMap<>();
    public static Map<ResourceLocation, WikiEntry.EntryComponents> ENTRY_COMPONENTS = new HashMap<>();



    public WikiApplicationScreen(MainTabletScreen mainTablet) {
        super(mainTablet, Component.literal("Wiki"));
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

}

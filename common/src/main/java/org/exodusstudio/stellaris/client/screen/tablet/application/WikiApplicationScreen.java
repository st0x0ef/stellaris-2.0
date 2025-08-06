package org.exodusstudio.stellaris.client.screen.tablet.application;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.client.data.wiki.TabletEntry;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;

import java.util.HashMap;
import java.util.Map;

public class WikiApplicationScreen extends ApplicationScreen {

    public Player player;

    public static Map<String, TabletEntry> ENTRIES = new HashMap<>();
    public static Map<ResourceLocation, TabletEntry.EntryComponents> ENTRY_COMPONENTS = new HashMap<>();

    protected WikiApplicationScreen(MainTabletScreen mainTablet) {
        super(mainTablet, Component.literal("Wiki"));
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }
}

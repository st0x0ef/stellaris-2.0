package org.exodusstudio.stellaris.client.screen.tablet.application.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screen.components.TexturedButton;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WikiApplicationScreen extends ApplicationScreen {


    /** Variables */
    public static ArrayList<WikiEntry> ENTRIES = new ArrayList<>();
    public static Map<ResourceLocation, WikiEntry.EntryInfo> ENTRY_COMPONENTS = new HashMap<>();

    public int currentPage = 0;

    public TexturedButton nextEntryButton;
    public TexturedButton prevEntryButton;

    public TexturedButton entryButton;

    public WikiApplicationScreen(MainTabletScreen mainTablet) {
        super(mainTablet, Component.literal("Wiki"));
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
    }

    private void setupButtons() {
        this.prevEntryButton = new TexturedButton(this.getLeftPos() + 40, (this.height /2) - 10, 20, 20,
                WikiEntryScreen.BACK_ARROW,
                WikiEntryScreen.BACK_ARROW_HOVER,
                button -> previousEntry());

        this.entryButton = new TexturedButton((this.width / 2) - 45, (this.height /2) - 30, 90, 60,
                ResourceLocationUtils.guiTexture("tablet/wiki/entry_button"),
                ResourceLocationUtils.guiTexture("tablet/wiki/entry_button"),
                button -> {
                    this.minecraft.setScreen(new WikiEntryScreen(this.mainTablet, ENTRIES.get(this.currentPage)));
                });

        this.nextEntryButton = new TexturedButton(this.getLeftPos() + 190, (this.height /2) - 10, 20, 20,
                WikiEntryScreen.NEXT_ARROW,
                WikiEntryScreen.NEXT_ARROW_HOVER,
                button -> nextEntry());


        this.addRenderableWidget(nextEntryButton);
        this.addRenderableWidget(entryButton);
        this.addRenderableWidget(prevEntryButton);
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

    public static @Nullable WikiEntry.EntryInfo getEntryInfo(ResourceLocation resourceLocation) {
        return ENTRY_COMPONENTS.getOrDefault(resourceLocation, null);
    }

}

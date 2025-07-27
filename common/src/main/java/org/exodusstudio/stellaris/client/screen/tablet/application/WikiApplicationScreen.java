package org.exodusstudio.stellaris.client.screen.tablet.application;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.common.menu.application.WikiApplicationMenu;
import org.jetbrains.annotations.NotNull;

public class WikiApplicationScreen extends AbstractApplicationScreen<WikiApplicationMenu>{

    public WikiApplicationScreen(WikiApplicationMenu menu, Inventory playerInventory) {
        super(menu, playerInventory);
    }

    @Override
    Component getName() {
        return null;
    }

    @Override
    public @NotNull Component getTitle() {
        return super.getTitle();
    }

    @Override
    Component getDescription() {
        return null;
    }

    @Override
    Screen getScreen() {
        return null;
    }

    @Override
    ResourceLocation getIconLocation() {
        return null;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

    }
}

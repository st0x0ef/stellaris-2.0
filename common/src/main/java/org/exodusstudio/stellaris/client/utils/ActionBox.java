package org.exodusstudio.stellaris.client.utils;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.client.screens.components.wiki.WikiInfosWidget;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A utility class mainly used for the wiki. Allow tooltip and page navigation.
 * @param x x coordinates of the action box
 * @param y y coordinated of the action box
 * @param width
 * @param height
 * @param hoverAction action to perform when the box is hovered
 * @param clickAction action to perform when the box is clicked
 * @param id
 */
public record ActionBox(int x, int y, int width, int height, @Nullable Consumer<RenderingInfo> hoverAction,
                        @Nullable Consumer<RenderingInfo> clickAction, String id) {

    public boolean isHovered(double mouseX, double mouseY, double finalHeight) {
        mouseY += finalHeight;
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void onClick(WikiInfosWidget infos) {
        if (clickAction != null) {
            clickAction.accept(new RenderingInfo(infos, this));
        }
    }

    public void onHover(WikiInfosWidget infos) {
        if (hoverAction != null) {
            hoverAction.accept(new RenderingInfo(infos, this));
        }
    }

    /**
     * ActionBox helper method
     */
    public void showTooltip(GuiGraphics guiGraphics, double mouseX, double mouseY, Component component) {
        guiGraphics.setTooltipForNextFrame(component, (int) mouseX, (int) mouseY);
    }

    public void changePage(WikiInfosWidget infos, String location) {
        var entryInfo = WikiApplicationScreen.getEntryInfo(ResourceLocation.parse(location));
        if (entryInfo != null && infos.info.id() != entryInfo.id()) {
            infos.refresh(entryInfo);
        }
    }


    public record RenderingInfo(WikiInfosWidget infoWidget, ActionBox actionBox) {}

}
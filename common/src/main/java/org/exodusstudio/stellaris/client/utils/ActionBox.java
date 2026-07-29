package org.exodusstudio.stellaris.client.utils;


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
public record ActionBox<T>(int x, int y, int width, int height, @Nullable Consumer<RenderingInfo<T>> hoverAction,
                        @Nullable Consumer<RenderingInfo<T>> clickAction, String id) {

    public boolean isHovered(double mouseX, double mouseY, double finalHeight) {
        mouseY += finalHeight;
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }


    public void onClick(T widget) {
        if (clickAction != null) {
            clickAction.accept(new RenderingInfo(widget, this));
        }
    }

    public void onHover(T widget) {
        if (hoverAction != null) {
            hoverAction.accept(new RenderingInfo(widget, this));
        }
    }


    public record RenderingInfo<T>(T widget, ActionBox actionBox) {}

}

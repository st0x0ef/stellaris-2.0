package org.exodusstudio.stellaris.client.utils;


import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * A utility class mainly used for the wiki. Allow tooltip and page navigation.
 * @param x x coordinates of the action box
 * @param y y coordinated of the action box
 * @param width
 * @param height
 * @param id
 */
public record ActionBox(int x, int y, int width, int height, String id, HashMap<String, String> data) {

    public boolean isHovered(double mouseX, double mouseY, double finalHeight) {
        mouseY += finalHeight;
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public @Nullable String getData(String key) {
        return data.get(key);
    }


}

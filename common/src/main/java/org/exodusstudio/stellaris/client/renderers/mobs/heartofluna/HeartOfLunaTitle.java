package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

public final class HeartOfLunaTitle {
    private static final String NAME = "HEART OF LUNA";

    public static void draw(GuiGraphicsExtractor g, float cx, float y, float scale, float opacity) {
        var font = Minecraft.getInstance().font;
        int width = font.width(NAME) + (NAME.length() - 1) * 2;
        int alpha = Math.round(255 * Mth.clamp(opacity, 0, 1));
        g.pose().pushMatrix();
        g.pose().translate(cx, y);
        g.pose().scale(scale, scale);
        int x = -width / 2;
        for (int i = 0; i < NAME.length(); i++) {
            String letter = NAME.substring(i, i + 1);
            float center = 1 - Math.abs(i - (NAME.length() - 1) / 2F) / (NAME.length() / 2F);
            int r = Math.round(202 + center * 43), green = Math.round(170 + center * 58), b = Math.round(227 + center * 25);
            g.text(font, letter, x, 1, (alpha << 24) | 0x261531);
            g.text(font, letter, x, 0, (alpha << 24) | (r << 16) | (green << 8) | b);
            x += font.width(letter) + 2;
        }
        for (int side : new int[]{-1, 1}) {
            int edge = side * (width / 2 + 9);
            g.fill(edge - 1, 3, edge + 2, 4, (alpha << 24) | 0xAE80C2);
            g.fill(edge, 2, edge + 1, 5, (alpha << 24) | 0xE3C1F0);
        }
        g.pose().popMatrix();
    }

    private HeartOfLunaTitle() {}
}

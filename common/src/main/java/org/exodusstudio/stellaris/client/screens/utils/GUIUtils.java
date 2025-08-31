package org.exodusstudio.stellaris.client.screens.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;

import java.util.Random;

public class GUIUtils {
    public static ClientTooltipComponent getMessageComponent(String text, String color) {
        return ClientTooltipComponent.create(Component.literal(text).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(getColorHexCode(color)))).getVisualOrderText());
    }

    public static int getColorHexCode(String colorName) {
        // Custom Colour Hex Code Support
        if (colorName.startsWith("#")) {
            try {
                return Integer.parseInt(colorName.substring(1), 16);
            } catch (NumberFormatException e) {
                return 0xFFFFFF; // Return white if invalid hex format
            }
        }

        return switch (colorName.toLowerCase()) {
            case "black" -> 0x000000;
            case "red" -> 0xFF0000;
            case "green" -> 0x008000;
            case "blue" -> 0x0000FF;
            case "yellow" -> 0xFFFF00;
            case "cyan" -> 0x00FFFF;
            case "magenta" -> 0xFF00FF;
            case "gray", "grey" -> 0x808080;
            case "maroon" -> 0x800000;
            case "olive" -> 0x808000;
            case "purple" -> 0x800080;
            case "teal" -> 0x008080;
            case "navy" -> 0x000080;
            case "orange" -> 0xFFA500;
            case "brown" -> 0xA52A2A;
            case "lime" -> 0x00FF00;
            case "pink" -> 0xFFC0CB;
            case "coral" -> 0xFF7F50;
            case "gold" -> 0xFFD700;
            case "silver" -> 0xC0C0C0;
            case "beige" -> 0xF5F5DC;
            case "lavender" -> 0xE6E6FA;
            case "turquoise" -> 0x40E0D0;
            case "salmon" -> 0xFA8072;
            case "khaki" -> 0xF0E68C;
            case "darkred" -> 0x8B0000;
            case "dark_red" -> 0x8B0000;
            case "rainbow" -> generateRandomHexColor();
            default -> 0xFFFFFF;
        };
    }

    public static int generateRandomHexColor() {
        Random random = new Random();
        return random.nextInt(0xFFFFFF + 1);
    }

    public static void renderEnergyGaugeTooltip(GuiGraphics graphics, GaugeWidget widget, int energyGeneratedPerTicks, int x, int y, Font font) {
        widget.renderTooltips(graphics, x, y, font, list -> list.add(ClientTooltipComponent.create(Component.translatable("gauge_text.stellaris.max_generation", energyGeneratedPerTicks).getVisualOrderText())));
    }
}

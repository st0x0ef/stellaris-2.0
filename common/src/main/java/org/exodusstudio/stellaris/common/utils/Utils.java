package org.exodusstudio.stellaris.common.utils;

import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3i;

import java.util.Random;

public class Utils {

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
            case "rainbow" -> Utils.generateRandomHexColor();
            default -> 0xFFFFFF;
        };
    }

    public static int generateRandomHexColor() {
        Random random = new Random();
        return random.nextInt(0xFFFFFF + 1);
    }

    public static int getMinecraftColor(String colorName) {
        int colorHex = getColorHexCode(colorName);
        Vec3 vector3i = hexToVec3(colorHex);
        return ARGB.color(vector3i);
    }

    public static Vec3 hexToVec3(int hex) {
        int r = (hex >> 16) & 0xFF;
        int g = (hex >> 8) & 0xFF;
        int b = hex & 0xFF;
        return new Vec3(r / 255.0, g / 255.0, b / 255.0);
    }


}

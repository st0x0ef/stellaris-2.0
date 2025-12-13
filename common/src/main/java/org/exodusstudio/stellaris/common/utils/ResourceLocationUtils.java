package org.exodusstudio.stellaris.common.utils;

import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.Stellaris;

public class ResourceLocationUtils {
    public static ResourceLocation texture(String path) {
        return id("textures/" + path + ".png");
    }

    public static ResourceLocation guiTexture(String path) {
        return texture("gui/" + path);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, path);
    }

    public static String key(ResourceLocation loc) {
        return loc.toString().split(":")[1];
    }

}

package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;

public class IdentifierUtils {
    public static Identifier texture(String path) {
        return id("textures/" + path + ".png");
    }

    public static Identifier guiTexture(String path) {
        return texture("gui/" + path);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, path);
    }

    public static <T> ResourceKey<T> resourceKey(ResourceKey<? extends Registry<T>> key, String path) {
        return ResourceKey.create(key, id(path));
    }
    public static String key(Identifier loc) {
        return loc.toString().split(":")[1];
    }

}

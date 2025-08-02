package org.exodusstudio.stellaris.utils;

import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.Stellaris;

public class ResourceLocationUtils {
    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, name);
    }
}

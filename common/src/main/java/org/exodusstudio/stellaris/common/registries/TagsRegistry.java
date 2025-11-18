package org.exodusstudio.stellaris.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class TagsRegistry {
    /** ITEMS */
    public static class ItemTags {
        public static final TagKey<Item> COAL_GENERATOR_FUEL = addTag("coal_generator_fuel");

        public static final TagKey<Item> CAN = addTag("can");

        public static TagKey<Item> addTag(String path) {
            return TagKey.create(Registries.ITEM, ResourceLocationUtils.id(path));
        }

        public static TagKey<Item> addTag(String path, String modid) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(path, modid));
        }

        public static TagKey<Item> addCTag(String path) {
            return addTag(path, "c");
        }
    }

}

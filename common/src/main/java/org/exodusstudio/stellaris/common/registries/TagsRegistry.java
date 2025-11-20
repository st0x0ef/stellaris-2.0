package org.exodusstudio.stellaris.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import org.exodusstudio.stellaris.Stellaris;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class TagsRegistry {
    /** ITEMS */
    public static class ItemTags {
        public static final TagKey<Item> COAL_GENERATOR_FUEL = addTag("coal_generator_fuel");

        public static final TagKey<Item> CAN = addTag("can");

        // Entities

        // Add entities that are corrosion immune if they spawn on Mars
        public static final TagKey<EntityType<?>> CORROSION_IMMUNE = addEntityTag("corrosion_immune");

        public static TagKey<Item> addTag(String path) {
            return TagKey.create(Registries.ITEM, ResourceLocationUtils.id(path));
        }

        public static TagKey<Item> addTag(String path, String modid) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(path, modid));
        }

        public static TagKey<Item> addCTag(String path) {
            return addTag(path, "c");
        }

        public static TagKey<EntityType<?>> addEntityTag(String path) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocationUtils.id(path));
        }
    }
    public static class BlockTags {
        public static final TagKey<Block> INFINIBURN_MOON = addTag("infiniburn_moon");

        public static TagKey<Block> addTag(String path) {
            return TagKey.create(Registries.BLOCK, ResourceLocationUtils.id(path));
        }

        public static TagKey<Block> addTag(String path, String modid) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(path, modid));
        }

        public static TagKey<Block> addCTag(String path) {
            return addTag(path, "c");
        }
    }

}

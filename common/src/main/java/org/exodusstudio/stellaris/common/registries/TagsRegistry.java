package org.exodusstudio.stellaris.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class TagsRegistry {
    /** ITEMS */
    public static class ItemTags {
        public static final TagKey<Item> COAL_GENERATOR_FUEL = addTag("coal_generator_fuel");

        public static final TagKey<Item> CAN = addTag("can");

        // Entities

        // Add entities that are corrosion immune if they spawn on Mars
        public static final TagKey<EntityType<?>> CORROSION_IMMUNE = addEntityTag("corrosion_immune");

        public static TagKey<Item> addTag(String path) {
            return TagKey.create(Registries.ITEM, IdentifierUtils.id(path));
        }

        public static TagKey<Item> addTag(String path, String modid) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(path, modid));
        }

        public static TagKey<Item> addCTag(String path) {
            return addTag(path, "c");
        }

        public static TagKey<EntityType<?>> addEntityTag(String path) {
            return TagKey.create(Registries.ENTITY_TYPE, IdentifierUtils.id(path));
        }
    }
    public static class BlockTags {
        public static final TagKey<Block> INFINIBURN_MOON = addTag("infiniburn_moon");

        public static TagKey<Block> addTag(String path) {
            return TagKey.create(Registries.BLOCK, IdentifierUtils.id(path));
        }

        public static TagKey<Block> addTag(String path, String modid) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(path, modid));
        }

        public static TagKey<Block> addCTag(String path) {
            return addTag(path, "c");
        }
    }

    public static class EntityTags {
        // Add entities that are corrosion immune if they spawn on Mars
        public static final TagKey<EntityType<?>> CORROSION_IMMUNE = addTag("corrosion_immune");
        public static final TagKey<EntityType<?>> INFECTION_IMMUNE = addTag("infection_immune");


        public static TagKey<EntityType<?>> addTag(String path) {
            return TagKey.create(Registries.ENTITY_TYPE, IdentifierUtils.id(path));
        }

        public static TagKey<EntityType<?>> addTag(String path, String modid) {
            return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(path, modid));
        }

        public static TagKey<EntityType<?>> addCTag(String path) {
            return addTag(path, "c");
        }
    }
}

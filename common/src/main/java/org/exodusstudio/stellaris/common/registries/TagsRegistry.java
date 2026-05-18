package org.exodusstudio.stellaris.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class TagsRegistry {
    /** ITEMS */
    public static class ItemTags {
        public static final TagKey<Item> COAL_GENERATOR_FUEL = addTag("coal_generator_fuel");
        public static final TagKey<Item> CAN = addTag("can");
        public static final TagKey<Item> SPACE_SUIT = addTag("space_suit");

        public static final TagKey<Item> TITANIUM_MATERIALS = addCTag("titanium_materials");

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
        public static final TagKey<Block> INCORRECT_FOR_TITANIUM_TOOL = addTag("incorrect_for_titanium_tool");
        public static final TagKey<Block> LUNAR_LOGS = addTag("lunar_logs");
        public static final TagKey<Block> MOON_STONE_ORE_REPLACEABLES = addTag("moon_stone_ore_replaceables");
        public static final TagKey<Block> FLUID_TANKS = addTag("fluid_tanks");
        public static final TagKey<Block> ANTENNA_REPLACEABLES = TagKey.create(Registries.BLOCK, IdentifierUtils.id("antenna_replaceable"));

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

    public static class BiomeTags {
        public static final TagKey<Biome> IS_MOON = addTag("is_moon");

        public static final TagKey<Biome> IS_MOON_WATER = addTag("is_moon_water");
        public static final TagKey<Biome> IS_MOON_FOREST = addTag("is_moon_forest");

        public static TagKey<Biome> addTag(String path) {
            return TagKey.create(Registries.BIOME, IdentifierUtils.id(path));
        }
    }

    public static class EntityTags {
        public static final TagKey<EntityType<?>> CORROSION_IMMUNE = addTag("corrosion_immune");
        public static final TagKey<EntityType<?>> INFECTION_IMMUNE = addTag("infection_immune");
        public static final TagKey<EntityType<?>> NO_OXYGEN_NEEDED = addTag("no_oxygen_needed");
        public static final TagKey<EntityType<?>> LUNAR_BOATS = addTag("lunar_boats");

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

    public static class FluidTags {
        public static final TagKey<Fluid> BLUE_LIQUID = addTag("blue_liquid");

        public static TagKey<Fluid> addTag(String path) {
            return TagKey.create(Registries.FLUID, IdentifierUtils.id(path));
        }

        public static TagKey<Fluid> addTag(String path, String modid) {
            return TagKey.create(Registries.FLUID, Identifier.fromNamespaceAndPath(path, modid));
        }

        public static TagKey<Fluid> addCTag(String path) {
            return addTag(path, "c");
        }
    }
}
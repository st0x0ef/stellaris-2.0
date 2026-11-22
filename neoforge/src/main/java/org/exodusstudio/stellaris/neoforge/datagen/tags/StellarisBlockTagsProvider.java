package org.exodusstudio.stellaris.neoforge.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

import java.util.concurrent.CompletableFuture;

public class StellarisBlockTagsProvider extends BlockTagsProvider {
    public StellarisBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Stellaris.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TagsRegistry.BlockTags.MARS_STONE_ORE_REPLACEABLE)
                .add(BlocksRegistry.MARS_STONE.block().get())
                .add(BlocksRegistry.MARS_RED_CANYON_STONE.block().get())
                .add(BlocksRegistry.MARS_RED_CLIFF_STONE.block().get());
        tag(TagsRegistry.BlockTags.ICE_BIOME_ORE_REPLACEABLE)
                .add(Blocks.CALCITE)
                .add(Blocks.PACKED_ICE)
                .add(Blocks.ICE)
                .addTag(TagsRegistry.BlockTags.MARS_STONE_ORE_REPLACEABLE);
        tag(TagsRegistry.BlockTags.LARGE_ICICLE_REPLACEABLE)
                .add(Blocks.CALCITE)
                .add(Blocks.PACKED_ICE)
                .addTag(TagsRegistry.BlockTags.MARS_STONE_ORE_REPLACEABLE);
        tag(TagsRegistry.BlockTags.TRIGGERS_AMBIENT_ICE_CRYSTAL_SOUNDS).add(Blocks.PACKED_ICE);
        tag(TagsRegistry.BlockTags.ICE_CREVASSE_REPLACEABLE)
                .addTag(TagsRegistry.BlockTags.ICE_BIOME_ORE_REPLACEABLE)
                .addTag(TagsRegistry.BlockTags.MARS_STONE_ORE_REPLACEABLE);
//        tag(TagsRegistry.BlockTags.BLOCKS_FLUID_FLOW)
//                .addTag(BlockTags.ALL_SIGNS)
//                .addTag(BlockTags.LEAVES)
//                .addTag(TagsRegistry.BlockTags.BLOCKS_MOTION_NO_LEAVES);
//        tag(TagsRegistry.BlockTags.BLOCKS_MOTION_NO_LEAVES)
//                .addTag(BlockTags.ALL_HANGING_SIGNS)
//                .addTag(BlockTags.CHAINS)
//                .addTag(BlockTags.BANNERS)
//                .addTag(BlockTags.SHULKER_BOXES)
//                .addTag(BlockTags.LANTERNS)
//                .addTag(BlockTags.CANDLE_CAKES)
//                .addTag(BlockTags.LIGHTNING_RODS)
//                .addTag(BlockTags.BEDS)
//                .addTag(BlockTags.WOOL)
//                .addTag(BlockTags.BARS)
//                .addTag(BlockTags.CAULDRONS)
//                .addTag(BlockTags.TERRACOTTA)
//                .addTag(Tags.Blocks.GLAZED_TERRACOTTAS)
//                .addTag(Tags.Blocks.CONCRETES)
//                .addTag(BlockTags.CONCRETE_POWDER)
//                .addTag(BlockTags.COPPER)
//                .addTag(BlockTags.COPPER_CHESTS)
//                .addTag(BlockTags.PRESSURE_PLATES)
//                .addTag(BlockTags.FENCES)
//                .addTag(BlockTags.FENCE_GATES)
//                .addTag(BlockTags.WALLS)
//                .addTag(BlockTags.CORAL_BLOCKS)
//                .addTag(BlockTags.PLANKS)
//                .addTag(BlockTags.LOGS)
//                .addTag(BlockTags.BAMBOO_BLOCKS)
//                .addTag(BlockTags.WOODEN_SHELVES)
//                .addTag(BlockTags.STAIRS)
//                .addTag(BlockTags.SLABS)
//                .addTag(BlockTags.DOORS)
//                .addTag(BlockTags.TRAPDOORS)
//                .addTag(Tags.Blocks.ORES)
//                .addTag(TagsRegistry.BlockTags.SPELEOTHEMS)
//                .addTag(BlockTags.GRASS_BLOCKS)
//                .addTag(BlockTags.DIRT)
//                .addTag(BlockTags.SAND)
//                .addTag(BlockTags.ICE)
//                .addTag(BlockTags.STONE_BRICKS)
//                .addTag(BlockTags.ANVIL)
//                .addTag(Tags.Blocks.GLASS_PANES)
//                .addTag(Tags.Blocks.GLASS_BLOCKS)
//                .addTag(BlockTags.COPPER)
//                .addTag(BlockTags.CORALS)
//                .add(Blocks.PISTON)
//                .add(Blocks.STICKY_PISTON)
//                .add(Blocks.PISTON_HEAD)
//                .add(Blocks.MOVING_PISTON)
//                .add(Blocks.CAKE, Blocks.TURTLE_EGG, Blocks.DRIED_GHAST, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.CONDUIT, Blocks.BAMBOO, Blocks.BELL, Blocks.AMETHYST_CLUSTER, Blocks.LARGE_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD, Blocks.SMALL_AMETHYST_BUD, Blocks.SCULK_VEIN, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.COBBLESTONE, Blocks.BAMBOO_MOSAIC, Blocks.BEDROCK, Blocks.GRAVEL, Blocks.SUSPICIOUS_GRAVEL, Blocks.MANGROVE_ROOTS, Blocks.MUDDY_MANGROVE_ROOTS, Blocks.SPONGE, Blocks.WET_SPONGE, Blocks.GLASS, Blocks.LAPIS_BLOCK, Blocks.DISPENSER, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.NOTE_BLOCK, Blocks.STICKY_PISTON, Blocks.PISTON, Blocks.GOLD_BLOCK, Blocks.IRON_BLOCK, Blocks.BRICKS, Blocks.TNT, Blocks.BOOKSHELF, Blocks.CHISELED_BOOKSHELF, Blocks.MOSSY_COBBLESTONE, Blocks.OBSIDIAN, Blocks.SPAWNER, Blocks.CREAKING_HEART, Blocks.CHEST, Blocks.DIAMOND_BLOCK, Blocks.CRAFTING_TABLE, Blocks.FARMLAND, Blocks.FURNACE, Blocks.SNOW_BLOCK, Blocks.CACTUS, Blocks.CLAY, Blocks.JUKEBOX, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.BASALT, Blocks.POLISHED_BASALT, Blocks.GLOWSTONE, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.PACKED_MUD, Blocks.MUD_BRICKS, Blocks.INFESTED_STONE, Blocks.INFESTED_COBBLESTONE, Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.MUSHROOM_STEM, Blocks.GLASS_PANE, Blocks.PUMPKIN, Blocks.MELON, Blocks.RESIN_BLOCK, Blocks.RESIN_BRICKS, Blocks.CHISELED_RESIN_BRICKS, Blocks.NETHER_BRICKS, Blocks.ENCHANTING_TABLE, Blocks.BREWING_STAND, Blocks.END_PORTAL_FRAME, Blocks.END_STONE, Blocks.DRAGON_EGG, Blocks.REDSTONE_LAMP, Blocks.ENDER_CHEST, Blocks.EMERALD_BLOCK, Blocks.COMMAND_BLOCK, Blocks.BEACON, Blocks.TRAPPED_CHEST, Blocks.DAYLIGHT_DETECTOR, Blocks.REDSTONE_BLOCK, Blocks.HOPPER, Blocks.QUARTZ_BLOCK, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR, Blocks.DROPPER, Blocks.SLIME_BLOCK, Blocks.BARRIER, Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.DARK_PRISMARINE, Blocks.SEA_LANTERN, Blocks.HAY_BLOCK, Blocks.COAL_BLOCK, Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR, Blocks.END_STONE_BRICKS, Blocks.DIRT_PATH, Blocks.REPEATING_COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.MAGMA_BLOCK, Blocks.NETHER_WART_BLOCK, Blocks.RED_NETHER_BRICKS, Blocks.BONE_BLOCK, Blocks.OBSERVER, Blocks.DRIED_KELP_BLOCK, Blocks.SNIFFER_EGG, Blocks.LOOM, Blocks.BARREL, Blocks.SMOKER, Blocks.BLAST_FURNACE, Blocks.CARTOGRAPHY_TABLE, Blocks.FLETCHING_TABLE, Blocks.GRINDSTONE, Blocks.LECTERN, Blocks.SMITHING_TABLE, Blocks.STONECUTTER, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Blocks.WARPED_NYLIUM, Blocks.WARPED_WART_BLOCK, Blocks.CRIMSON_NYLIUM, Blocks.SHROOMLIGHT, Blocks.STRUCTURE_BLOCK, Blocks.JIGSAW, Blocks.TEST_BLOCK, Blocks.TEST_INSTANCE_BLOCK, Blocks.COMPOSTER, Blocks.TARGET, Blocks.BEE_NEST, Blocks.BEEHIVE, Blocks.HONEY_BLOCK, Blocks.HONEYCOMB_BLOCK, Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS, Blocks.CRYING_OBSIDIAN, Blocks.RESPAWN_ANCHOR, Blocks.LODESTONE, Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.GILDED_BLACKSTONE, Blocks.CHISELED_NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS, Blocks.QUARTZ_BRICKS, Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST, Blocks.TUFF, Blocks.POLISHED_TUFF, Blocks.CHISELED_TUFF, Blocks.TUFF_BRICKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.CALCITE, Blocks.TINTED_GLASS, Blocks.SCULK_SENSOR, Blocks.CALIBRATED_SCULK_SENSOR, Blocks.SCULK, Blocks.SCULK_CATALYST, Blocks.SCULK_SHRIEKER, Blocks.DRIPSTONE_BLOCK, Blocks.MOSS_BLOCK, Blocks.MUD, Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS, Blocks.CHISELED_DEEPSLATE, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_TILES, Blocks.INFESTED_DEEPSLATE, Blocks.SMOOTH_BASALT, Blocks.RAW_IRON_BLOCK, Blocks.RAW_COPPER_BLOCK, Blocks.RAW_GOLD_BLOCK, Blocks.OCHRE_FROGLIGHT, Blocks.VERDANT_FROGLIGHT, Blocks.PEARLESCENT_FROGLIGHT, Blocks.REINFORCED_DEEPSLATE, Blocks.DECORATED_POT, Blocks.CRAFTER, Blocks.TRIAL_SPAWNER, Blocks.VAULT, Blocks.PALE_MOSS_BLOCK)
//                .add(BlocksRegistry.METEORITE.block().get(), BlocksRegistry.TITANIUM_ORE.block().get(), BlocksRegistry.DEEPSLATE_TITANIUM_ORE.block().get(), BlocksRegistry.RAW_TITANIUM_BLOCK.block().get(), BlocksRegistry.TITANIUM_BLOCK.block().get(), BlocksRegistry.TITANIUM_PILLAR.block().get(), BlocksRegistry.TITANIUM_PLATING_BLOCK.block().get(), BlocksRegistry.IRON_PLATING_BLOCK.block().get(), BlocksRegistry.IRON_PLATING_SLAB.block().get(), BlocksRegistry.IRON_PLATING_STAIRS.block().get(), BlocksRegistry.IRON_PILLAR.block().get(), BlocksRegistry.NOVITE_ORE.block().get(), BlocksRegistry.NOVITE_BLOCK.block().get(), BlocksRegistry.RAW_NOVITE_BLOCK.block().get(), BlocksRegistry.MOON_SAND.block().get(), BlocksRegistry.MOON_STONE.block().get(), BlocksRegistry.MOON_COBBLESTONE.block().get(), BlocksRegistry.MOON_DEEPSLATE.block().get(), BlocksRegistry.MOON_STONE_DUST.block().get(), BlocksRegistry.MOON_STONE_BRICKS.block().get(), BlocksRegistry.MOON_CRACKED_STONE_BRICKS.block().get(), BlocksRegistry.MOON_STONE_PILLAR.block().get(), BlocksRegistry.POLISHED_MOON_STONE.block().get(), BlocksRegistry.CHISELED_MOON_STONE.block().get(), BlocksRegistry.MOON_PILLAR.block().get(), BlocksRegistry.MOON_TITANIUM_ORE.block().get(), BlocksRegistry.MOON_DESH_ORE.block().get(), BlocksRegistry.MOON_STONE_IRON_ORE.block().get(), BlocksRegistry.DESH_BLOCK.block().get(), BlocksRegistry.RAW_DESH_BLOCK.block().get(), BlocksRegistry.DESH_PLATING_BLOCK.block().get(), BlocksRegistry.DESH_PILLAR.block().get(), BlocksRegistry.LUNAR_STONED_WOOD_LOG.block().get(), BlocksRegistry.LUNAR_DIRT.block().get(), BlocksRegistry.LUNAR_ROCK.block().get(), BlocksRegistry.LUNAR_LOG.block().get(), BlocksRegistry.LUNAR_WOOD.block().get(), BlocksRegistry.STRIPPED_LUNAR_LOG.block().get(), BlocksRegistry.STRIPPED_LUNAR_WOOD.block().get(), BlocksRegistry.LUNAR_PLANKS.block().get(), BlocksRegistry.PACKED_ICE_BRICKS.block().get(), BlocksRegistry.PACKED_ICE_PILLAR.block().get(), BlocksRegistry.POLISHED_PACKED_ICE.block().get(), BlocksRegistry.MARS_RED_CANYON_STONE.block().get(), BlocksRegistry.MARS_RED_CLIFF_STONE.block().get(), BlocksRegistry.MARS_RED_STONE_BRICKS_PILLAR.block().get(), BlocksRegistry.MARS_SAND.block().get(), BlocksRegistry.MARS_STONE.block().get(), BlocksRegistry.MARS_COBBLESTONE.block().get(), BlocksRegistry.MARS_IRON_ORE.block().get(), BlocksRegistry.MARS_WEATHERED_STONE.block().get(), BlocksRegistry.MARS_WEATHERED_SAND.block().get(), BlocksRegistry.MARS_REGOLITH.block().get(), BlocksRegistry.MARS_REGOLITH_BRICKS.block().get(), BlocksRegistry.MARS_STONE_BRICKS.block().get(), BlocksRegistry.CRACKED_MARS_STONE_BRICKS.block().get(), BlocksRegistry.CHISELED_MARS_STONE_BRICKS.block().get(), BlocksRegistry.POLISHED_MARS_STONE.block().get(), BlocksRegistry.MARS_PILLAR.block().get(), BlocksRegistry.MARS_THARSITE_ORE.block().get());

        tag(TagsRegistry.BlockTags.SPELEOTHEMS)
                .add(Blocks.POINTED_DRIPSTONE)
                .add(BlocksRegistry.ICICLE.block().get());

        tag(TagsRegistry.BlockTags.MELTS_ICICLE_ABOVE)
                .add(Blocks.MAGMA_BLOCK);
    }
}

package org.exodusstudio.stellaris.neoforge.datagen.loottable;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

import java.util.Set;

public class StellarisBlockLootTable extends BlockLootSubProvider {
    private final ResourceManager resourceManager;
    public StellarisBlockLootTable(HolderLookup.Provider registries, ResourceManager resourceManager) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        this.resourceManager = resourceManager;
    }

    @Override
    protected void generate() {
        add(BlocksRegistry.MARS_STONE.block().get(), block -> this.createSingleItemTableWithSilkTouch(block, BlocksRegistry.MARS_COBBLESTONE.block().get()));
        dropSelf(BlocksRegistry.MARS_SAND.block().get());
        this.add(BlocksRegistry.MARS_STONE_IRON_ORE.block().get(), (block) -> this.createOreDrop(block, Items.RAW_IRON));
        dropSelf(BlocksRegistry.MARS_RED_CANYON_STONE.block().get());
        dropSelf(BlocksRegistry.MARS_RED_CLIFF_STONE.block().get());
        dropSelf(BlocksRegistry.MARS_RED_STONE_BRICKS_PILLAR.block().get());
        dropSelf(BlocksRegistry.MARS_REGOLITH.block().get());
        dropSelf(BlocksRegistry.MARS_REGOLITH_BRICKS.block().get());
        dropSelf(BlocksRegistry.MARS_WEATHERED_SAND.block().get());
        dropSelf(BlocksRegistry.MARS_WEATHERED_STONE.block().get());
        dropSelf(BlocksRegistry.DUSTBLOOM.block().get());
        dropSelf(BlocksRegistry.ECLIPSE_TULIP.block().get());
        this.add(BlocksRegistry.STARLIGHT_BUSH.block().get(), (block) -> this.createSinglePropConditionTable(block, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
        dropSelf(BlocksRegistry.MARS_COBBLESTONE.block().get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.listElements().filter(holder -> holder.getKey().identifier().getNamespace().equals(Stellaris.MOD_ID) && resourceManager.getResource(holder.getKey().identifier().withPrefix("loot_table/blocks/").withSuffix(".json")).isEmpty()).map(Holder::value)::iterator;
    }
}

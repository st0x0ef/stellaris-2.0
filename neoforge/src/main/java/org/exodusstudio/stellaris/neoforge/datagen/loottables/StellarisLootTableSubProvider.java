package org.exodusstudio.stellaris.neoforge.datagen.loottables;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StellarisLootTableSubProvider extends BlockLootSubProvider {
    public StellarisLootTableSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }



    @Override
    protected void generate() {
        this.dropSelf(BlocksRegistry.MOON_ROCK.block().get());
        this.dropSelf(BlocksRegistry.MOON_SAND.block().get());
        this.dropSelf(BlocksRegistry.MOON_STONE.block().get());
        this.dropSelf(BlocksRegistry.MOON_STONE_IRON_ORE.block().get());
        this.dropSelf(BlocksRegistry.POLISHED_MOON_STONE.block().get());
        this.dropSelf(BlocksRegistry.LUNAR_STONED_WOOD_LOG.block().get());
        this.dropSelf(BlocksRegistry.ICED_MAGMA_BLOCK.block().get());
        this.dropSelf(BlocksRegistry.PACKED_ICE_BRICKS.block().get());
        this.dropSelf(BlocksRegistry.PACKED_ICE_PILLAR.block().get());
        this.dropSelf(BlocksRegistry.POLISHED_PACKED_ICE.block().get());
        this.dropSelf(BlocksRegistry.SOLAR_PANEL.block().get());
        this.dropSelf(BlocksRegistry.COAL_GENERATOR.block().get());
        this.dropSelf(BlocksRegistry.SOLAR_PANEL.block().get());
        this.dropSelf(BlocksRegistry.COAL_GENERATOR.block().get());
        this.dropSelf(BlocksRegistry.VACUUMATOR.block().get());
        this.dropSelf(BlocksRegistry.POWER_BANK_T1.block().get());
        this.dropSelf(BlocksRegistry.CABLE_T1.block().get());
        this.dropSelf(BlocksRegistry.DESH_BLOCK.block().get());

    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> blocks = new ArrayList<>();
        BlocksRegistry.BLOCKS.iterator().forEachRemaining(supplier -> blocks.add(supplier.get()));
        return blocks;
    }
}

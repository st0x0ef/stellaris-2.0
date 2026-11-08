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
                .add(BlocksRegistry.MARS_RED_CLIFF_STONE.block().get())
                .add(BlocksRegistry.MARS_SAND.block().get());
        tag(TagsRegistry.BlockTags.ICE_CAVE_ORE_REPLACEABLE)
                .add(Blocks.CALCITE)
                .add(Blocks.PACKED_ICE)
                .addTag(TagsRegistry.BlockTags.MARS_STONE_ORE_REPLACEABLE);
        tag(TagsRegistry.BlockTags.LARGE_ICICLE_REPLACEABLE)
                .add(Blocks.CALCITE)
                .add(Blocks.PACKED_ICE)
                .addTag(TagsRegistry.BlockTags.MARS_STONE_ORE_REPLACEABLE);
    }
}

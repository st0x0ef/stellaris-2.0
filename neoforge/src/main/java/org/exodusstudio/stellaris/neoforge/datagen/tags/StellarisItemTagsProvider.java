package org.exodusstudio.stellaris.neoforge.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import org.exodusstudio.stellaris.Stellaris;

import java.util.concurrent.CompletableFuture;

public class StellarisItemTagsProvider extends BlockTagCopyingItemTagProvider {


    public StellarisItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, Stellaris.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}

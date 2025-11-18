package org.exodusstudio.stellaris.neoforge.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import org.exodusstudio.stellaris.Stellaris;

import java.util.concurrent.CompletableFuture;

public class StellarisItemTagsProvider extends BlockTagCopyingItemTagProvider {


    public StellarisItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> providerCompletableFuture, CompletableFuture<TagLookup<Block>> tagLookupCompletableFuture) {
        super(packOutput, providerCompletableFuture, tagLookupCompletableFuture, Stellaris.MOD_ID);

    }

    /*
        - example for items
        this.tag(MY_TAG)
        .add(...Items) // one or more
        .addTags(...Item_TAG) //One or more
        .add(TagEntry.optionalElement(ResourceLocation)) // Optional
        .addOptionalTags(...TagKey) // Optional Tags one or more
        .replace() // for replace = true
        .remove(..Items) // for remove

        copy tag from a item tag
        this.copy(EXAMPLE_Item_TAG, EXAMPLE_ITEM_TAG);

     */
    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}

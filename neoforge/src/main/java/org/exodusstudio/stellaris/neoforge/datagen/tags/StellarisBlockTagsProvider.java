package org.exodusstudio.stellaris.neoforge.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

import java.util.concurrent.CompletableFuture;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

public class StellarisBlockTagsProvider extends BlockTagsProvider {


    public StellarisBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MOD_ID);
    }

    /*
        - example for items
        this.tag(MY_TAG)
        .add(...Blocks) // one or more
        .addTags(...BLOCK_TAG) //One or more
        .add(TagEntry.optionalElement(ResourceLocation)) // Optional
        .addOptionalTags(...TagKey) // Optional Tags one or more
        .replace() // for replace = true
        .remove(..Blocks) // for remove

        copy tag from a block tag
        this.copy(EXAMPLE_BLOCK_TAG, EXAMPLE_ITEM_TAG);

     */
    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BlocksRegistry.MOON_ROCK.block().get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(BlocksRegistry.MOON_ROCK.block().get());
    }
}

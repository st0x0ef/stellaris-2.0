package org.exodusstudio.stellaris.neoforge.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

import java.util.concurrent.CompletableFuture;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

public class NeoForgeBlockTagsProvider extends BlockTagsProvider {


    public NeoForgeBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MOD_ID);
    }

    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BlocksRegistry.MOON_ROCK.block().get());

        tag(BlockTags.NEEDS_IRON_TOOL).add(
                BlocksRegistry.MOON_ROCK.block().get());
    }
}

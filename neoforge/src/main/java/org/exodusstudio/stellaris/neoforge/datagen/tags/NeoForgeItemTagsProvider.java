package org.exodusstudio.stellaris.neoforge.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;

import java.util.concurrent.CompletableFuture;

public class NeoForgeItemTagsProvider extends ItemTagsProvider {


    public NeoForgeItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    @Override
        protected void addTags(HolderLookup.Provider provider) {
        // example
            tag(ItemTags.RAILS).add(
                    ItemsRegistry.DESH_INGOT.get()
            );

            // custom
            // tag(ModItemTags.YOUR_CUSTOM_TAG).add(
            //     ItemsRegistry.YOUR_ITEM.get()
            // );
        }
    }

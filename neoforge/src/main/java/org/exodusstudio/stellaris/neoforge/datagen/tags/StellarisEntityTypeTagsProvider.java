package org.exodusstudio.stellaris.neoforge.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

import java.util.concurrent.CompletableFuture;

public class StellarisEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public StellarisEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Stellaris.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(TagsRegistry.EntityTags.DOES_NOT_BREAK_BRITTLE_ICE)
                .add(EntityType.STRAY);
    }
}

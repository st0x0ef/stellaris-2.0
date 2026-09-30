package org.exodusstudio.stellaris.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.neoforge.datagen.worldgen.StellarisFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class StellarisDatapackProvide extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, StellarisFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, StellarisFeatures::bootstrapPlaced)
            ;


    public StellarisDatapackProvide(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Stellaris.MOD_ID));
    }

    
}

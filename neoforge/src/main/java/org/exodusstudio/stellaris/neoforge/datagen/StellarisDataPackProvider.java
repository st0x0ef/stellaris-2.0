package org.exodusstudio.stellaris.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.exodusstudio.stellaris.Stellaris;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class StellarisDataPackProvider extends DatapackBuiltinEntriesProvider {
    public StellarisDataPackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Stellaris.MOD_ID));
    }

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();
            //.add(Registries.TRIM_MATERIAL, ModTrimMatirials::boostrap)
            //.add(Registries.ENCHANTMENT, ModEnchantments::boostrap);
}

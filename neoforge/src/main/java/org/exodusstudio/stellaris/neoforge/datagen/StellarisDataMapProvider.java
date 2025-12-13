package org.exodusstudio.stellaris.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;

import java.util.concurrent.CompletableFuture;

public class StellarisDataMapProvider extends DataMapProvider {
    protected StellarisDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    public void gather(HolderLookup.Provider provider) {
        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(ItemsRegistry.RAW_DESH, new FurnaceFuel(1200), false);

    }
}

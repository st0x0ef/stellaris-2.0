package org.exodusstudio.stellaris.neoforge.datagen;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.exodusstudio.stellaris.neoforge.datagen.loottables.StellarisLootTableSubProvider;
import org.exodusstudio.stellaris.neoforge.datagen.tags.StellarisBlockTagsProvider;
import org.exodusstudio.stellaris.neoforge.datagen.tags.StellarisItemTagsProvider;

import java.util.List;
import java.util.Set;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class StellarisDataGeneration {

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
//        DataGenerator generator = event.getGenerator();
//        PackOutput packOutput = generator.getPackOutput();
//        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
//
//        //LootTable
//        generator.addProvider(true , new LootTableProvider(packOutput, Collections.emptySet(),
//                List.of(new LootTableProvider.SubProviderEntry(StellarisBlockLootTables::new, LootContextParamSets.BLOCK)), lookupProvider));
//        generator.addProvider(true, new StellarisRecipeProvider.Runner(packOutput, lookupProvider));
//
//
//        //Tags
//        BlockTagsProvider blockTagsProvider = new NeoForgeBlockTagsProvider(packOutput, lookupProvider);
//        generator.addProvider(true, blockTagsProvider);
//        generator.addProvider(true, new NeoForgeItemTagsProvider(packOutput, lookupProvider, MOD_ID));
//
//        //Models
//        generator.addProvider(true, new StellarisModelProvider(packOutput));
//
//        // DataPacks
//        generator.addProvider(true, new StellarisDataPackProvider(packOutput, lookupProvider));
//
//        generator.addProvider(true, new StellarisGlobalLootModifierProvider(packOutput, lookupProvider));

        // Recipes (Can make multiple classes for different recipe according to new system)
        event.createProvider(StellarisRecipeProvider.Runner::new);
        //Loot Tables
        event.createProvider((output,lookupProvider) -> new LootTableProvider(
                output,
                Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(
                                StellarisLootTableSubProvider::new,
                                LootContextParamSets.BLOCK
                        )
                ),
                lookupProvider
        ));
        //Tags
        event.createBlockAndItemTags(StellarisBlockTagsProvider::new, StellarisItemTagsProvider::new);
        // Models
        // event.createProvider(StellarisModelProvider::new);
        // Datapacks
        event.createProvider(StellarisDataPackProvider::new);
    }


    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event) {
//        DataGenerator generator = event.getGenerator();
//        PackOutput packOutput = generator.getPackOutput();
//        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
//
//
//        //LootTable
//        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
//                List.of(new LootTableProvider.SubProviderEntry(StellarisBlockLootTables::new, LootContextParamSets.BLOCK)), lookupProvider));
//
//
//        //Tags
//        BlockTagsProvider blockTagsProvider = new NeoForgeBlockTagsProvider(packOutput, lookupProvider);
//        generator.addProvider(true, blockTagsProvider);
//        generator.addProvider(true, new NeoForgeItemTagsProvider(packOutput, lookupProvider, MOD_ID));
//
//        //DataMap
//        generator.addProvider(true, new StellarisDataMapProvider(packOutput, lookupProvider));
//
//
//        //Recipes
//        generator.addProvider(true, new StellarisRecipeProvider.Runner(packOutput, lookupProvider));
//
//
//        //DataPack
//        generator.addProvider(true, new StellarisDataPackProvider(packOutput, lookupProvider));
    }
}

package org.exodusstudio.stellaris.neoforge.datagen;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.neoforge.datagen.loottable.StellarisBlockLootTable;
import org.exodusstudio.stellaris.neoforge.datagen.tags.StellarisBlockTagsProvider;
import org.exodusstudio.stellaris.neoforge.datagen.tags.StellarisItemTagsProvider;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = Stellaris.MOD_ID)
public class StellarisDatagen {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {

        ResourceManager clientResourceManager = event.getResourceManager(PackType.CLIENT_RESOURCES);
        ResourceManager serverResourceManager = event.getResourceManager(PackType.SERVER_DATA);
        event.createProvider(packOutput -> new StellarisModelGenerator(packOutput, clientResourceManager));
        event.createProvider(StellarisDatapackProvide::new);
        event.createBlockAndItemTags(StellarisBlockTagsProvider::new, StellarisItemTagsProvider::new);
        event.createProvider(((output, lookupProvider) -> new LootTableProvider(
                output,
                Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(registries -> new StellarisBlockLootTable(registries, serverResourceManager), LootContextParamSets.BLOCK)
                ),
                lookupProvider
        )));
    }
}

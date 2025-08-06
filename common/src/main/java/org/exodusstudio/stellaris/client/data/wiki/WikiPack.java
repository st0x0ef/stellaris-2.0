package org.exodusstudio.stellaris.client.data.wiki;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screen.tablet.application.wiki.WikiApplicationScreen;

import java.util.Map;

public class WikiPack extends SimpleJsonResourceReloadListener<WikiEntry> {

    public WikiPack() {
        super(WikiEntry.CODEC, FileToIdConverter.json("wiki"));
    }

    @Override
    protected void apply(Map<ResourceLocation, WikiEntry> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {

        Stellaris.LOG.error("Loading Assets for Tablet Pack");
        resourceLocationJsonElementMap.forEach((key, entry) -> {

            if (!WikiApplicationScreen.ENTRIES.containsKey(entry.id())) {
                WikiApplicationScreen.ENTRIES.put(entry.id(), entry);
            }
            entry.components().forEach(info -> WikiApplicationScreen.ENTRY_COMPONENTS.put(ResourceLocation.fromNamespaceAndPath(entry.id(), info.id()), info));

            Stellaris.LOG.info("Loading tablet entry: {}", key);

        });

    }
}

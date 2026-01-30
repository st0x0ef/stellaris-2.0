package org.exodusstudio.stellaris.client.data.wiki;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.exodusstudio.stellaris.Stellaris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WikiPacks {

    public static ArrayList<WikiEntry> ENTRIES = new ArrayList<>();

    public static Map<Identifier, EntryInfo> ENTRY_COMPONENTS = new HashMap<>();


    public static class WikiEntryPack extends SimpleJsonResourceReloadListener<WikiEntry> {

        public WikiEntryPack() {
            super(WikiEntry.CODEC, FileToIdConverter.json("wiki/entries"));
        }

        @Override
        protected void apply(Map<Identifier, WikiEntry> IdentifierJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {
            Stellaris.LOG.error("Loading Assets for Tablet Pack");
            IdentifierJsonElementMap.forEach((key, entry) -> {
                if (!WikiPacks.ENTRIES.contains(entry)) {
                    WikiPacks.ENTRIES.add(entry);
                }

                Stellaris.LOG.info("Loaded tablet entry: {}", key);
            });

        }
    }

    public static class EntryInfoPack extends SimpleJsonResourceReloadListener<EntryInfo> {

        public EntryInfoPack() {
            super(EntryInfo.CODEC, FileToIdConverter.json("wiki/infos"));
        }

        @Override
        protected void apply(Map<Identifier, EntryInfo> IdentifierJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {
            Stellaris.LOG.error("Loading Assets for Tablet Pack");

            WikiPacks.ENTRY_COMPONENTS.putAll(IdentifierJsonElementMap);

        }
    }
}

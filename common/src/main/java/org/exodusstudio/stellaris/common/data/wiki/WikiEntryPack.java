package org.exodusstudio.stellaris.common.data.wiki;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.exodusstudio.stellaris.Stellaris;

import java.util.ArrayList;
import java.util.Map;

public class WikiEntryPack extends SimpleJsonResourceReloadListener<WikiEntry> {
    public static final String ID = "wiki/entries";

    public static ArrayList<WikiEntry> ENTRIES = new ArrayList<>();

    public WikiEntryPack() {
        super(WikiEntry.CODEC, FileToIdConverter.json(ID));
    }

    @Override
    protected void apply(Map<Identifier, WikiEntry> IdentifierJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        Stellaris.LOG.info("Loading Assets for Tablet Pack");
        WikiEntryPack.ENTRIES.clear();
        IdentifierJsonElementMap.forEach((key, entry) -> {
            if (!WikiEntryPack.ENTRIES.contains(entry)) {
                WikiEntryPack.ENTRIES.add(entry);
            }

            Stellaris.LOG.info("Loaded tablet entry: {}", key);
        });

    }

}

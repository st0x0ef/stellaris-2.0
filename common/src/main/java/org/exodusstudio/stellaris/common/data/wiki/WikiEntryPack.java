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

    public static volatile ArrayList<WikiEntry> ENTRIES = new ArrayList<>();

    public WikiEntryPack() {
        super(WikiEntry.CODEC, FileToIdConverter.json(ID));
    }

    @Override
    protected void apply(Map<Identifier, WikiEntry> IdentifierJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        Stellaris.LOG.debug("Loading Assets for Tablet Pack");
        ArrayList<WikiEntry> entries = new ArrayList<>();
        IdentifierJsonElementMap.forEach((key, entry) -> {
            if (!entries.contains(entry)) {
                entries.add(entry);
            }

            Stellaris.LOG.debug("Loaded tablet entry: {}", key);
        });
        WikiEntryPack.ENTRIES = entries;

    }

}

package org.exodusstudio.stellaris.common.data.wiki;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.Stellaris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WikiPacks {

    public static ArrayList<WikiEntry> ENTRIES = new ArrayList<>();

    public static HashMap<Identifier, EntryInfo> ENTRY_COMPONENTS = new HashMap<>();


    public static class WikiEntryPack extends SimpleJsonResourceReloadListener<WikiEntry> {
        public static final String ID = "wiki/entries";

        public WikiEntryPack() {
            super(WikiEntry.CODEC, FileToIdConverter.json(ID));
        }

        @Override
        protected void apply(Map<Identifier, WikiEntry> IdentifierJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {
            Stellaris.LOG.info("Loading Assets for Tablet Pack");
            WikiPacks.ENTRIES.clear();
            IdentifierJsonElementMap.forEach((key, entry) -> {
                if (!WikiPacks.ENTRIES.contains(entry)) {
                    WikiPacks.ENTRIES.add(entry);
                }

                Stellaris.LOG.info("Loaded tablet entry: {}", key);
            });

        }
    }

    public static class EntryInfoPack extends SimpleJsonResourceReloadListener<EntryInfo> {
        public static final String ID = "wiki/infos";

        public EntryInfoPack() {
            super(EntryInfo.CODEC, FileToIdConverter.json(ID));
        }

        public static Map<ResourceKey<Block>, Identifier> BLOCK_ENTRY_RESOLVER = new HashMap<>();
        public static Map<TagKey<Block>, Identifier> TAG_ENTRY_RESOLVER = new HashMap<>();


        @Override
        protected void apply(Map<Identifier, EntryInfo> IdentifierJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {
            WikiPacks.ENTRY_COMPONENTS.clear();
            BLOCK_ENTRY_RESOLVER.clear();
            TAG_ENTRY_RESOLVER.clear();

            WikiPacks.ENTRY_COMPONENTS.putAll(IdentifierJsonElementMap);

            IdentifierJsonElementMap.forEach((key, entry) -> entry.associatedBlocks().ifPresent(blocks -> {
                for(Either<TagKey<Block>, ResourceKey<Block>> tagKey : blocks) {
                    tagKey.ifLeft((tag) -> TAG_ENTRY_RESOLVER.put(tag, key));
                    tagKey.ifRight((resourceKey) -> BLOCK_ENTRY_RESOLVER.put(resourceKey, key));
                }
            }));
        }
    }
}

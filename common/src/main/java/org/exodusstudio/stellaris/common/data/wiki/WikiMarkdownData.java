package org.exodusstudio.stellaris.common.data.wiki;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.Stellaris;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class WikiMarkdownData extends SimplePreparableReloadListener<Map<Identifier, String>> {

    private final FileToIdConverter lister;

    public static final String ID = "wiki/infos";

    public static HashMap<Identifier, MarkdownPage> ENTRY_PAGES = new HashMap<>();

    public static Map<ResourceKey<Block>, Identifier> BLOCK_ENTRY_RESOLVER = new HashMap<>();
    public static Map<TagKey<Block>, Identifier> TAG_ENTRY_RESOLVER = new HashMap<>();


    public WikiMarkdownData() {
        this.lister = new FileToIdConverter(ID, ".md");
    }


    @Override
    protected @NonNull Map<Identifier, String> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<Identifier, String> result = new HashMap<>();
        scanDirectory(manager, this.lister, result);
        return result;
    }

    @Override
    protected void apply(Map<Identifier, String> preparations, ResourceManager manager, ProfilerFiller profiler) {
        for(Identifier id : preparations.keySet()) {


            //TODO
            //Note for my self : the id already has the folder in it.
            MarkdownPage page = new MarkdownPage(id, preparations.get(id));
            ENTRY_PAGES.put(id, page);

            for (Either<TagKey<Block>, ResourceKey<Block>> associatedBlock : page.associatedBlocks) {
                associatedBlock.ifLeft(tagKey -> TAG_ENTRY_RESOLVER.put(tagKey, id));
                associatedBlock.ifRight(resourceKey -> BLOCK_ENTRY_RESOLVER.put(resourceKey, id));
            }
        }
    }


    public static void scanDirectory(final ResourceManager manager, final FileToIdConverter lister, final Map<Identifier, String> result) {
        for(Map.Entry<Identifier, Resource> entry : lister.listMatchingResources(manager).entrySet()) {
            Identifier location = entry.getKey();
            Identifier id = lister.fileToId(location);

            try (Reader reader = (entry.getValue()).openAsReader()) {
                result.putIfAbsent(id, reader.readAllAsString());
            } catch (IllegalArgumentException | IOException | JsonParseException e) {
                Stellaris.LOG.error("Couldn't parse data file '{}' from '{}'",id, location, e);
            }
        }

    }
}

package org.exodusstudio.stellaris.common.data;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class SdCardData extends SimpleJsonResourceReloadListener<SdCard> {
    public static final String ID = "sd_cards";

    public static final Map<String, SdCard> SD_CARDS = new HashMap<>();

    public SdCardData() {
        super(SdCard.CODEC, FileToIdConverter.json(ID));
    }

    @Override
    protected void apply(Map<Identifier, SdCard> sdCardMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        SD_CARDS.clear();
        for (Map.Entry<Identifier, SdCard> entry : sdCardMap.entrySet()) {
            SD_CARDS.put(entry.getKey().toString(), entry.getValue());
        }
    }

    public static SdCard getSdCard(String identifier) {
        return SD_CARDS.getOrDefault(identifier, SD_CARDS.get("stellaris:error"));
    }

}

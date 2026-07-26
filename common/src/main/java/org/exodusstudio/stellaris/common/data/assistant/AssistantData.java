package org.exodusstudio.stellaris.common.data.assistant;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.exodusstudio.stellaris.Stellaris;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AssistantData extends SimpleJsonResourceReloadListener<AssistantMessage> {
    public static final String ID = "assistant";

    private static final Map<AssistantTrigger, List<Entry>> MESSAGES = new EnumMap<>(AssistantTrigger.class);

    public record Entry(Identifier id, AssistantMessage message) {}

    public AssistantData() {
        super(AssistantMessage.CODEC, FileToIdConverter.json(ID));
    }

    @Override
    protected void apply(Map<Identifier, AssistantMessage> messageMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        setMessages(messageMap);
    }

    public static void setMessages(Map<Identifier, AssistantMessage> messages) {
        MESSAGES.clear();

        for (Map.Entry<Identifier, AssistantMessage> entry : messages.entrySet()) {
            MESSAGES.computeIfAbsent(entry.getValue().trigger(), trigger -> new ArrayList<>())
                    .add(new Entry(entry.getKey(), entry.getValue()));
        }

        for (List<Entry> entries : MESSAGES.values()) {
            entries.sort(Comparator.comparingInt((Entry entry) -> entry.message().priority()).reversed()
                    .thenComparing(entry -> entry.id().toString()));
        }

        Stellaris.LOG.debug("Loaded {} assistant messages", messages.size());
    }

    public static List<Entry> get(AssistantTrigger trigger) {
        return MESSAGES.getOrDefault(trigger, List.of());
    }
}

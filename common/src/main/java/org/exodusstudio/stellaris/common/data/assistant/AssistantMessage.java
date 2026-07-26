package org.exodusstudio.stellaris.common.data.assistant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public record AssistantMessage(
        AssistantTrigger trigger,
        Optional<Identifier> dimension,
        Optional<Component> speaker,
        boolean oncePerPlayer,
        int priority,
        List<AssistantLine> lines) {

    public static final Component DEFAULT_SPEAKER = Component.translatable("assistant.stellaris.name");

    public static final Codec<AssistantMessage> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    AssistantTrigger.CODEC.fieldOf("trigger").forGetter(AssistantMessage::trigger),
                    Identifier.CODEC.optionalFieldOf("dimension").forGetter(AssistantMessage::dimension),
                    ComponentSerialization.CODEC.optionalFieldOf("speaker").forGetter(AssistantMessage::speaker),
                    Codec.BOOL.optionalFieldOf("once_per_player", true).forGetter(AssistantMessage::oncePerPlayer),
                    Codec.INT.optionalFieldOf("priority", 0).forGetter(AssistantMessage::priority),
                    AssistantLine.CODEC.listOf().fieldOf("lines").forGetter(AssistantMessage::lines)
            ).apply(instance, AssistantMessage::new)
    );

    public Component speakerOrDefault() {
        return speaker.orElse(DEFAULT_SPEAKER);
    }
}

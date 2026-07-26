package org.exodusstudio.stellaris.common.data.assistant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record AssistantLine(Component text, int delay) {

    public static final Codec<AssistantLine> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ComponentSerialization.CODEC.fieldOf("text").forGetter(AssistantLine::text),
                    Codec.INT.optionalFieldOf("delay", 0).forGetter(AssistantLine::delay)
            ).apply(instance, AssistantLine::new)
    );
}

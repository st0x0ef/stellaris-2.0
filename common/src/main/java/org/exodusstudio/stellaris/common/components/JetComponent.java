package org.exodusstudio.stellaris.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitBoots;

import java.io.Serializable;

public record JetComponent(SpaceSuitBoots.ModeType type) implements Serializable {
    public static final Codec<JetComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpaceSuitBoots.ModeType.CODEC.fieldOf("type").forGetter(JetComponent::type)
    ).apply(instance, JetComponent::new));

    public static final StreamCodec<ByteBuf, JetComponent> STREAM_CODEC;

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.fromCodec(SpaceSuitBoots.ModeType.CODEC), JetComponent::type, JetComponent::new);
    }
}

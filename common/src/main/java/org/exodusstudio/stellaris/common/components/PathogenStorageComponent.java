package org.exodusstudio.stellaris.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PathogenStorageComponent(int stored, int capacity) {
    public static Codec<PathogenStorageComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("stored").forGetter(PathogenStorageComponent::stored),
                    Codec.INT.fieldOf("capacity").forGetter(PathogenStorageComponent::capacity)
            ).apply(instance, PathogenStorageComponent::new)
    );

    public static final StreamCodec<ByteBuf, PathogenStorageComponent> STREAM_CODEC;

    static {
        STREAM_CODEC = StreamCodec.of(
                (buf, timerComponents) -> {
                    buf.writeInt(timerComponents.stored);
                    buf.writeInt(timerComponents.capacity);
                },
                buf -> new PathogenStorageComponent(buf.readInt(), buf.readInt())
        );
    }
}

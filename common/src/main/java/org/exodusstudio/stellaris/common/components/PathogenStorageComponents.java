package org.exodusstudio.stellaris.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PathogenStorageComponents(int stored, int capacity) {
    public static Codec<PathogenStorageComponents> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("stored").forGetter(PathogenStorageComponents::stored),
                    Codec.INT.fieldOf("capacity").forGetter(PathogenStorageComponents::capacity)
            ).apply(instance, PathogenStorageComponents::new)
    );

    public static final StreamCodec<ByteBuf, PathogenStorageComponents> STREAM_CODEC;

    static {
        STREAM_CODEC = StreamCodec.of(
                (buf, timerComponents) -> {
                    buf.writeInt(timerComponents.stored);
                    buf.writeInt(timerComponents.capacity);
                },
                buf -> new PathogenStorageComponents(buf.readInt(), buf.readInt())
        );
    }
}

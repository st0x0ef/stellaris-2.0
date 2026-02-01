package org.exodusstudio.stellaris.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TimerComponent(int timeLeft, int maxTime) {
    public TimerComponent(int maxTime) {
        this(maxTime, maxTime);
    }

    public static Codec<TimerComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("timeLeft").forGetter(TimerComponent::timeLeft),
                    Codec.INT.fieldOf("maxTime").forGetter(TimerComponent::maxTime)
            ).apply(instance, TimerComponent::new)
    );

    public static final StreamCodec<ByteBuf, TimerComponent> STREAM_CODEC;

    static {
        STREAM_CODEC = StreamCodec.of(
                (buf, timerComponents) -> {
                    buf.writeInt(timerComponents.timeLeft);
                    buf.writeInt(timerComponents.maxTime);
                },
                buf -> new TimerComponent(buf.readInt(), buf.readInt())
        );
    }

    public TimerComponent tick() {
        if (timeLeft - 1 > 0) {
            return new TimerComponent(timeLeft - 1, maxTime);
        }
        return new TimerComponent(0, maxTime);
    }
}

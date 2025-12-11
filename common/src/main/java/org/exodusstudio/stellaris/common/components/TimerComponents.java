package org.exodusstudio.stellaris.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TimerComponents(int timeLeft, int maxTime) {
    public TimerComponents(int maxTime) {
        this(maxTime, maxTime);
    }

    public static Codec<TimerComponents> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("timeLeft").forGetter(TimerComponents::timeLeft),
                    Codec.INT.fieldOf("maxTime").forGetter(TimerComponents::maxTime)
            ).apply(instance, TimerComponents::new)
    );

    public static final StreamCodec<ByteBuf, TimerComponents> STREAM_CODEC;

    static {
        STREAM_CODEC = StreamCodec.of(
                (buf, timerComponents) -> {
                    buf.writeInt(timerComponents.timeLeft);
                    buf.writeInt(timerComponents.maxTime);
                },
                buf -> new TimerComponents(buf.readInt(), buf.readInt())
        );
    }

    public TimerComponents tick(int delta) {
        if (timeLeft - delta > 0) {
            return new TimerComponents(timeLeft - delta, maxTime);
        }
        return new TimerComponents(0, maxTime);
    }
}

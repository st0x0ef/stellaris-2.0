package org.exodusstudio.stellaris.common.components;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.function.Function;

public record GeigerCounterComponent(Type type, int radius) {


    public static final Codec<GeigerCounterComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.fieldOf("type").forGetter(GeigerCounterComponent::type),
            Codec.INT.fieldOf("radius").forGetter(GeigerCounterComponent::radius)
    ).apply(instance, GeigerCounterComponent::new));

    public static final StreamCodec<ByteBuf, GeigerCounterComponent> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.fromCodec(Type.CODEC), GeigerCounterComponent::type,
                ByteBufCodecs.VAR_INT, GeigerCounterComponent::radius,
                GeigerCounterComponent::new);


    public enum Type implements StringRepresentable {
        MAX_RADIATION(blocks -> blocks.stream().map(Pair::getSecond).max(Float::compare).orElse(0f)),
        TOTAL_RADIATION(blocks -> blocks.stream().map(Pair::getSecond).reduce(0f, Float::sum));

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }

        @Override
        public String toString() {
            return getSerializedName();
        }

        private Function<List<Pair<BlockPos, Float>>, Float> function;

        Type(Function<List<Pair<BlockPos, Float>>, Float> function) {
            this.function = function;
        }

        public float calculate(List<Pair<BlockPos, Float>> blocks) {
            return function.apply(blocks);
        }

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

    }

}

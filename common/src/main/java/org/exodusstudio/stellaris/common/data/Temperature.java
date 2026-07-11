package org.exodusstudio.stellaris.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Temperature(int dayTimeTemperature, int nightTimeTemperature) {
    public static final Codec<Temperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("dayTimeTemperature").forGetter(Temperature::dayTimeTemperature),
            Codec.INT.fieldOf("nightTimeTemperature").forGetter(Temperature::nightTimeTemperature)
    ).apply(instance, Temperature::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Temperature> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Temperature::dayTimeTemperature,
            ByteBufCodecs.INT, Temperature::nightTimeTemperature,
            Temperature::new
    );
}

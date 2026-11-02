package org.exodusstudio.stellaris.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RadiationHolderComponent(float radiationContained, float maxRadiation) {

    public RadiationHolderComponent(float maxRadiation) {
        this(0.0f, maxRadiation);
    }

    public boolean canAddRadiation(float amount) {
        return radiationContained + amount <= maxRadiation;
    }

    public boolean isFull() {
        return radiationContained >= maxRadiation;
    }

    public RadiationHolderComponent addRadiation(float amount) {
        float newRadiationContained = Math.min(this.radiationContained + amount, this.maxRadiation);
        return new RadiationHolderComponent(newRadiationContained, this.maxRadiation);
    }

    public static Codec<RadiationHolderComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("radiationContained").forGetter(RadiationHolderComponent::radiationContained),
                    Codec.FLOAT.fieldOf("maxRadiation").forGetter(RadiationHolderComponent::maxRadiation)
            ).apply(instance, RadiationHolderComponent::new)
    );

    public static final StreamCodec<ByteBuf, RadiationHolderComponent> STREAM_CODEC= StreamCodec.composite(
            ByteBufCodecs.FLOAT, RadiationHolderComponent::radiationContained,
            ByteBufCodecs.FLOAT, RadiationHolderComponent::maxRadiation,
            RadiationHolderComponent::new
    );



}

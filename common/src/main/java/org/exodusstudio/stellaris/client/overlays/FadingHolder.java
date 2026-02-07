package org.exodusstudio.stellaris.client.overlays;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * A simple record to hold fading information for overlays.
 * @param fading Whether the overlay is currently fading or not. If true, the screen will fade. If false the screen will unfade.
 * @param fadeAmount The current fade amount, between 0 and 1, where 0 is fully transparent and 1 is fully opaque.
 */
public record FadingHolder(boolean fading, float fadeAmount) {

    public static final Codec<FadingHolder> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("fading").forGetter(FadingHolder::fading),
                    Codec.FLOAT.fieldOf("fadeAmount").forGetter(FadingHolder::fadeAmount)
            ).apply(instance, FadingHolder::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FadingHolder> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, FadingHolder::fading,
            ByteBufCodecs.FLOAT, FadingHolder::fadeAmount,
            FadingHolder::new
    );
}

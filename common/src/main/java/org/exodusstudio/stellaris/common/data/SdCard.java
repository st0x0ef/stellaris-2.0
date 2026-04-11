package org.exodusstudio.stellaris.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SdCard(String name, String content) {
    public static final Codec<SdCard> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(SdCard::name),
            Codec.STRING.fieldOf("content").forGetter(SdCard::content))
            .apply(instance, SdCard::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SdCard> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SdCard::name,
            ByteBufCodecs.STRING_UTF8, SdCard::content,
            SdCard::new
    );
}

package org.exodusstudio.stellaris.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * @param id the id this card was loaded under. Not part of the file format nor of the packet: it is
 *           re-attached from the map key on both sides, and is only used to build the translation keys.
 */
public record SdCard(String name, String content, @Nullable Identifier id) {

    public SdCard(String name, String content) {
        this(name, content, null);
    }

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

    public SdCard withId(@Nullable Identifier id) {
        return new SdCard(this.name, this.content, id);
    }

    /** The card name, translated. The raw {@code name} from the data file is the fallback. */
    public Component getDisplayName() {
        return translate("name", this.name);
    }

    /** The card content, translated. The raw {@code content} from the data file is the fallback. */
    public Component getDisplayContent() {
        return translate("content", this.content);
    }

    private Component translate(String suffix, String fallback) {
        if (this.id == null) {
            return Component.literal(fallback);
        }
        return Component.translatableWithFallback(
                "sd_card." + this.id.getNamespace() + "." + this.id.getPath() + "." + suffix, fallback);
    }
}

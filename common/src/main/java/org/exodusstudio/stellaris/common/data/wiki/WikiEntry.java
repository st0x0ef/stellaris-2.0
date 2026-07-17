package org.exodusstudio.stellaris.common.data.wiki;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record WikiEntry(
        Identifier id,
        String description,
        Identifier icon,
        Identifier hoverIcon) {


    public static final Codec<WikiEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(WikiEntry::id),
            Codec.STRING.fieldOf("description").forGetter(WikiEntry::description),
            Identifier.CODEC.fieldOf("icon").forGetter(WikiEntry::icon),
            Identifier.CODEC.fieldOf("hoverIcon").forGetter(WikiEntry::hoverIcon)
    ).apply(instance, WikiEntry::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, WikiEntry> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, WikiEntry::id,
            ByteBufCodecs.STRING_UTF8, WikiEntry::description,
            Identifier.STREAM_CODEC, WikiEntry::icon,
            Identifier.STREAM_CODEC, WikiEntry::hoverIcon,
            WikiEntry::new
    );


    public Component getTitle() {
        return Component.translatable("wiki." + id.getNamespace() + "." + id.getPath() + ".title");
    }

}

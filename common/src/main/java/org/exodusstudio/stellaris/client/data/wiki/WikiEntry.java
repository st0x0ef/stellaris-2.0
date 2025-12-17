package org.exodusstudio.stellaris.client.data.wiki;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record WikiEntry(ResourceLocation id, String description, ResourceLocation icon, ResourceLocation hoverIcon) {

    public static final Codec<WikiEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(WikiEntry::id),
            Codec.STRING.fieldOf("description").forGetter(WikiEntry::description),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(WikiEntry::icon),
            ResourceLocation.CODEC.fieldOf("hoverIcon").forGetter(WikiEntry::hoverIcon)
    ).apply(instance, WikiEntry::new));

    public Component getTitle() {
        return Component.translatable("wiki." + id.getNamespace() + "." + id.getPath() + ".title");
    }

}

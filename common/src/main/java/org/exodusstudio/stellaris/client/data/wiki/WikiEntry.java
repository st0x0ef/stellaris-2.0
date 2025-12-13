package org.exodusstudio.stellaris.client.data.wiki;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

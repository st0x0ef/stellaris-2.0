package org.exodusstudio.stellaris.client.data.wiki;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record WikiEntry(ResourceLocation id, String description, ResourceLocation icon, ResourceLocation hoverIcon,
                        List<EntryInfo> components) {

    public static final Codec<WikiEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(WikiEntry::id),
            Codec.STRING.fieldOf("description").forGetter(WikiEntry::description),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(WikiEntry::icon),
            ResourceLocation.CODEC.fieldOf("hoverIcon").forGetter(WikiEntry::hoverIcon),
            EntryInfo.CODEC.listOf().fieldOf("infos").forGetter(WikiEntry::components)
    ).apply(instance, WikiEntry::new));

    public Component getTitle() {
        return Component.translatable("wiki." + id.getNamespace() + "." + id.getPath() + ".title");
    }

    public record EntryInfo(String id, String title, String iconType, List<InfoComponent> components) {

        public static final Codec<EntryInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(EntryInfo::id),
                Codec.STRING.fieldOf("title").forGetter(EntryInfo::title),
                Codec.STRING.fieldOf("iconType").forGetter(EntryInfo::iconType),
                InfoComponent.CODEC.listOf().fieldOf("components").forGetter(EntryInfo::components)
        ).apply(instance, EntryInfo::new));
    }

    public record InfoComponent(String type, Optional<String> text, Optional<ImageComponent> image,
                                Optional<ItemComponent> item, Optional<EntityComponent> entity) {

        public static final Codec<InfoComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("type").forGetter(InfoComponent::type),
                Codec.STRING.optionalFieldOf("text").forGetter(InfoComponent::text),
                ImageComponent.CODEC.optionalFieldOf("image").forGetter(InfoComponent::image),
                ItemComponent.CODEC.optionalFieldOf("item").forGetter(InfoComponent::item),
                EntityComponent.CODEC.optionalFieldOf("entity").forGetter(InfoComponent::entity)

        ).apply(instance, InfoComponent::new));
    }

    public record ImageComponent(ResourceLocation location, int width, int height) {

        public static final Codec<ImageComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("location").forGetter(ImageComponent::location),
                Codec.INT.fieldOf("width").forGetter(ImageComponent::width),
                Codec.INT.fieldOf("height").forGetter(ImageComponent::height)
        ).apply(instance, ImageComponent::new));
    }

    public record ItemComponent(ItemStack stack, float size, Optional<Boolean> onlyIcon) {

        public static final Codec<ItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(ItemComponent::stack),
                Codec.FLOAT.fieldOf("size").forGetter(ItemComponent::size),
                Codec.BOOL.optionalFieldOf("onlyIcon").forGetter(ItemComponent::onlyIcon)
        ).apply(instance, ItemComponent::new));
    }

    public record EntityComponent(ResourceLocation entity, int scale) {

        public static final Codec<EntityComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(EntityComponent::entity),
                Codec.INT.fieldOf("scale").forGetter(EntityComponent::scale)
        ).apply(instance, EntityComponent::new));
    }
}

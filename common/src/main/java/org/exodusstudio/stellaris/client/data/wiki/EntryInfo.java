package org.exodusstudio.stellaris.client.data.wiki;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record EntryInfo(ResourceLocation id, ResourceLocation entryId, String title, String iconType, List<InfoComponent> components) {

    public static final Codec<EntryInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(EntryInfo::id),
            ResourceLocation.CODEC.fieldOf("entryId").forGetter(EntryInfo::entryId),
            Codec.STRING.fieldOf("title").forGetter(EntryInfo::title),
            Codec.STRING.fieldOf("iconType").forGetter(EntryInfo::iconType),
            InfoComponent.CODEC.listOf().fieldOf("components").forGetter(EntryInfo::components)
    ).apply(instance, EntryInfo::new));

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


    /**
     * A component that render an image on the wiki.
     * @param location the location of the image
     * @param width the width of the image
     * @param height the height of the image
     */
    public record ImageComponent(ResourceLocation location, int width, int height) {

        public static final Codec<ImageComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("location").forGetter(ImageComponent::location),
                Codec.INT.fieldOf("width").forGetter(ImageComponent::width),
                Codec.INT.fieldOf("height").forGetter(ImageComponent::height)
        ).apply(instance, ImageComponent::new));

        /**
         * If the location don't have a .png at the end, we add it.
         * @return the resource location with .png at the end
         */
        public ResourceLocation formatFileLocation() {
            if(!this.location.getPath().endsWith(".png")) {
                return location.withPrefix(".png");
            }
            return location;
        }
    }

    /**
     * A component that render an item on the wiki.
     * @param stack the item to render
     * @param size the item size
     * @param onlyIcon If present, the item won't be shown in the wiki page but only on the enty button.
     */
    public record ItemComponent(ItemStack stack, float size, Optional<Boolean> onlyIcon) {

        public static final Codec<ItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(ItemComponent::stack),
                Codec.FLOAT.fieldOf("size").forGetter(ItemComponent::size),
                Codec.BOOL.optionalFieldOf("onlyIcon").forGetter(ItemComponent::onlyIcon)
        ).apply(instance, ItemComponent::new));

    }

    /**
     * A component that render an entity on the wiki.
     * @param location the location of the entity to render
     * @param scale the entity scale
     */
    public record EntityComponent(ResourceLocation location, int scale) {

        public static final Codec<EntityComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(EntityComponent::location),
                Codec.INT.fieldOf("scale").forGetter(EntityComponent::scale)
        ).apply(instance, EntityComponent::new));

    }


}

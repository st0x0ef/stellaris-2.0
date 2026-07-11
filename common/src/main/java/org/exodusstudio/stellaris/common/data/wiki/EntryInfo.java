package org.exodusstudio.stellaris.common.data.wiki;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public record EntryInfo(Identifier id, Identifier entryId, String title, String iconType, List<InfoComponent> components,
                        Optional<List<Either<TagKey<Block>, ResourceKey<Block>>>> associatedBlocks) {

    public static Codec<Vector3f> VEC3F = Codec.FLOAT.listOf().comapFlatMap((list) -> Util.fixedSize(list, 3).map((listx) -> new Vector3f(listx.getFirst(), listx.get(1), listx.getLast())), (vector3f) -> List.of(vector3f.x, vector3f.y,vector3f.z));


    public static final Codec<EntryInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(EntryInfo::id),
            Identifier.CODEC.fieldOf("entryId").forGetter(EntryInfo::entryId),
            Codec.STRING.fieldOf("title").forGetter(EntryInfo::title),
            Codec.STRING.fieldOf("iconType").forGetter(EntryInfo::iconType),
            InfoComponent.CODEC.listOf().fieldOf("components").forGetter(EntryInfo::components),
            Codec.either(
                    TagKey.hashedCodec(Registries.BLOCK),
                    ResourceKey.codec(Registries.BLOCK)
            ).listOf().optionalFieldOf("associatedBlocks").forGetter(EntryInfo::associatedBlocks)
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
    public record ImageComponent(Identifier location, int width, int height, Optional<String> legend) {
        public static final Codec<ImageComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("location").forGetter(ImageComponent::location),
                Codec.INT.fieldOf("width").forGetter(ImageComponent::width),
                Codec.INT.fieldOf("height").forGetter(ImageComponent::height),
                Codec.STRING.optionalFieldOf("legend").forGetter(ImageComponent::legend)
        ).apply(instance, ImageComponent::new));

        /**
         * If the location don't have a .png at the end, we add it.
         * @return the resource location with .png at the end
         */
        public Identifier formatFileLocation() {
            if(!this.location.getPath().endsWith(".png")) {
                return location.withSuffix(".png");
            }
            return location;
        }
    }

    /**
     * A component that render an item on the wiki.
     * @param stack the item to render
     * @param onlyIcon If present, the item won't be shown in the wiki page but only on the enty button.
     */
    public record ItemComponent(ItemStackTemplate stack, Optional<Boolean> onlyIcon, Optional<Float> scale) {
        public static final Codec<ItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStackTemplate.CODEC.fieldOf("stack").forGetter(ItemComponent::stack),
                Codec.BOOL.optionalFieldOf("onlyIcon").forGetter(ItemComponent::onlyIcon),
                Codec.FLOAT.optionalFieldOf("scale").forGetter(ItemComponent::scale)
        ).apply(instance, ItemComponent::new));
    }

    /**
     * A component that render an entity on the wiki.
     * @param location the location of the entity to render
     * @param scale the entity scale
     */
    public record EntityComponent(Identifier location, int scale, Optional<Vector3f> defaultRotation) {

        public static final Codec<EntityComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(EntityComponent::location),
                Codec.INT.fieldOf("scale").forGetter(EntityComponent::scale),
                VEC3F.optionalFieldOf("defaultRotation").forGetter(EntityComponent::defaultRotation)
        ).apply(instance, EntityComponent::new));

    }


}

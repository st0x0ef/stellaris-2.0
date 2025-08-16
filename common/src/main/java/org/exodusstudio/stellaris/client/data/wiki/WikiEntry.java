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

    public record EntryInfo(String id, String title, String iconType, List<InfoComponent> components) implements RenderableEntry{

        public static final Codec<EntryInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(EntryInfo::id),
                Codec.STRING.fieldOf("title").forGetter(EntryInfo::title),
                Codec.STRING.fieldOf("iconType").forGetter(EntryInfo::iconType),
                InfoComponent.CODEC.listOf().fieldOf("components").forGetter(EntryInfo::components)
        ).apply(instance, EntryInfo::new));

        @Override
        public int render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {


            return 9;
        }
    }

    public record InfoComponent(String type, Optional<String> text, Optional<ImageComponent> image,
                                Optional<ItemComponent> item, Optional<EntityComponent> entity) implements RenderableEntry {

        public static final Codec<InfoComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("type").forGetter(InfoComponent::type),
                Codec.STRING.optionalFieldOf("text").forGetter(InfoComponent::text),
                ImageComponent.CODEC.optionalFieldOf("image").forGetter(InfoComponent::image),
                ItemComponent.CODEC.optionalFieldOf("item").forGetter(InfoComponent::item),
                EntityComponent.CODEC.optionalFieldOf("entity").forGetter(InfoComponent::entity)

        ).apply(instance, InfoComponent::new));

        @Override
        public int render(GuiGraphics guiGraphics, int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            if(Objects.equals(this.type, "text")) {
                guiGraphics.drawString(Minecraft.getInstance().font, this.text().get(), x, y, Utils.getColorHexCode("white"));
            }

            return 9;
        }
    }


    /**
     *
     * Components that can be rendered in the wiki entry.
     * Each component implements the RenderableEntry interface to define how it should be rendered.
     *
     */
    public record ImageComponent(ResourceLocation location, int width, int height) implements RenderableEntry {

        public static final Codec<ImageComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("location").forGetter(ImageComponent::location),
                Codec.INT.fieldOf("width").forGetter(ImageComponent::width),
                Codec.INT.fieldOf("height").forGetter(ImageComponent::height)
        ).apply(instance, ImageComponent::new));

        @Override
        public int render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            return 9; // Implement rendering logic here, e.g., guiGraphics.blit(location, x, y, width, height);
        }

        @Override
        public int getHeight() {
            return this.height + 40; // Adding padding for top and bottom
        }
    }


    public record ItemComponent(ItemStack stack, float size, Optional<Boolean> onlyIcon) implements RenderableEntry {

        public static final Codec<ItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(ItemComponent::stack),
                Codec.FLOAT.fieldOf("size").forGetter(ItemComponent::size),
                Codec.BOOL.optionalFieldOf("onlyIcon").forGetter(ItemComponent::onlyIcon)
        ).apply(instance, ItemComponent::new));

        @Override
        public int render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            return 9; // Implement rendering logic here, e.g., guiGraphics.renderItem(stack, x, y);
        }

        @Override
        public int getHeight() {
            if (this.onlyIcon().isEmpty() || !this.onlyIcon().get()) {
                return 35 + (int) (this.size / 4) + 40;
            }
            return 0;
        }
    }

    public record EntityComponent(ResourceLocation entity, int scale) implements RenderableEntry {

        public static final Codec<EntityComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(EntityComponent::entity),
                Codec.INT.fieldOf("scale").forGetter(EntityComponent::scale)
        ).apply(instance, EntityComponent::new));

        @Override
        public int render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            return 9; // Implement rendering logic here, e.g., guiGraphics.renderEntity(entity, x, y, scale);
        }

        @Override
        public int getHeight() {
            return 80;
        }
    }

    public interface RenderableEntry {
        int render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick);

        default int getHeight() {
            return 20; // Default height, can be overridden by specific components
        }
    }
}

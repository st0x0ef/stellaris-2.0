package org.exodusstudio.stellaris.common.data.space_station;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

import java.util.ArrayList;
import java.util.List;

public record SpaceStationRecipe(List<IngredientWithCount> items, Identifier structureId, Vec3i antenna_position) {

    public static final Codec<SpaceStationRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IngredientWithCount.CODEC.listOf().fieldOf("items").forGetter(SpaceStationRecipe::items),
            Identifier.CODEC.fieldOf("location").forGetter(SpaceStationRecipe::structureId),
            Vec3i.CODEC.fieldOf("antenna_position").forGetter(SpaceStationRecipe::antenna_position)
    ).apply(instance, SpaceStationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpaceStationRecipe> STREAM_CODEC = StreamCodec.composite(
            IngredientWithCount.STREAM_CODEC.apply(ByteBufCodecs.list()), SpaceStationRecipe::items,
            Identifier.STREAM_CODEC, SpaceStationRecipe::structureId,
            Vec3i.STREAM_CODEC, SpaceStationRecipe::antenna_position,
            SpaceStationRecipe::new
    );

    public Component getTooltip() {
        MutableComponent component = Component.literal("Resources :");

        for(IngredientWithCount ingredient : this.items) {
            component.append( "\n").append(Component.literal( "- " + ingredient.count() + "x " ).withStyle(ChatFormatting.GRAY));
            ingredient.itemRef().ifRight(tagKey -> component.append(Component.literal(tagKey.location().toString()).withStyle(ChatFormatting.GRAY)));
            ingredient.itemRef().ifLeft(itemKey -> component.append(Component.literal(itemKey.identifier().toString()).withStyle(ChatFormatting.GRAY)));
        }

        return component;
    }

    public boolean hasMaterials(List<Slot> slotsToCheck) {
        return planConsumption(slotsToCheck) != null;
    }

    public boolean removeMaterials(List<Slot> slotsToCheck) {
        int[] removalAmounts = planConsumption(slotsToCheck);
        if (removalAmounts == null) {
            return false;
        }

        for (int i = 0; i < slotsToCheck.size(); i++) {
            int amountToRemove = removalAmounts[i];
            if (amountToRemove > 0) {
                slotsToCheck.get(i).remove(amountToRemove);
            }
        }

        return true;
    }

    private int[] planConsumption(List<Slot> slotsToCheck) {
        List<IngredientWithCount> itemsLeftToCheck = new ArrayList<>(items);
        int[] removalAmounts = new int[slotsToCheck.size()];

        for (int slotIndex = 0; slotIndex < slotsToCheck.size(); slotIndex++) {
            Slot slot = slotsToCheck.get(slotIndex);
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) {
                continue;
            }

            int remainingInStack = stack.getCount();
            for (int i = 0; i < itemsLeftToCheck.size() && remainingInStack > 0; i++) {
                IngredientWithCount required = itemsLeftToCheck.get(i);
                if (!required.matches(stack)) {
                    continue;
                }

                int consumed = Math.min(remainingInStack, required.count());
                remainingInStack -= consumed;
                removalAmounts[slotIndex] += consumed;

                int remainingRequired = required.count() - consumed;
                if (remainingRequired <= 0) {
                    itemsLeftToCheck.remove(i);
                    i--;
                } else {
                    itemsLeftToCheck.set(i, new IngredientWithCount(required.itemRef(), remainingRequired));
                }
            }
        }

        if (!itemsLeftToCheck.isEmpty()) {
            return null;
        }

        return removalAmounts;
    }

    public static Component getComponent(ItemStack itemStack) {
        MutableComponent component = Component.translatable("tooltip.item.stellaris.space_station_blueprint");
        if (itemStack.has(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get())) {
            return component.append(itemStack.get(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get()).getDisplayName());
        }
        return component.append("None");
    }

    public MutableComponent getDisplayName() {
        return Component.translatable("station." + this.structureId.getNamespace() + "." + this.structureId.getPath());
    }


    public record IngredientWithCount(Either<ResourceKey<Item>, TagKey<Item>> itemRef, int count) {
        public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.either(ResourceKey.codec(Registries.ITEM),
                        TagKey.hashedCodec(Registries.ITEM)).fieldOf("item").forGetter(IngredientWithCount::itemRef),
                Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientWithCount::count)
        ).apply(instance, IngredientWithCount::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithCount> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.either(ResourceKey.streamCodec(Registries.ITEM), TagKey.streamCodec(Registries.ITEM)), IngredientWithCount::itemRef,
                ByteBufCodecs.INT, IngredientWithCount::count,
                IngredientWithCount::new
        );

        public boolean matches(ItemStack stack) {
            return itemRef.map(itemKey -> stack.typeHolder().is(itemKey), stack::is);
        }
    }


}

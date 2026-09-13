package org.exodusstudio.stellaris.common.data.space_station;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
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
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

import java.util.ArrayList;
import java.util.Comparator;
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
        MutableComponent component = Component.translatable("tooltip.stellaris.space_station.resources");

        for(IngredientWithCount ingredient : this.items) {
            // Ask the item for its own description id rather than assuming a prefix: block items keep
            // item.* unless they opt into useBlockDescriptionPrefix(), and either way this stays correct.
            Component name = ingredient.itemRef().map(
                    itemKey -> BuiltInRegistries.ITEM.get(itemKey)
                            .map(holder -> (Component) Component.translatable(holder.value().getDescriptionId()))
                            .orElseGet(() -> Component.literal(itemKey.identifier().toString())),
                    tagKey -> (Component) Component.literal("#" + tagKey.location()));
            component.append("\n").append(Component.translatable("tooltip.stellaris.space_station.ingredient",
                    ingredient.count(), name).withStyle(ChatFormatting.GRAY));
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
        int[] removalAmounts = new int[slotsToCheck.size()];
        int[] stillAvailable = new int[slotsToCheck.size()];
        for (int slotIndex = 0; slotIndex < slotsToCheck.size(); slotIndex++) {
            stillAvailable[slotIndex] = slotsToCheck.get(slotIndex).getItem().getCount();
        }

        // One stack can satisfy several requirements at once - an item requirement and a tag that
        // contains that item - so satisfy the pickiest requirements first. Walking the grid slot by
        // slot instead used to spend a stack on a broad tag that a narrower requirement still needed,
        // failing grids that could in fact be paid for.
        List<IngredientWithCount> ordered = new ArrayList<>(items);
        ordered.sort(Comparator.comparingInt(required -> countAvailable(required, slotsToCheck)));

        for (IngredientWithCount required : ordered) {
            int stillNeeded = required.count();

            for (int slotIndex = 0; slotIndex < slotsToCheck.size() && stillNeeded > 0; slotIndex++) {
                if (stillAvailable[slotIndex] <= 0) {
                    continue;
                }

                ItemStack stack = slotsToCheck.get(slotIndex).getItem();
                if (stack.isEmpty() || !required.matches(stack)) {
                    continue;
                }

                int consumed = Math.min(stillNeeded, stillAvailable[slotIndex]);
                stillAvailable[slotIndex] -= consumed;
                removalAmounts[slotIndex] += consumed;
                stillNeeded -= consumed;
            }

            if (stillNeeded > 0) {
                return null;
            }
        }

        return removalAmounts;
    }

    private static int countAvailable(IngredientWithCount required, List<Slot> slotsToCheck) {
        int total = 0;
        for (Slot slot : slotsToCheck) {
            ItemStack stack = slot.getItem();
            if (!stack.isEmpty() && required.matches(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public static Component getComponent(ItemStack itemStack) {
        MutableComponent component = Component.translatable("tooltip.item.stellaris.space_station_blueprint");
        if (itemStack.has(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get())) {
            return component.append(itemStack.get(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get()).getDisplayName());
        }
        return component.append(Component.translatable("tooltip.stellaris.none"));
    }

    /**
     * Tells the player what to do with a planned blueprint. A blank one does nothing in a rocket,
     * so it points at the engineering station instead.
     */
    public static Component getUsageComponent(ItemStack itemStack) {
        String key = itemStack.has(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get())
                ? "tooltip.item.stellaris.space_station_blueprint.in_rocket"
                : "tooltip.item.stellaris.space_station_blueprint.plan_first";
        return Component.translatable(key).withStyle(ChatFormatting.GRAY);
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

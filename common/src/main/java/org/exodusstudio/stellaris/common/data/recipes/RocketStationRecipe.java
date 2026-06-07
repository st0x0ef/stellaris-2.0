package org.exodusstudio.stellaris.common.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.blocks.entities.machines.EngineeringStationBlockEntity;
import org.exodusstudio.stellaris.common.data.recipes.input.RocketStationInput;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RocketStationRecipe(List<Ingredient> recipeItems, ItemStackTemplate output) implements Recipe<RocketStationInput> {

    @Override
    public boolean matches(RocketStationInput container, Level level) {
        for (int i = 0; i < ((EngineeringStationBlockEntity) container.entity()).getContainerSize() - 1; i++) {
            if (!recipeItems.get(i).test(container.getItem(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull ItemStack assemble(RocketStationInput container) {
        return output.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<RocketStationInput>> getSerializer() {
        return RecipesRegistry.ROCKET_STATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RocketStationInput>> getType() {
        return RecipesRegistry.ROCKET_STATION_TYPE.get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.create(this.recipeItems);
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static class Serializer {

        public static final MapCodec<RocketStationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf(1, 14).fieldOf("ingredients").forGetter(RocketStationRecipe::recipeItems),
                ItemStackTemplate.CODEC.fieldOf("output").forGetter(RocketStationRecipe::output)
        ).apply(instance, RocketStationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RocketStationRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), RocketStationRecipe::recipeItems,
                ItemStackTemplate.STREAM_CODEC, RocketStationRecipe::output,
                RocketStationRecipe::new);


        public static RecipeSerializer<RocketStationRecipe> create() {
            return new RecipeSerializer<>(CODEC, STREAM_CODEC);
        }
    }
}

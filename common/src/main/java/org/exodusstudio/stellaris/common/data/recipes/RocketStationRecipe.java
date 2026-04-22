package org.exodusstudio.stellaris.common.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.blocks.entities.machines.EngineeringStationBlockEntity;
import org.exodusstudio.stellaris.common.data.recipes.input.RocketStationInput;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RocketStationRecipe(List<Ingredient> recipeItems, ItemStack output) implements Recipe<RocketStationInput> {

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
    public @NotNull ItemStack assemble(RocketStationInput container, HolderLookup.Provider provider) {
        return output;
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

    public static class Serializer implements RecipeSerializer<RocketStationRecipe> {

        public static final MapCodec<RocketStationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf(1, 14).fieldOf("ingredients").forGetter(RocketStationRecipe::recipeItems),
                ItemStack.CODEC.fieldOf("output").forGetter(RocketStationRecipe::output)
        ).apply(instance, RocketStationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RocketStationRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), RocketStationRecipe::recipeItems,
                ItemStack.STREAM_CODEC, RocketStationRecipe::output,
                RocketStationRecipe::new);


        @Override
        public MapCodec<RocketStationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RocketStationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

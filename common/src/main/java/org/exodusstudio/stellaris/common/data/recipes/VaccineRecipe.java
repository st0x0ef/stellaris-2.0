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
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.LaboratoryBlockEntity;
import org.exodusstudio.stellaris.common.data.recipes.input.VaccineInput;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;

import java.util.List;

public record VaccineRecipe(List<Ingredient> ingredients, ItemStack output) implements Recipe<VaccineInput> {

    @Override
    public boolean matches(VaccineInput input, Level level) {
        for (int i = 0; i < ((LaboratoryBlockEntity) input.entity()).getContainerSize() - 1; i++) {
            Stellaris.LOG.error("Testing ingredient {}: {} against {}", i, input.getItem(i).getItem().getDefaultInstance(), ingredients.get(i));
            if (!ingredients.get(i).test(input.getItem(i).getItem().getDefaultInstance())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(VaccineInput input, HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public RecipeSerializer<? extends Recipe<VaccineInput>> getSerializer() {
        return RecipesRegistry.VACCINE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<VaccineInput>> getType() {
        return RecipesRegistry.VACCINE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.ingredients);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static class Serializer implements RecipeSerializer<VaccineRecipe> {

        public static final MapCodec<VaccineRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf(1, 4).fieldOf("ingredients").forGetter(VaccineRecipe::ingredients),
                ItemStack.CODEC.fieldOf("output").forGetter(VaccineRecipe::output)
        ).apply(instance, VaccineRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, VaccineRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), VaccineRecipe::ingredients,
                ItemStack.STREAM_CODEC, VaccineRecipe::output,
                VaccineRecipe::new);


        @Override
        public MapCodec<VaccineRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, VaccineRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

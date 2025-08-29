package org.exodusstudio.stellaris.neoforge.datagen;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StellarisRecipeProvider extends RecipeProvider {
    public StellarisRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new StellarisRecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "Stellaris Recipes";
        }
    }


    @Override
    protected void buildRecipes() {


        shaped(RecipeCategory.MISC, ItemsRegistry.DESH_INGOT.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ItemsRegistry.DESH_INGOT.get());

        // if you will be gen data for armor trims
        // not working for now

    }

    protected void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                               float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                               float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                List<ItemLike> Ingredients, RecipeCategory Category, ItemLike pResult, float Experience, int CookingTime, String Group, String RecipeName) {
        for(ItemLike itemlike : Ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), Category, pResult, Experience, CookingTime, pCookingSerializer, factory).group(Group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, Stellaris.MOD_ID + ":" + getItemName(pResult) + RecipeName + "_" + getItemName(itemlike));
        }
    }
}
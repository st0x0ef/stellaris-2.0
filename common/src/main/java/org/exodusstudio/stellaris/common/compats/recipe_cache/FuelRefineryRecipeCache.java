package org.exodusstudio.stellaris.common.compats.recipe_cache;

import org.exodusstudio.stellaris.common.data.recipes.FuelRefineryRecipe;

import java.util.List;

public class FuelRefineryRecipeCache {
    private static List<FuelRefineryRecipe> recipes = List.of();

    public static void set(List<FuelRefineryRecipe> incoming) {
        recipes = List.copyOf(incoming);
    }

    public static List<FuelRefineryRecipe> get() {
        return recipes;
    }
}

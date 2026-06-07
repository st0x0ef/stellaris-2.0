package org.exodusstudio.stellaris.common.compats.jei.recipe_cache;

import org.exodusstudio.stellaris.common.data.recipes.ElectrolyzeRecipe;

import java.util.List;

public class ElectrolyzerRecipeCache {
    private static List<ElectrolyzeRecipe> recipes = List.of();

    public static void set(List<ElectrolyzeRecipe> incoming) {
        recipes = List.copyOf(incoming);
    }

    public static List<ElectrolyzeRecipe> get() {
        return recipes;
    }
}

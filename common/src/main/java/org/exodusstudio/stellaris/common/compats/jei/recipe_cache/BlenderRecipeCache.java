package org.exodusstudio.stellaris.common.compats.jei.recipe_cache;

import org.exodusstudio.stellaris.common.data.recipes.BlendingRecipe;

import java.util.List;

public class BlenderRecipeCache {
    private static List<BlendingRecipe> recipes = List.of();

    public static void set(List<BlendingRecipe> incoming) {
        recipes = List.copyOf(incoming);
    }

    public static List<BlendingRecipe> get() {
        return recipes;
    }
}

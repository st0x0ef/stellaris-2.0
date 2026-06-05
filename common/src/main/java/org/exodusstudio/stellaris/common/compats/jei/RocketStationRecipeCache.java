package org.exodusstudio.stellaris.common.compats.jei;

import org.exodusstudio.stellaris.common.data.recipes.RocketStationRecipe;

import java.util.List;

public class RocketStationRecipeCache {
    private static List<RocketStationRecipe> recipes = List.of();

    public static void set(List<RocketStationRecipe> incoming) {
        recipes = List.copyOf(incoming);
    }

    public static List<RocketStationRecipe> get() {
        return recipes;
    }
}
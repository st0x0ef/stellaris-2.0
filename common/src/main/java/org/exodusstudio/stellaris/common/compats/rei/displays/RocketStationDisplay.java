package org.exodusstudio.stellaris.common.compats.rei.displays;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import org.exodusstudio.stellaris.common.compats.rei.categories.RocketStationCategory;
import org.exodusstudio.stellaris.common.data.recipes.RocketStationRecipe;

import java.util.List;

public class RocketStationDisplay extends BasicDisplay {

    public RocketStationDisplay(RocketStationRecipe recipe) {
        super(EntryIngredients.ofIngredients(recipe.recipeItems()),
                List.of(EntryIngredients.of(recipe.output())));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return RocketStationCategory.ID;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}

package org.exodusstudio.stellaris.common.compats.rei.displays;

import dev.architectury.fluid.FluidStackTemplate;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import org.exodusstudio.stellaris.common.compats.rei.categories.ElectrolyzerCategory;
import org.exodusstudio.stellaris.common.data.recipes.ElectrolyzeRecipe;

import java.util.List;

public class ElectrolyzeDisplay extends BasicDisplay {

    private final ElectrolyzeRecipe recipe;

    public ElectrolyzeDisplay(ElectrolyzeRecipe recipe) {
        super(List.of(fluid(recipe.ingredientStack())),
                recipe.resultStacks().stream().map(ElectrolyzeDisplay::fluid).toList());
        this.recipe = recipe;
    }

    private static EntryIngredient fluid(FluidStackTemplate template) {
        return EntryIngredients.ofFluidHolder(template.fluid(), template.amount());
    }

    public ElectrolyzeRecipe recipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ElectrolyzerCategory.ID;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}

package org.exodusstudio.stellaris.common.compats.rei.displays;

import dev.architectury.fluid.FluidStackTemplate;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import org.exodusstudio.stellaris.common.compats.rei.categories.FuelRefineryCategory;
import org.exodusstudio.stellaris.common.data.recipes.FuelRefineryRecipe;

import java.util.List;

public class FuelRefineryDisplay extends BasicDisplay {

    private final FuelRefineryRecipe recipe;

    public FuelRefineryDisplay(FuelRefineryRecipe recipe) {
        super(List.of(fluid(recipe.ingredientStack())),
                List.of(fluid(recipe.fuelStack()), fluid(recipe.dieselStack())));
        this.recipe = recipe;
    }

    private static EntryIngredient fluid(FluidStackTemplate template) {
        return EntryIngredients.ofFluidHolder(template.fluid(), template.amount());
    }

    public FuelRefineryRecipe recipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return FuelRefineryCategory.ID;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}

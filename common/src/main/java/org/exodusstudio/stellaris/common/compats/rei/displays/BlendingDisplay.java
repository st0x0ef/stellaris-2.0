package org.exodusstudio.stellaris.common.compats.rei.displays;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import org.exodusstudio.stellaris.common.compats.rei.categories.BlenderCategory;
import org.exodusstudio.stellaris.common.data.recipes.BlendingRecipe;

import java.util.List;

public class BlendingDisplay extends BasicDisplay {

    public BlendingDisplay(BlendingRecipe recipe) {
        super(ingredientsOf(recipe), List.of(EntryIngredients.of(recipe.result())));
    }

    private static List<EntryIngredient> ingredientsOf(BlendingRecipe recipe) {
        return recipe.ingredients().stream()
                .map(sized -> EntryIngredients.ofItemStacks(sized.ingredient().display()
                        .resolveForStacks(EntryIngredients.slotDisplayContext())
                        .stream()
                        .map(stack -> stack.copyWithCount(sized.count()))
                        .toList()))
                .toList();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BlenderCategory.ID;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}

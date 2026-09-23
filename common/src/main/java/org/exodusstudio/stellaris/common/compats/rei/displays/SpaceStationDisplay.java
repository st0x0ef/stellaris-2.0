package org.exodusstudio.stellaris.common.compats.rei.displays;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.compats.rei.categories.SpaceStationCategory;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;

import java.util.ArrayList;
import java.util.List;

public class SpaceStationDisplay extends BasicDisplay {

    private final List<EntryIngredient> materials;

    public SpaceStationDisplay(SpaceStationRecipe recipe) {
        this(materialsOf(recipe), recipe);
    }

    private SpaceStationDisplay(List<EntryIngredient> materials, SpaceStationRecipe recipe) {
        super(append(materials, EntryIngredients.of(blueprint())),
                List.of(EntryIngredients.of(stamped(recipe))));
        this.materials = materials;
    }

    public List<EntryIngredient> materials() {
        return materials;
    }

    private static List<EntryIngredient> materialsOf(SpaceStationRecipe recipe) {
        return recipe.items().stream()
                .limit(SpaceStationCategory.MATERIAL_SLOTS)
                .map(SpaceStationDisplay::stacksFor)
                .map(EntryIngredients::ofItemStacks)
                .toList();
    }

    private static List<ItemStack> stacksFor(SpaceStationRecipe.IngredientWithCount ingredient) {
        List<ItemStack> stacks = new ArrayList<>();

        ingredient.itemRef().ifLeft(itemKey -> BuiltInRegistries.ITEM.get(itemKey)
                .ifPresent(holder -> stacks.add(new ItemStack(holder, ingredient.count()))));

        ingredient.itemRef().ifRight(tagKey -> BuiltInRegistries.ITEM.get(tagKey).ifPresent(tag -> {
            for (var holder : tag) {
                stacks.add(new ItemStack(holder, ingredient.count()));
            }
        }));

        return stacks;
    }

    private static ItemStack blueprint() {
        return ItemsRegistry.SPACE_STATION_BLUEPRINT.get().getDefaultInstance();
    }

    private static ItemStack stamped(SpaceStationRecipe recipe) {
        ItemStack planned = blueprint();
        planned.set(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get(), recipe);
        return planned;
    }

    private static List<EntryIngredient> append(List<EntryIngredient> ingredients, EntryIngredient last) {
        List<EntryIngredient> all = new ArrayList<>(ingredients);
        all.add(last);
        return all;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceStationCategory.ID;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}

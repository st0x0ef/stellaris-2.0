package org.exodusstudio.stellaris.common.compats.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.data.recipes.RocketStationRecipe;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record RocketStationCategory(IGuiHelper guiHelper, IDrawable background) implements IRecipeCategory<RocketStationRecipe> {

    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/rocket_station");
    public static final IRecipeType<RocketStationRecipe> RECIPE = IRecipeType.create(IdentifierUtils.id("rocket_station"), RocketStationRecipe.class);

    public static RocketStationCategory create(IGuiHelper guiHelper) {
        IDrawable background = guiHelper.createDrawable(TEXTURE, 0, 0, 180, 147);
        return new RocketStationCategory(guiHelper, background);
    }

    @Override
    public IRecipeType<RocketStationRecipe> getRecipeType() {
        return RECIPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("stellaris.screen.engineering_station");
    }

    @Override
    public int getWidth() {
        return 180;
    }

    @Override
    public int getHeight() {
        return 147;
    }

    @Override
    public void draw(RocketStationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableItemStack(BlocksRegistry.ENGINEERING_STATION.item().get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RocketStationRecipe recipe, IFocusGroup focuses) {
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).add(BlocksRegistry.ENGINEERING_STATION.item().get());

        inputSlotAdder(builder, recipe, 63, 20, 0);
        inputSlotAdder(builder, recipe, 54, 38, 1);
        inputSlotAdder(builder, recipe, 72, 38, 2);
        inputSlotAdder(builder, recipe, 54, 56, 3);
        inputSlotAdder(builder, recipe, 72, 56, 4);
        inputSlotAdder(builder, recipe, 54, 74, 5);
        inputSlotAdder(builder, recipe, 72, 74, 6);
        inputSlotAdder(builder, recipe, 36, 92, 7);
        inputSlotAdder(builder, recipe, 54, 92, 8);
        inputSlotAdder(builder, recipe, 72, 92, 9);
        inputSlotAdder(builder, recipe, 90, 92, 10);
        inputSlotAdder(builder, recipe, 36, 110, 11);
        inputSlotAdder(builder, recipe, 63, 110, 12);
        inputSlotAdder(builder, recipe, 90, 110, 13);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 122, 54).add(recipe.output());
    }

    private static void inputSlotAdder(IRecipeLayoutBuilder builder, RocketStationRecipe recipe, int x, int y, int index) {
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y);
        if (index < recipe.recipeItems().size()) {
            slot.add(recipe.recipeItems().get(index));
        }
    }
}

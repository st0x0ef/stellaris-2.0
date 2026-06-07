package org.exodusstudio.stellaris.common.compats.jei.categories;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
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
import org.exodusstudio.stellaris.common.data.recipes.ElectrolyzeRecipe;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record ElectrolyzerCategory(IGuiHelper guiHelper, IDrawable background) implements IRecipeCategory<ElectrolyzeRecipe> {

    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/electrolyzer");
    public static final IRecipeType<ElectrolyzeRecipe> RECIPE = IRecipeType.create(IdentifierUtils.id("electrolyze"), ElectrolyzeRecipe.class);

    private static final int WIDTH = 180;
    private static final int HEIGHT = 147;
    private static final long INPUT_CAPACITY = 3000;
    private static final long OUTPUT_CAPACITY = 6000;

    public static ElectrolyzerCategory create(IGuiHelper guiHelper) {
        IDrawable background = guiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        return new ElectrolyzerCategory(guiHelper, background);
    }

    @Override
    public IRecipeType<ElectrolyzeRecipe> getRecipeType() {
        return RECIPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("item.stellaris.electrolyzer");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableItemStack(BlocksRegistry.ELECTROLYZER.item().get().getDefaultInstance());
    }

    @Override
    public void draw(ElectrolyzeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ElectrolyzeRecipe recipe, IFocusGroup focuses) {
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).add(BlocksRegistry.ELECTROLYZER.item().get());

        builder.addSlot(RecipeIngredientRole.INPUT, 53, 54)
                .add(recipe.ingredientStack().fluid().value(), recipe.ingredientStack().amount())
                .setFluidRenderer(INPUT_CAPACITY, false, 76, 46);

        if (!recipe.resultStacks().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 22, 54)
                    .add(recipe.resultStacks().getFirst().fluid().value(), recipe.resultStacks().getFirst().amount())
                    .setFluidRenderer(OUTPUT_CAPACITY, false, 12, 46);
        }

        if (recipe.resultStacks().size() >= 2) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 146, 54)
                    .add(recipe.resultStacks().get(1).fluid().value(), recipe.resultStacks().get(1).amount())
                    .setFluidRenderer(OUTPUT_CAPACITY, false, 12, 46);
        }
    }
}

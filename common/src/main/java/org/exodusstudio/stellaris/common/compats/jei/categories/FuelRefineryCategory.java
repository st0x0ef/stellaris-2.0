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
import org.exodusstudio.stellaris.common.data.recipes.FuelRefineryRecipe;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record FuelRefineryCategory(IGuiHelper guiHelper, IDrawable background) implements IRecipeCategory<FuelRefineryRecipe> {

    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/fuel_refinery");
    public static final IRecipeType<FuelRefineryRecipe> RECIPE = IRecipeType.create(IdentifierUtils.id("fuel_refinery"), FuelRefineryRecipe.class);

    private static final int WIDTH = 180;
    private static final int HEIGHT = 147;
    private static final long TANK_CAPACITY = 10000;

    public static FuelRefineryCategory create(IGuiHelper guiHelper) {
        IDrawable background = guiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        return new FuelRefineryCategory(guiHelper, background);
    }

    @Override
    public IRecipeType<FuelRefineryRecipe> getRecipeType() {
        return RECIPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("item.stellaris.fuel_refinery");
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
        return guiHelper.createDrawableItemStack(BlocksRegistry.FUEL_REFINERY.item().get().getDefaultInstance());
    }

    @Override
    public void draw(FuelRefineryRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FuelRefineryRecipe recipe, IFocusGroup focuses) {
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).add(BlocksRegistry.FUEL_REFINERY.item().get());

        builder.addSlot(RecipeIngredientRole.INPUT, 42, 78)
                .add(recipe.ingredientStack().fluid().value(), recipe.ingredientStack().amount())
                .setFluidRenderer(TANK_CAPACITY, false, 12, 46);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 78)
                .add(recipe.fuelStack().fluid().value(), recipe.fuelStack().amount())
                .setFluidRenderer(TANK_CAPACITY, false, 12, 46);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 78)
                .add(recipe.dieselStack().fluid().value(), recipe.dieselStack().amount())
                .setFluidRenderer(TANK_CAPACITY, false, 12, 46);
    }
}

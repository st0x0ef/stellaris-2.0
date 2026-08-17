package org.exodusstudio.stellaris.common.compats.jei.categories;

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
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.BlenderBlockEntity;
import org.exodusstudio.stellaris.common.data.recipes.BlendingRecipe;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record BlenderCategory(IGuiHelper guiHelper, IDrawable background) implements IRecipeCategory<BlendingRecipe> {

    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/blender");
    public static final IRecipeType<BlendingRecipe> RECIPE = IRecipeType.create(IdentifierUtils.id("blending"), BlendingRecipe.class);

    private static final int WIDTH = 180;
    private static final int HEIGHT = 111;

    private static final int GRID_X = 34;
    private static final int GRID_Y = 40;
    private static final int SLOT_SIZE = 18;
    private static final int RESULT_X = 126;
    private static final int RESULT_Y = 58;

    public static BlenderCategory create(IGuiHelper guiHelper) {
        IDrawable background = guiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        return new BlenderCategory(guiHelper, background);
    }

    @Override
    public IRecipeType<BlendingRecipe> getRecipeType() {
        return RECIPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("item.stellaris.blender");
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
        return guiHelper.createDrawableItemStack(BlocksRegistry.BLENDER.item().get().getDefaultInstance());
    }

    @Override
    public void draw(BlendingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BlendingRecipe recipe, IFocusGroup focuses) {
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).add(BlocksRegistry.BLENDER.item().get());

        for (int index = 0; index < BlenderBlockEntity.INPUT_SLOT_COUNT; index++) {
            int x = GRID_X + (index % BlenderBlockEntity.GRID_WIDTH) * SLOT_SIZE;
            int y = GRID_Y + (index / BlenderBlockEntity.GRID_WIDTH) * SLOT_SIZE;
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y);

            if (index < recipe.ingredients().size()) {
                BlendingRecipe.SizedIngredient sized = recipe.ingredients().get(index);
                slot.addItemStacks(sized.ingredient().items()
                        .map(item -> new ItemStack(item, sized.count()))
                        .toList());
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, RESULT_X, RESULT_Y).add(recipe.result());
    }
}

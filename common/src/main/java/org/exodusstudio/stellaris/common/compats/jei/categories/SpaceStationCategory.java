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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;

public record SpaceStationCategory(IGuiHelper guiHelper, IDrawable background) implements IRecipeCategory<SpaceStationRecipe> {

    public static final IRecipeType<SpaceStationRecipe> RECIPE =
            IRecipeType.create(IdentifierUtils.id("space_station"), SpaceStationRecipe.class);

    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/space_station");

    private static final int WIDTH = 180;
    private static final int HEIGHT = 100;
    /** Eight of the nine grid cells hold materials; the ninth holds the blueprint. */
    private static final int MATERIAL_SLOTS = 8;
    /** Top-left cell of the 3x3 grid drawn in the texture, and the pitch between cells. */
    private static final int GRID_X = 24;
    private static final int GRID_Y = 24;
    private static final int GRID_PITCH = 18;
    private static final int OUTPUT_X = 120;
    private static final int OUTPUT_Y = 42;

    public static SpaceStationCategory create(IGuiHelper guiHelper) {
        return new SpaceStationCategory(guiHelper, guiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT));
    }

    @Override
    public IRecipeType<SpaceStationRecipe> getRecipeType() {
        return RECIPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("stellaris.screen.space_station_planner");
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
    public void draw(SpaceStationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableItemStack(ItemsRegistry.SPACE_STATION_BLUEPRINT.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SpaceStationRecipe recipe, IFocusGroup focuses) {
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).add(BlocksRegistry.ENGINEERING_STATION.item().get());

        // The planner is one 3x3 grid holding the eight materials and the blueprint together, so the
        // last cell is the blueprint and never a material.
        for (int index = 0; index < MATERIAL_SLOTS; index++) {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, gridX(index), gridY(index));

            if (index < recipe.items().size()) {
                slot.addItemStacks(stacksFor(recipe.items().get(index)));
            }
        }

        // A blank blueprint is consumed along with the materials, so it is an ingredient, not a result.
        builder.addSlot(RecipeIngredientRole.INPUT, gridX(MATERIAL_SLOTS), gridY(MATERIAL_SLOTS))
                .add(ItemsRegistry.SPACE_STATION_BLUEPRINT.get().getDefaultInstance());

        // What you take out of the result slot is that blueprint, stamped with the station it plans.
        ItemStack planned = ItemsRegistry.SPACE_STATION_BLUEPRINT.get().getDefaultInstance();
        planned.set(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get(), recipe);

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .add(planned);
    }

    private static int gridX(int index) {
        return GRID_X + (index % 3) * GRID_PITCH;
    }

    private static int gridY(int index) {
        return GRID_Y + (index / 3) * GRID_PITCH;
    }

    /** An ingredient is either one item or a tag, and a tag shows every item it accepts. */
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
}

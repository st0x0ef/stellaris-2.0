package org.exodusstudio.stellaris.common.compats.rei.categories;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.compats.rei.REIWidgets;
import org.exodusstudio.stellaris.common.compats.rei.displays.ElectrolyzeDisplay;
import org.exodusstudio.stellaris.common.data.recipes.ElectrolyzeRecipe;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;

public class ElectrolyzerCategory implements DisplayCategory<ElectrolyzeDisplay> {

    public static final CategoryIdentifier<ElectrolyzeDisplay> ID = CategoryIdentifier.of(IdentifierUtils.id("electrolyze"));
    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/electrolyzer");

    private static final int WIDTH = 180;
    private static final int HEIGHT = 147;
    private static final long INPUT_CAPACITY = 3000;
    private static final long OUTPUT_CAPACITY = 6000;
    private static final int TANK_Y = 54;
    private static final int TANK_HEIGHT = 46;
    private static final int OUTPUT_TANK_WIDTH = 12;

    @Override
    public CategoryIdentifier<? extends ElectrolyzeDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("item.stellaris.electrolyzer");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BlocksRegistry.ELECTROLYZER.item().get());
    }

    @Override
    public int getDisplayWidth(ElectrolyzeDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(ElectrolyzeDisplay display, Rectangle bounds) {
        Point origin = REIWidgets.origin(bounds, WIDTH, HEIGHT);
        ElectrolyzeRecipe recipe = display.recipe();

        List<Widget> widgets = new ArrayList<>();
        widgets.add(REIWidgets.background(TEXTURE, origin, WIDTH, HEIGHT));

        widgets.add(REIWidgets.tank(origin, 53, TANK_Y, 76, TANK_HEIGHT).markInput()
                .entry(REIWidgets.fluid(recipe.ingredientStack(), INPUT_CAPACITY)));

        if (!recipe.resultStacks().isEmpty()) {
            widgets.add(REIWidgets.tank(origin, 22, TANK_Y, OUTPUT_TANK_WIDTH, TANK_HEIGHT).markOutput()
                    .entry(REIWidgets.fluid(recipe.resultStacks().getFirst(), OUTPUT_CAPACITY)));
        }

        if (recipe.resultStacks().size() >= 2) {
            widgets.add(REIWidgets.tank(origin, 146, TANK_Y, OUTPUT_TANK_WIDTH, TANK_HEIGHT).markOutput()
                    .entry(REIWidgets.fluid(recipe.resultStacks().get(1), OUTPUT_CAPACITY)));
        }

        return widgets;
    }
}

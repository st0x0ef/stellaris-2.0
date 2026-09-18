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
import org.exodusstudio.stellaris.common.compats.rei.displays.FuelRefineryDisplay;
import org.exodusstudio.stellaris.common.data.recipes.FuelRefineryRecipe;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;

public class FuelRefineryCategory implements DisplayCategory<FuelRefineryDisplay> {

    public static final CategoryIdentifier<FuelRefineryDisplay> ID = CategoryIdentifier.of(IdentifierUtils.id("fuel_refinery"));
    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/fuel_refinery");

    private static final int WIDTH = 180;
    private static final int HEIGHT = 147;
    private static final long TANK_CAPACITY = 10000;
    private static final int TANK_WIDTH = 12;
    private static final int TANK_HEIGHT = 46;
    private static final int TANK_Y = 78;

    @Override
    public CategoryIdentifier<? extends FuelRefineryDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("item.stellaris.fuel_refinery");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BlocksRegistry.FUEL_REFINERY.item().get());
    }

    @Override
    public int getDisplayWidth(FuelRefineryDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(FuelRefineryDisplay display, Rectangle bounds) {
        Point origin = REIWidgets.origin(bounds, WIDTH, HEIGHT);
        FuelRefineryRecipe recipe = display.recipe();

        List<Widget> widgets = new ArrayList<>();
        widgets.add(REIWidgets.background(TEXTURE, origin, WIDTH, HEIGHT));

        widgets.add(tank(origin, 42).markInput()
                .entry(REIWidgets.fluid(recipe.ingredientStack(), TANK_CAPACITY)));
        widgets.add(tank(origin, 80).markOutput()
                .entry(REIWidgets.fluid(recipe.fuelStack(), TANK_CAPACITY)));
        widgets.add(tank(origin, 128).markOutput()
                .entry(REIWidgets.fluid(recipe.dieselStack(), TANK_CAPACITY)));

        return widgets;
    }

    private static me.shedaniel.rei.api.client.gui.widgets.Slot tank(Point origin, int x) {
        return REIWidgets.tank(origin, x, TANK_Y, TANK_WIDTH, TANK_HEIGHT);
    }
}

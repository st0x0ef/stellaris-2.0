package org.exodusstudio.stellaris.common.compats.rei.categories;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.compats.rei.REIWidgets;
import org.exodusstudio.stellaris.common.compats.rei.displays.SpaceStationDisplay;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;

public class SpaceStationCategory implements DisplayCategory<SpaceStationDisplay> {

    public static final CategoryIdentifier<SpaceStationDisplay> ID = CategoryIdentifier.of(IdentifierUtils.id("space_station"));
    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/space_station");

    public static final int MATERIAL_SLOTS = 8;

    private static final int WIDTH = 180;
    private static final int HEIGHT = 100;
    private static final int GRID_X = 24;
    private static final int GRID_Y = 24;
    private static final int GRID_PITCH = 18;
    private static final int OUTPUT_X = 120;
    private static final int OUTPUT_Y = 42;

    @Override
    public CategoryIdentifier<? extends SpaceStationDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("stellaris.screen.space_station_planner");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ItemsRegistry.SPACE_STATION_BLUEPRINT.get());
    }

    @Override
    public int getDisplayWidth(SpaceStationDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(SpaceStationDisplay display, Rectangle bounds) {
        Point origin = REIWidgets.origin(bounds, WIDTH, HEIGHT);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(REIWidgets.background(TEXTURE, origin, WIDTH, HEIGHT));

        List<EntryIngredient> materials = display.materials();
        for (int index = 0; index < MATERIAL_SLOTS; index++) {
            Slot slot = REIWidgets.slot(origin, gridX(index), gridY(index)).markInput();

            if (index < materials.size()) {
                slot.entries(materials.get(index));
            }

            widgets.add(slot);
        }

        widgets.add(REIWidgets.slot(origin, gridX(MATERIAL_SLOTS), gridY(MATERIAL_SLOTS))
                .markInput()
                .entries(display.getInputEntries().getLast()));

        widgets.add(REIWidgets.slot(origin, OUTPUT_X, OUTPUT_Y)
                .markOutput()
                .entries(display.getOutputEntries().getFirst()));

        return widgets;
    }

    private static int gridX(int index) {
        return GRID_X + (index % 3) * GRID_PITCH;
    }

    private static int gridY(int index) {
        return GRID_Y + (index / 3) * GRID_PITCH;
    }
}

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
import org.exodusstudio.stellaris.common.compats.rei.displays.RocketStationDisplay;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;

public class RocketStationCategory implements DisplayCategory<RocketStationDisplay> {

    public static final CategoryIdentifier<RocketStationDisplay> ID = CategoryIdentifier.of(IdentifierUtils.id("rocket_station"));
    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/rocket_station");

    private static final int WIDTH = 180;
    private static final int HEIGHT = 147;
    private static final int[][] INPUT_SLOTS = {
            {63, 20},
            {54, 38}, {72, 38},
            {54, 56}, {72, 56},
            {54, 74}, {72, 74},
            {36, 92}, {54, 92}, {72, 92}, {90, 92},
            {36, 110}, {63, 110}, {90, 110}
    };
    private static final int OUTPUT_X = 122;
    private static final int OUTPUT_Y = 54;

    @Override
    public CategoryIdentifier<? extends RocketStationDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("stellaris.screen.engineering_station");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BlocksRegistry.ENGINEERING_STATION.item().get());
    }

    @Override
    public int getDisplayWidth(RocketStationDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(RocketStationDisplay display, Rectangle bounds) {
        Point origin = REIWidgets.origin(bounds, WIDTH, HEIGHT);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(REIWidgets.background(TEXTURE, origin, WIDTH, HEIGHT));

        List<EntryIngredient> inputs = display.getInputEntries();
        for (int index = 0; index < INPUT_SLOTS.length; index++) {
            Slot slot = REIWidgets.slot(origin, INPUT_SLOTS[index][0], INPUT_SLOTS[index][1]).markInput();

            if (index < inputs.size()) {
                slot.entries(inputs.get(index));
            }

            widgets.add(slot);
        }

        widgets.add(REIWidgets.slot(origin, OUTPUT_X, OUTPUT_Y)
                .markOutput()
                .entries(display.getOutputEntries().getFirst()));

        return widgets;
    }
}

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
import org.exodusstudio.stellaris.common.blocks.entities.machines.BlenderBlockEntity;
import org.exodusstudio.stellaris.common.compats.rei.REIWidgets;
import org.exodusstudio.stellaris.common.compats.rei.displays.BlendingDisplay;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;

public class BlenderCategory implements DisplayCategory<BlendingDisplay> {

    public static final CategoryIdentifier<BlendingDisplay> ID = CategoryIdentifier.of(IdentifierUtils.id("blending"));
    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("recipe_viewer/blender");

    private static final int WIDTH = 180;
    private static final int HEIGHT = 111;

    private static final int GRID_X = 34;
    private static final int GRID_Y = 40;
    private static final int SLOT_SIZE = 18;
    private static final int RESULT_X = 126;
    private static final int RESULT_Y = 58;

    @Override
    public CategoryIdentifier<? extends BlendingDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("item.stellaris.blender");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BlocksRegistry.BLENDER.item().get());
    }

    @Override
    public int getDisplayWidth(BlendingDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(BlendingDisplay display, Rectangle bounds) {
        Point origin = REIWidgets.origin(bounds, WIDTH, HEIGHT);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(REIWidgets.background(TEXTURE, origin, WIDTH, HEIGHT));

        List<EntryIngredient> inputs = display.getInputEntries();
        for (int index = 0; index < BlenderBlockEntity.INPUT_SLOT_COUNT; index++) {
            int x = GRID_X + (index % BlenderBlockEntity.GRID_WIDTH) * SLOT_SIZE;
            int y = GRID_Y + (index / BlenderBlockEntity.GRID_WIDTH) * SLOT_SIZE;
            Slot slot = REIWidgets.slot(origin, x, y).markInput();

            if (index < inputs.size()) {
                slot.entries(inputs.get(index));
            }

            widgets.add(slot);
        }

        widgets.add(REIWidgets.slot(origin, RESULT_X, RESULT_Y)
                .markOutput()
                .entries(display.getOutputEntries().getFirst()));

        return widgets;
    }
}

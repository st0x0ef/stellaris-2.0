package org.exodusstudio.stellaris.client.screens.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.components.containers.DraggableContainer;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.FluidOutputManager;

import java.util.ArrayList;
import java.util.HashMap;

public class FluidOutputManagerWidget extends DraggableContainer {

    public FluidOutputManager fluidOutputManager;
    public Screen screen;
    private final HashMap<Direction, Button> directionButtons = new HashMap<>();

    public FluidOutputManagerWidget(int x, int y, int width, int height, FluidOutputManager fluidOutputManager, Screen screen) {
        super(x, y, width, height);
        this.fluidOutputManager = fluidOutputManager;
        this.screen = screen;
    }

    @Override
    public FluidOutputManagerWidget addDefaultChildren() {
        ArrayList<AbstractWidget> newChildren = new ArrayList<>();

        int i = 0;
        for (Direction direction : Direction.values()) {
            final Direction dir = direction;
            Button.Builder buttonToAdd = new Button.Builder(Component.literal(Direction.values()[i].getName()), button -> {
                Stellaris.LOG.error(fluidOutputManager.outputs.get(dir).toString());
            });
            if (i < 3) {
                buttonToAdd.bounds(getX() + 10 + 22 * i, getY() + 22 * 2, 20, 20);
            } else {
                if (i == 5) i++;
                buttonToAdd.bounds(getX() + 10 + 22, getY() + (i - 3) * 22, 20, 20);
            }
            Button button = buttonToAdd.build();
            directionButtons.put(dir, button);
            newChildren.add(button);
            i++;
        }
        newChildren.forEach((b) -> this.screen.addRenderableWidget(b));
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        // Mise à jour dynamique des tooltips à chaque frame
        for (Direction direction : directionButtons.keySet()) {
            Button button = directionButtons.get(direction);
            var storage = fluidOutputManager.outputs.get(direction);
            if (storage != null) {
                button.setTooltip(Tooltip.create(
                    Component.literal("Output: ")
                        .append(storage.getFluidInTank(0).getName())
                        .append(Component.literal("\n" + direction.toString()))
                ));
            }
        }
    }
}

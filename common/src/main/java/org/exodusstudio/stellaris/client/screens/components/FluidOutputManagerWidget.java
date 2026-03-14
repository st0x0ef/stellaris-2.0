package org.exodusstudio.stellaris.client.screens.components;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.components.containers.DraggableContainer;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.FluidOutputManager;

import java.util.ArrayList;

public class FluidOutputManagerWidget extends DraggableContainer {

    public FluidOutputManager fluidOutputManager;
    public Screen screen;
    public FluidOutputManagerWidget(int x, int y, int width, int height, FluidOutputManager fluidOutputManager, Screen screen) {
        super(x, y, width, height);
        this.fluidOutputManager = fluidOutputManager;
        this.screen = screen;
    }

    @Override
    public FluidOutputManagerWidget addDefaultChildren() {
        ArrayList<AbstractWidget> newChildren = new ArrayList<>();


        for(int i = 0; i < Direction.values().length; i++) {
            int finalI = i;

            Button.Builder buttonToAdd = new Button.Builder(Component.literal(Direction.values()[i].getName()), button -> {
                Stellaris.LOG.error(fluidOutputManager.outputs.get(Direction.values()[finalI]).toString());

            });
            if (i < 3) {

                buttonToAdd.bounds(getX() + 10 + 22 * i, getY() + 22 * 2 , 20, 20);
            } else {

                int yIndex = i;

                if(i == 5) yIndex++;

                buttonToAdd.bounds(getX() + 10 + 22, getY() + (yIndex - 3) * 22 , 20, 20).build();

            }
            newChildren.add(buttonToAdd.build());
        }

        newChildren.forEach((b) -> this.screen.addRenderableWidget(b));
        return this;
    }
}

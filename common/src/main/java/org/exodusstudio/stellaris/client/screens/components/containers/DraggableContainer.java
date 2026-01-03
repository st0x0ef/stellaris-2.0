package org.exodusstudio.stellaris.client.screens.components.containers;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * Utility class to make Draggable Container.
 */
public class DraggableContainer extends BasicContainer {

    public int dragOffsetX = 0;
    public int dragOffsetY = 0;

    public DraggableContainer(int baseX, int baseY, int width, int height, AbstractWidget... children) {
        super(baseX, baseY, width, height, children);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if(this.isHovered()) {
            this.dragOffsetX += (int) event.x();
            this.dragOffsetY += (int) event.y();

            updateChildrenPosition();

            this.dragOffsetX = 0;
            this.dragOffsetY = 0;

            return true; // Indicate that the mouse was dragged
        }

        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public void updateChildrenPosition() {
        for (AbstractWidget child : this.children) {
            int childX = child.getX() + this.dragOffsetX;
            int childY = child.getY() + this.dragOffsetY;
            child.setX(childX);
            child.setY(childY);
        }
    }
}

package org.exodusstudio.stellaris.client.screen.components.containers;

import net.minecraft.client.gui.components.AbstractWidget;

public class DraggableContainer extends BasicContainer {

    public int dragOffsetX = 0;
    public int dragOffsetY = 0;


    public DraggableContainer(int baseX, int baseY, int width, int height, AbstractWidget... children) {
        super(baseX, baseY, width, height, children);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {

        if(this.isHovered()) {
            this.dragOffsetX += dragX;
            this.dragOffsetY += dragY;

            updateChildrenPosition();

            this.dragOffsetX = 0;
            this.dragOffsetY = 0;

            return true; // Indicate that the mouse was dragged
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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

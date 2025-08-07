package org.exodusstudio.stellaris.client.screen.components.containers;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class ScrollableContainer extends BasicContainer {

    private int scrollOffset = 0;

    public ScrollableContainer(int baseX, int baseY, int width, int height, AbstractWidget... children) {
        super(baseX, baseY, width, height, children);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scrollOffset += scrollY;
        updateChildrenPosition();
        return false;
    }

    @Override
    public void updateChildrenPosition() {
        int y = this.scrollOffset;
        for (AbstractWidget child : this.children) {
            int childHeight = child.getHeight();
            child.setY(childHeight + y);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

}

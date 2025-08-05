package org.exodusstudio.stellaris.client.screen.components.containers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class ScrollableContainer<C extends AbstractWidget> extends BasicContainer<C> {

    private int scrollOffset = 0;

    public ScrollableContainer(int baseX, int baseY, int width, int height, C... children) {
        super(baseX, baseY, width, height, children);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

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
        for (C child : this.children) {
            int childHeight = child.getHeight();
            child.setY(childHeight + y);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

}

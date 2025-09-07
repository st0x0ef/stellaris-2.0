package org.exodusstudio.stellaris.client.screens.components.containers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.exodusstudio.stellaris.Stellaris;

import java.util.Collection;

public class ScrollableContainer extends BasicContainer {

    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, "icon/scroller");
    private static final ResourceLocation SCROLLER_SPRITE_HOVER = ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, "icon/scroller_hover");

    private int scrollOffset = 0;
    private int minOffset = -16;
    private boolean showScrollbar = true;


    public ScrollableContainer(int baseX, int baseY, int width, int height, AbstractWidget... children) {
        super(baseX, baseY, width, height, children);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(this.scrollOffset < minOffset) {
            this.scrollOffset = minOffset;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());

        for(AbstractWidget widget : this.children) {
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        guiGraphics.disableScissor();
        guiGraphics.pose().popMatrix();
        renderScrollbar(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(this.canOffset(dragY) && this.isScrollbarHovered(mouseX, mouseY)) {
            this.scrollOffset += dragY * 1.5f;
            updateChildrenPosition();
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.canOffset(scrollY)) {
            this.scrollOffset -= scrollY;
            updateChildrenPosition();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void updateChildrenPosition() {
        int y = this.getY() - this.scrollOffset;
        for (AbstractWidget child : this.children) {
            child.setY(y);
            y += child.getHeight();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }


    @Override
    public ScrollableContainer addChild(Screen parent, AbstractWidget child) {
        parent.addRenderableWidget(child);
        return (ScrollableContainer) this.addChild(child);
    }

    public boolean canOffset(double yOffset) {

        return this.scrollOffset + yOffset >= minOffset;
    }

    protected int scrollerHeight() {
        return 40;
    }

    protected int scrollBarY() {
        return Mth.clamp(getY() + scrollOffset, getY() , getBottom() - scrollerHeight());
    }

    public boolean isScrollbarHovered(double mouseX, double mouseY) {
        int scrollbarX = this.getRight() - 10;
        int scrollbarY = this.scrollBarY();
        return mouseX >= scrollbarX && mouseX <= this.getRight() && mouseY >= scrollbarY && mouseY <= scrollbarY + this.scrollerHeight();
    }

    private void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.showScrollbar) {
            int i = this.getRight() - 10;
            int j = this.scrollerHeight();
            int k = this.scrollBarY();
            if(this.isScrollbarHovered(mouseX, mouseY)) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_SPRITE_HOVER, i, k, 10, j);
            } else {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_SPRITE, i, k, 10, j);
            }
        }
    }

    public ScrollableContainer setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
        return this;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }
}

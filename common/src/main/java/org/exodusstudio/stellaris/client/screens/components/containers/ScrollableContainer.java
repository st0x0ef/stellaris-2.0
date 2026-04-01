package org.exodusstudio.stellaris.client.screens.components.containers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.client.screens.components.Padding;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScrollableContainer extends AbstractScrollArea implements ContainerEventHandler {
    public HashMap<AbstractWidget, Integer> defaultPositions = new HashMap<>();
    public ArrayList<AbstractWidget> children = new ArrayList<>();
    public int contentHeight = 0;
    public RenderInfo onRender;
    public double scrollRate = 7;
    public boolean allowScrollingOnChildren = true;

    private Identifier scrollerSprite = IdentifierUtils.id("icon/scroller");
    @Nullable
    private Identifier scrollerBackground;
    @Nullable
    private Identifier background;

    public Padding padding = new Padding(0);

    public ScrollableContainer(int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
    }

    @Override
    protected int contentHeight() {
        // Include vertical padding in the reported content height
        return contentHeight + padding.top + padding.bottom;
    }



    @Override
    protected double scrollRate() {
        return scrollRate;
    }

    @Override
    public void setScrollAmount(double scrollAmount) {
        super.setScrollAmount(scrollAmount);
        updateChildrenPosition();
    }

    public void updateChildrenPosition() {
        for (AbstractWidget child : this.children) {
            // Apply vertical padding when positioning children
            Integer defaultY = this.defaultPositions.get(child);
            if (defaultY == null) continue;
            child.setY((int) (defaultY - this.scrollAmount()));
        }
    }


    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        if(this.background != null) guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.background, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());


        guiGraphics.pose().pushMatrix();
        guiGraphics.enableScissor(this.getX() + this.padding.left,
                this.getY() + this.padding.left, this.getRight() - this.padding.right, this.getBottom() - this.padding.bottom);


        renderContent(guiGraphics, mouseX, mouseY, partialTick);
        renderScrollbar(guiGraphics, mouseX, mouseY);

        guiGraphics.disableScissor();
        guiGraphics.pose().popMatrix();


    }

    public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for(AbstractWidget widget : this.children) {
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        if(onRender != null) {
            onRender.render(this, guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    /**
     * We Override this to allow changing textures
     */
    @Override
    protected void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.scrollbarVisible()) {
            int i = this.scrollBarX() - this.padding.right;
            int j = this.scrollerHeight() + this.padding.top;
            int k = this.scrollBarY();
            if(this.scrollerBackground != null) guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.scrollerBackground, i, this.getY(), 6, this.getHeight());
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.scrollerSprite, i, k, 6, j);
        }
    }

    @Override
    public boolean isHoveredOrFocused() {

        boolean bl = false;
        if(allowScrollingOnChildren) {
            for(AbstractWidget widget : this.children) {
                if(widget.isHoveredOrFocused()) {
                    bl = true;
                    break;
                }
            }
        }

        return super.isHoveredOrFocused() || bl;
    }

    public double getOffsetHeight() {
        return this.getY() - this.scrollAmount();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean isDragging) {

    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        for(AbstractWidget widget : this.children) {
            if(widget.isFocused()) return widget;
        }
        return null;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        for(AbstractWidget widget : this.children) {
            widget.setFocused(focused == widget);
        }
    }



    /** Builder */
    public ScrollableContainer setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
        return this;
    }

    /**
     * This allows to render things but without attaching child widget.
     * @param info the things to render.
     * @return the container
     */
    public ScrollableContainer setRender(RenderInfo info) {
        this.onRender = info;
        return this;
    }

    public ScrollableContainer setScrollRate(double scrollRate) {
        this.scrollRate = scrollRate;
        return this;
    }

    public ScrollableContainer setScrollerTexture(Identifier sprite, @Nullable Identifier background) {
        this.scrollerSprite = sprite;
        this.scrollerBackground = background;
        return this;
    }

    public ScrollableContainer setBackground(@Nullable Identifier background) {
        this.background = background;
        return this;
    }

    /**
     * Add a child to the container. We apply current padding to the child position
     */
    public ScrollableContainer addChild(Screen parent, AbstractWidget child) {
        parent.addWidget(child);
        child.setY(child.getY());
        child.setX(child.getX());
        this.children.add(child);
        this.defaultPositions.put(child, child.getY());
        return this;
    }

    public ScrollableContainer setPadding(Padding padding) {
        this.padding = padding;
        return this;
    }

    public Padding getPadding() {
        return this.padding;
    }



    @FunctionalInterface
    public interface RenderInfo {
        void render(ScrollableContainer container,  GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
    }
}

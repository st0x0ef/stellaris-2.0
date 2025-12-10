package org.exodusstudio.stellaris.client.screens.components.containers;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
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

    private ResourceLocation scrollerSprite = ResourceLocationUtils.id("icon/scroller");
    @Nullable
    private ResourceLocation scrollerBackground;
    @Nullable
    private ResourceLocation background;

    public ScrollableContainer(int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
    }

    @Override
    protected int contentHeight() {
        return contentHeight;
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
            child.setY((int) (this.defaultPositions.get(child) - this.scrollAmount()));
        }
    }


    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());

        if(this.background != null) guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.background, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getBottom(), this.getWidth(), this.getHeight());

        renderContent(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.disableScissor();
        guiGraphics.pose().popMatrix();


        renderScrollbar(guiGraphics);

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
    protected void renderScrollbar(GuiGraphics guiGraphics) {
        if (this.scrollbarVisible()) {
            int i = this.scrollBarX();
            int j = this.scrollerHeight();
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

    public ScrollableContainer setScrollerTexture(ResourceLocation sprite, @Nullable ResourceLocation background) {
        this.scrollerSprite = sprite;
        this.scrollerBackground = background;
        return this;
    }

    public ScrollableContainer setBackground(@Nullable ResourceLocation background) {
        this.background = background;
        return this;
    }

    public ScrollableContainer addChild(Screen parent, AbstractWidget child) {
        parent.addWidget(child);
        this.children.add(child);
        this.defaultPositions.put(child, child.getY());
        return this;
    }



    @FunctionalInterface
    public interface RenderInfo {

        void render(ScrollableContainer container,  GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

    }
}

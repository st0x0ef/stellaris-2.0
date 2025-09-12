package org.exodusstudio.stellaris.client.screens.components.containers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ScrollableContainer extends AbstractScrollArea {


    public HashMap<AbstractWidget, Integer> defaultPositions = new HashMap<>();
    public ArrayList<AbstractWidget> children = new ArrayList<>();
    public int contentHeight = 0;
    public RenderInfo onRender;
    public double scrollRate = 7;

    private ResourceLocation scrollerSprite = ResourceLocationUtils.id("icon/scroller");
    @Nullable
    private ResourceLocation scrollerBackground;

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
            child.setY((int) (this.defaultPositions.get(child) + this.scrollAmount()));
        }
    }


    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());

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

    public double getOffsetHeight() {
        return this.getY() - this.scrollAmount();
    }

    /** Builder */
    public ScrollableContainer setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
        return this;
    }

    public ScrollableContainer setRender(RenderInfo info) {
        this.onRender = info;
        return this;
    }

    public ScrollableContainer setScrollRate(double scrollRate) {
        this.scrollRate = scrollRate;
        return this;
    }

    public ScrollableContainer setTexture(ResourceLocation sprite, @Nullable ResourceLocation background) {
        this.scrollerSprite = sprite;
        this.scrollerBackground = background;
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

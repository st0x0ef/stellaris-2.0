package org.exodusstudio.stellaris.client.screens.components.containers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;

public class NewScrollableContainer extends AbstractScrollArea {


    public HashMap<AbstractWidget, Integer> defaultPositions = new HashMap<>();
    public ArrayList<AbstractWidget> children = new ArrayList<>();
    public int contentHeight = 0;



    public NewScrollableContainer(int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
    }

    @Override
    protected int contentHeight() {
        return contentHeight;
    }

    @Override
    protected double scrollRate() {
        return 7;
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

        //guiGraphics.pose().pushMatrix();
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());

        for(AbstractWidget widget : this.children) {
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }


        guiGraphics.disableScissor();
        //guiGraphics.pose().popMatrix();

        renderScrollbar(guiGraphics);

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public NewScrollableContainer setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
        return this;
    }


    public NewScrollableContainer addChild(Screen parent, AbstractWidget child) {
        parent.addRenderableWidget(child);
        this.children.add(child);
        this.defaultPositions.put(child, child.getY());
        return this;
    }


}

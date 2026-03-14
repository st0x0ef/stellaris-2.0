package org.exodusstudio.stellaris.client.screens.components.containers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class BasicContainer extends AbstractWidget {

    public ArrayList<AbstractWidget> children;

    public BasicContainer(int baseX, int baseY, int width, int height, AbstractWidget ...children) {
        super(baseX, baseY, width, height, Component.empty());
        this.children = new ArrayList<>(Arrays.asList(children));
        //this.children = addDefaultChildren(this.children);
    }

    public BasicContainer(int baseX, int baseY, int width, int height) {
        this(baseX, baseY, width, height, new AbstractWidget[0]);
    }

    /**
     * Override this method to add default children to the container.
     * This can be useful for making moveable window.
     *
     * @return The modified list of children with default widgets added.
     */
    public BasicContainer addDefaultChildren() {
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public BasicContainer setVisible(boolean visible) {
        this.visible = visible;
        for (AbstractWidget child : this.children) {
            child.visible = visible;
        }
        return this;
    }

    public BasicContainer addChild(Screen parent, AbstractWidget child) {
        parent.addRenderableWidget(child);
        return this.addChild(child);
    }

    public BasicContainer addChild(AbstractWidget child) {
        this.children.add(child);
        return this;
    }

    /**
     * This method should be called to update the position of all children.
     * It is used in mouseDragged and mouseScrolled methods to update the position of children.
     */
    abstract public void updateChildrenPosition();

}

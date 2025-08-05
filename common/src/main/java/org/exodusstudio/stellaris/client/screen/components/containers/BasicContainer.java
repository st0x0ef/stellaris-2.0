package org.exodusstudio.stellaris.client.screen.components.containers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class BasicContainer extends AbstractWidget{

    public ArrayList<AbstractWidget> children = new ArrayList<AbstractWidget>();

    @SafeVarargs
    public BasicContainer(int baseX, int baseY, int width, int height, AbstractWidget ...children) {
        this(baseX, baseY, width, height);
        this.children = new ArrayList<>(Arrays.asList(children));
    }

    public BasicContainer(int baseX, int baseY, int width, int height) {
        super(baseX, baseY, width, height, Component.empty());
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

    abstract public void updateChildrenPosition();

}

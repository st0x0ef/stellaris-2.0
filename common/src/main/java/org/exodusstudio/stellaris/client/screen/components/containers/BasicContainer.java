package org.exodusstudio.stellaris.client.screen.components.containers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class BasicContainer<C extends AbstractWidget> extends AbstractWidget{

    public ArrayList<C> children = new ArrayList<C>();

    @SafeVarargs
    public BasicContainer(int baseX, int baseY, int width, int height, C ...children) {
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

    public BasicContainer<C> addChild(Screen parent, C child) {
        parent.addRenderableWidget(child);
        return this.addChild(child);
    }

    public BasicContainer<C> addChild(C child) {
        this.children.add(child);
        return this;
    }

    abstract public void updateChildrenPosition();

}

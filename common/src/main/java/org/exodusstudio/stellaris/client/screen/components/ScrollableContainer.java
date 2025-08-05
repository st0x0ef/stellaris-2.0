package org.exodusstudio.stellaris.client.screen.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.Stellaris;

import java.util.ArrayList;
import java.util.Arrays;

public class ScrollableContainer<C extends AbstractWidget> extends AbstractWidget {

    private int scrollOffset = 0;


    public ArrayList<C> childrens = new ArrayList<C>();

    public ScrollableContainer(int baseX, int baseY, int width, int height, C ...childrens) {
        this(baseX, baseY, width, height);
        this.childrens = new ArrayList<>(Arrays.asList(childrens));
    }

    public ScrollableContainer(int baseX, int baseY, int width, int height) {
        super(baseX, baseY, width, height, Component.empty());
        this.scrollOffset = scrollOffset;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
//        this.scrollOffset += dragY;
//        updatePosition();
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        Stellaris.LOG.error("Mouse scrolled: " + childrens.size());
        this.scrollOffset += scrollY;
        updatePosition();
        return false;
    }


    public void updatePosition() {
        int y = this.scrollOffset;
        for (C child : this.childrens) {
            int childHeight = child.getHeight();
            child.setY(childHeight + y);
        }
    }

    public ScrollableContainer<C> addChild(Screen parent, C child) {
        parent.addRenderableWidget(child);
        return this.addChild(child);
    }

    public ScrollableContainer<C> addChild(C child) {
        this.childrens.add(child);
        return this;
    }


    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}

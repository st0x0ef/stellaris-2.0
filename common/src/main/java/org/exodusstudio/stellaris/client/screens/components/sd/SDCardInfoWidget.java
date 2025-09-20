package org.exodusstudio.stellaris.client.screens.components.sd;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.client.screens.components.ScrollableTextWidget;
import org.exodusstudio.stellaris.common.sdcard.SDCard;

import java.util.ArrayList;
import java.util.List;

public class SDCardInfoWidget extends AbstractContainerWidget {

    private final ArrayList<AbstractWidget> children = new ArrayList<>();

    private ScrollableTextWidget nameContainer;
    private ScrollableTextWidget descriptionContainer;

    private SDCard card;

    public SDCardInfoWidget(int x, int y, int width, int height, SDCard card) {
        super(x, y, width, height, Component.empty());

        this.card = card;
        setupTextWidgets(card);
    }

    public void setCard(SDCard card) {
        this.card = card;
        setupTextWidgets(card);

        if (card == null) {
            this.children.clear();
            return;
        }
        this.children.add(this.nameContainer);
        this.children.add(this.descriptionContainer);
    }

    public SDCard getCard() { return this.card; }

    public void setupTextWidgets(SDCard card) {
        if (card == null) return;
        this.nameContainer = new ScrollableTextWidget((this.getX() + this.getWidth()) / 2, this.getY(), (this.getX() + this.getWidth()) / 2, 50, card.getCardInfo().name());
        this.descriptionContainer = new ScrollableTextWidget(this.getX() + 5, this.getY() + 55, this.getWidth() - 10, 100, card.getCardInfo().description());
    }

    @Override
    protected int contentHeight() {
        return 50;
    }

    @Override
    protected double scrollRate() {
        return 10.0;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.children.forEach(w -> {
            if (isInBoundingBox(mouseX, mouseY, w)) {
                w.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        });

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.children.forEach(w -> w.render(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    private boolean isInBoundingBox(double mouseX, double mouseY, AbstractWidget w) {
        return mouseX >= w.getX() && mouseX <= w.getX() + w.getWidth() &&
                mouseY >= w.getY() && mouseY <= w.getY() + w.getHeight();
    }

}

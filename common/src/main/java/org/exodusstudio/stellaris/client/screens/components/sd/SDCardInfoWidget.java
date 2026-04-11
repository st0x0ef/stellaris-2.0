package org.exodusstudio.stellaris.client.screens.components.sd;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.client.screens.components.ScrollableTextWidget;
import org.exodusstudio.stellaris.common.data.SdCard;

import java.util.ArrayList;
import java.util.List;

public class SDCardInfoWidget extends AbstractContainerWidget {

    private final ArrayList<AbstractWidget> children = new ArrayList<>();

    private ScrollableTextWidget nameContainer;
    private ScrollableTextWidget descriptionContainer;

    private SdCard card;

    public SDCardInfoWidget(int x, int y, int width, int height, SdCard card) {
        super(x, y, width, height, Component.empty());

        this.card = card;
        setupTextWidgets(card);
    }

    public void setCard(SdCard card) {
        this.card = card;
        setupTextWidgets(card);

        if (card == null) {
            this.children.clear();
            return;
        }
        this.children.add(this.nameContainer);
        this.children.add(this.descriptionContainer);
    }

    public SdCard getCard() { return this.card; }

    public void setupTextWidgets(SdCard card) {
        if (card == null) return;
        this.nameContainer = new ScrollableTextWidget(this.getX(), this.getY(), this.getWidth(), 26, card.name());
        this.descriptionContainer = new ScrollableTextWidget(this.getX(), this.getY() + 28, this.getWidth(), 110, card.content());
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
    protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
        this.children.forEach(w -> w.render(ctx, mouseX, mouseY, partialTick));
        if (this.getCard() != null) ctx.fill(this.getX() + 4, this.getY() + 27, this.getX() + this.getWidth() + 4, this.getY() + 28, 0xFFFFFFFF);
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

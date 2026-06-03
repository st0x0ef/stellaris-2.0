package org.exodusstudio.stellaris.client.screens.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class ScrollableTextWidget extends AbstractWidget {

    private static final int LINE_PADDING = 2;
    private static final int PADDING = 5;

    private final List<FormattedCharSequence> text;
    private final int contentHeight;

    private double scrollAmount = 0;
    private int scrollRate = 7;

    public ScrollableTextWidget(int x, int y, int width, int height, String text) {
        this(x, y, width, height, Component.literal(text));
    }

    public ScrollableTextWidget(int x, int y, int width, int height, Component text) {
        super(x, y, width, height, Component.empty());
        this.text = Minecraft.getInstance().font.split(text, width - PADDING * 2);

        var numLines = this.text.size();
        this.contentHeight = numLines * Minecraft.getInstance().font.lineHeight + numLines * LINE_PADDING;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float partialTick) {
        ctx.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());

        double height = 5 - scrollAmount;
        for (FormattedCharSequence seq : this.text) {
            ctx.text(Minecraft.getInstance().font, seq, this.getX() + PADDING, (int) (this.getY() + height), 0xFFFFFFFF);
            height += Minecraft.getInstance().font.lineHeight + LINE_PADDING;
        }

        ctx.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.visible || contentHeight < this.getHeight()) {
            return false;
        } else {
            this.scrollAmount = Math.clamp(this.scrollAmount - scrollY * this.scrollRate, 0, Math.abs(this.contentHeight - this.getHeight() + PADDING));
            return true;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

}

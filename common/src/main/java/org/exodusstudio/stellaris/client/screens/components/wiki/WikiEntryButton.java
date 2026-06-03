package org.exodusstudio.stellaris.client.screens.components.wiki;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.exodusstudio.stellaris.common.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;

public class WikiEntryButton extends TexturedButton {

    public final WikiEntry entry;

    public WikiEntryButton(int x, int y, int widthIn, int heightIn, WikiEntry entry, OnPress onPressIn) {
        super(x, y, widthIn, heightIn, onPressIn);
        this.entry = entry;
        setTooltip(Tooltip.create(
                MutableComponent.create(entry.getTitle().getContents())
                        .append("\n")
                        .append(Component.literal(entry.description()).withStyle(ChatFormatting.GRAY))
        ));
    }


    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractContents(graphics, mouseX, mouseY, partialTicks);

        graphics.blit(RenderPipelines.GUI_TEXTURED, this.isHovered() ? entry.hoverIcon() : entry.icon(), this.getX(), this.getY() + 2, 0, 0,
                16, 16, 16, 16);

        Font font = Minecraft.getInstance().font;

        renderScrollingStringOverContents(graphics.textRendererForWidget(this,
                GuiGraphicsExtractor.HoveredTextEffects.NONE), entry.getTitle(), this.getX() + 20, (this.getY() + getHeight() / 2) - font.lineHeight / 2);
    }

    protected void renderScrollingStringOverContents(ActiveTextCollector activeTextCollector, Component text, int x, int y) {
        int endX = this.getX() + this.getWidth() - 5;
        int endY = y + Minecraft.getInstance().font.lineHeight;

        activeTextCollector.acceptScrollingWithDefaultCenter(text, x, endX, y, endY);
    }

}

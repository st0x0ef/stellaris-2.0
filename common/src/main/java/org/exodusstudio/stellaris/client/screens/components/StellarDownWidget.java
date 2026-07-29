package org.exodusstudio.stellaris.client.screens.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.client.utils.minedown.StellardownRenderer;

/**
 * A Widget that allow to render our markdown system.
 */
public class StellarDownWidget extends AbstractWidget {

    StellardownRenderer wikiEntryTextRenderer;

    public StellarDownWidget(int x, int y, int width, int height, String content) {
        super(x, y, width, height, Component.empty());
        this.wikiEntryTextRenderer = new StellardownRenderer(content, width, Minecraft.getInstance().font);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.wikiEntryTextRenderer.render(this.getX(), this.getY(), guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    /**
     * A builder class to create a StellarDownWidget. It provides a more convenient way to create a StellarDownWidget object by allowing us to add text, colored text, line breaks, and conditionally colored text in a more fluent way.
     */
    public static class Builder {
        private final StringBuilder textBuilder = new StringBuilder();

        public Builder addText(String text) {
            textBuilder.append(" ").append(text);
            return this;
        }

        public Builder addTranslatableText(String text) {
            textBuilder.append(" [tr]").append(text).append("[/tr]");
            return this;
        }
        public Builder addText(Object text) {
            textBuilder.append(" ").append(text);
            return this;
        }

        public Builder addColoredText(String text, String color) {
            textBuilder.append(" [color=").append(color).append("] ").append(text).append(" [color]");
            return this;
        }

        public Builder breakL() {
            textBuilder.append("[br]");
            return this;
        }

        public Builder conditionColorText(String text, String color, boolean condition) {
            if(condition) {
                addColoredText(text, color);
            } else {
                addText(text);
            }
            return this;
        }

        public Builder conditionColorText(String text, String color, String color2, boolean condition) {
            if(condition) {
                addColoredText(text, color);
            } else {
                addColoredText(text, color2);
            }
            return this;
        }

        public String toString() {
            return textBuilder.toString();
        }

        public StellarDownWidget build(int x, int y, int width, int height) {
            return new StellarDownWidget(x, y, width, height, textBuilder.toString());
        }
     }

}

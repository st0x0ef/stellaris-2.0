package org.exodusstudio.stellaris.client.screens.components.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;

public class WikiEntryButton extends TexturedButton {

    public final WikiEntry entry;

    public WikiEntryButton(int x, int y, int widthIn, int heightIn, WikiEntry entry, OnPress onPressIn) {
        super(x, y, widthIn, heightIn, onPressIn);
        this.entry = entry;
        setTooltip(Tooltip.create(
                MutableComponent.create(entry.getTitle().getContents())
                        .append("\n")
                        .append(entry.description())
        ));
    }


    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);

        graphics.blit(RenderPipelines.GUI_TEXTURED, this.isHovered() ? entry.hoverIcon() : entry.icon(), this.getX(), this.getY() + 2, 0, 0,
                16, 16, 16, 16);


        Button.renderScrollingString(graphics, Minecraft.getInstance().font, entry.getTitle(), this.getX() + 20, this.getY() + 2, this.getRight(), this.getBottom(), ARGB.white(1));
    }

}

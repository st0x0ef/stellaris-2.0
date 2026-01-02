package org.exodusstudio.stellaris.client.screens.components.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class StatWidget extends AbstractWidget {

    private static final Identifier BACKGROUND = IdentifierUtils.guiTexture("tablet/stats/stat_entry");

    private final Component statName;
    private final Component statValue;
    private final ScrollableContainer parent;

    public StatWidget(int x, int y, int width, int height, Component name, Component val, ScrollableContainer parent) {
        super(x, y, width, height, Component.literal("Stat Widget"));
        this.statName = name;
        this.statValue = val;
        this.parent = parent;
    }

    @Override
    protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
        ctx.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), 260, 20);
        var tx = Minecraft.getInstance().font;
        ctx.drawString(tx, statName, this.getX() + 3, this.getY() + this.getHeight() / 2 - tx.lineHeight / 2, 0xFFFFFFFF);
        ctx.drawString(tx, statValue, this.getX() + this.getWidth() - tx.width(statValue) - 8, this.getY() + this.getHeight() / 2 - tx.lineHeight / 2, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.parent.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

}

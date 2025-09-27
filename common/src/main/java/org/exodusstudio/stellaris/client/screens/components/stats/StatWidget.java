package org.exodusstudio.stellaris.client.screens.components.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class StatWidget extends AbstractWidget {

    private static final ResourceLocation BACKGROUND = ResourceLocationUtils.guiTexture("tablet/stats/stat_entry");

    private final Component statName;
    private final Component statValue;

    public StatWidget(int x, int y, int width, int height, Component name, Component val) {
        super(x, y, width, height, Component.literal("Stat Widget"));
        this.statName = name;
        this.statValue = val;
    }

    @Override
    protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
        ctx.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), 260, 20);
        var tx = Minecraft.getInstance().font;
        ctx.drawString(tx, statName, this.getX() + 3, this.getY() + this.getHeight() / 2 - tx.lineHeight / 2, 0xFFFFFFFF);
        ctx.drawString(tx, statValue, this.getX() + this.getWidth() - tx.width(statValue) - 8, this.getY() + this.getHeight() / 2 - tx.lineHeight / 2, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

}

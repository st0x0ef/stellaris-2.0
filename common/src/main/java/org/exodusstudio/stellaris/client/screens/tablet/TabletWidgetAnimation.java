package org.exodusstudio.stellaris.client.screens.tablet;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Util;
import org.joml.Matrix3x2fStack;

public class TabletWidgetAnimation {

    private static final long ENTER_DURATION_MS = 280L;
    private static final long PRESS_DURATION_MS = 220L;

    private final long createdAt = Util.getMillis();
    private float hoverAmount = 0.0F;
    private long lastFrameAt = Util.getMillis();
    private long pressedAt = -PRESS_DURATION_MS;

    public void press() {
        this.pressedAt = Util.getMillis();
    }

    public void push(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean hoveredOrFocused, boolean active) {
        long now = Util.getMillis();
        this.updateHover(now, hoveredOrFocused, active);

        float enter = this.enterAmount(now, x, y);
        float hover = TabletAnimation.easeOutCubic(this.hoverAmount);
        float pressProgress = Math.max(0.0F, Math.min(1.0F, (now - this.pressedAt) / (float) PRESS_DURATION_MS));
        float press = (float) Math.sin(Math.PI * pressProgress);

        float enterScale = TabletAnimation.lerp(0.82F, 1.0F, enter);
        float hoverScale = TabletAnimation.lerp(1.0F, 1.075F, hover);
        float xScale = enterScale * hoverScale * (1.0F + press * 0.018F);
        float yScale = enterScale * hoverScale * (1.0F - press * 0.045F);
        float yOffset = (1.0F - enter) * 5.0F - hover * 1.25F + press * 1.0F;

        float centerX = x + width / 2.0F;
        float centerY = y + height / 2.0F;

        Matrix3x2fStack matrixStack = guiGraphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(centerX, centerY + yOffset);
        matrixStack.scale(xScale, yScale);
        matrixStack.translate(-centerX, -centerY);
    }

    public void pop(GuiGraphics guiGraphics) {
        guiGraphics.pose().popMatrix();
    }

    public float glowAmount() {
        return TabletAnimation.easeOutCubic(this.hoverAmount);
    }

    private void updateHover(long now, boolean hoveredOrFocused, boolean active) {
        float frameSeconds = Math.min(0.05F, (now - this.lastFrameAt) / 1000.0F);
        this.lastFrameAt = now;

        float target = hoveredOrFocused && active ? 1.0F : 0.0F;
        float blend = 1.0F - (float) Math.pow(0.00008F, frameSeconds);
        this.hoverAmount += (target - this.hoverAmount) * blend;
    }

    private float enterAmount(long now, int x, int y) {
        int stagger = Math.floorMod(x * 13 + y * 7, 85);
        float progress = Math.max(0.0F, Math.min(1.0F, (now - this.createdAt - stagger) / (float) ENTER_DURATION_MS));
        return TabletAnimation.easeOutCubic(progress);
    }
}

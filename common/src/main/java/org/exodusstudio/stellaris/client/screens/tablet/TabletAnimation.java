package org.exodusstudio.stellaris.client.screens.tablet;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Util;
import org.joml.Matrix3x2fStack;

public class TabletAnimation {

    private static final long OPEN_DURATION_MS = 460L;
    private static final long CLOSE_DURATION_MS = 320L;

    private final long openedAt = Util.getMillis();
    private long closeStartedAt = -1L;
    private boolean closeActionRan = false;

    public boolean isClosing() {
        return this.closeStartedAt >= 0L;
    }

    public void startClosing() {
        if (!this.isClosing()) {
            this.closeStartedAt = Util.getMillis();
        }
    }

    public boolean shouldInterceptClose() {
        if (this.closeActionRan || this.isCloseFinished()) {
            return false;
        }

        this.startClosing();
        return true;
    }

    public boolean finishClosing(Runnable closeAction) {
        if (!this.isCloseFinished() || this.closeActionRan) {
            return false;
        }

        this.closeActionRan = true;
        closeAction.run();
        return true;
    }

    public void renderBackdrop(GuiGraphics guiGraphics, int width, int height, float partialTick) {
        int alpha = Math.round(150.0F * this.fadeAmount(partialTick));
        guiGraphics.fill(0, 0, width, height, color(alpha, 0x050711));
    }

    public void renderTabletShadow(GuiGraphics guiGraphics, int left, int top, int width, int height, float partialTick) {
        float alpha = this.fadeAmount(partialTick);
        float xScale = this.xScale(partialTick);
        float yScale = this.yScale(partialTick);
        float yOffset = this.yOffset(partialTick);
        float centerX = left + width / 2.0F;
        float centerY = top + height / 2.0F;

        int scaledLeft = Math.round(centerX - (width * xScale) / 2.0F);
        int scaledRight = Math.round(centerX + (width * xScale) / 2.0F);
        int scaledTop = Math.round(centerY + yOffset - (height * yScale) / 2.0F);
        int scaledBottom = Math.round(centerY + yOffset + (height * yScale) / 2.0F);

        int lift = Math.round(10.0F + (1.0F - alpha) * 20.0F);
        for (int i = 4; i >= 1; i--) {
            int spread = i * 4;
            int shadowAlpha = Math.round(alpha * (10 + i * 6));
            guiGraphics.fill(scaledLeft - spread, scaledTop + lift - spread / 2, scaledRight + spread, scaledBottom + lift + spread, color(shadowAlpha, 0x000000));
        }
    }

    public void pushScreen(GuiGraphics guiGraphics, float centerX, float centerY, float partialTick) {
        Matrix3x2fStack matrixStack = guiGraphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(centerX, centerY + this.yOffset(partialTick));
        matrixStack.scale(this.xScale(partialTick), this.yScale(partialTick));
        matrixStack.translate(-centerX, -centerY);
    }

    public void popScreen(GuiGraphics guiGraphics) {
        guiGraphics.pose().popMatrix();
    }

    public void renderGlassEffects(GuiGraphics guiGraphics, int left, int top, int width, int height, float partialTick) {
        float fade = this.fadeAmount(partialTick);
        if (fade <= 0.0F) {
            return;
        }

        int edgeAlpha = Math.round(32.0F * fade);
        guiGraphics.fill(left + 14, top + 9, left + width - 14, top + 10, color(edgeAlpha, 0xE9F7FF));
        guiGraphics.fill(left + 14, top + height - 11, left + width - 14, top + height - 10, color(Math.round(18.0F * fade), 0x64B5FF));
        guiGraphics.fill(left + 13, top + 10, left + 14, top + height - 10, color(Math.round(16.0F * fade), 0x8EE6FF));
        guiGraphics.fill(left + width - 14, top + 10, left + width - 13, top + height - 10, color(Math.round(16.0F * fade), 0x8EE6FF));

        if (!this.isClosing()) {
            float wakeProgress = clamp(this.progressSince(this.openedAt, OPEN_DURATION_MS) / 0.72F);
            if (wakeProgress < 1.0F) {
                float sweep = easeOutCubic(wakeProgress);
                int sweepY = Math.round(lerp(top + 18.0F, top + height - 28.0F, sweep));
                int sweepAlpha = Math.round(54.0F * (1.0F - wakeProgress));
                guiGraphics.fill(left + 22, sweepY, left + width - 22, sweepY + 2, color(sweepAlpha, 0xFFFFFF));
                guiGraphics.fill(left + 30, sweepY + 2, left + width - 30, sweepY + 5, color(sweepAlpha / 3, 0x9FE8FF));
            }
        }
    }

    public int transformMouseX(int mouseX, float centerX, float partialTick) {
        return Math.round(centerX + ((mouseX - centerX) / this.xScale(partialTick)));
    }

    public int transformMouseY(int mouseY, float centerY, float partialTick) {
        return Math.round(centerY + ((mouseY - centerY - this.yOffset(partialTick)) / this.yScale(partialTick)));
    }

    private boolean isCloseFinished() {
        return this.isClosing() && Util.getMillis() - this.closeStartedAt >= CLOSE_DURATION_MS;
    }

    private float xScale(float partialTick) {
        float amount = this.visibleAmount(partialTick);
        float settle = this.settleBounce(partialTick);
        return lerp(0.72F, 1.0F, amount) + settle * 0.018F;
    }

    private float yScale(float partialTick) {
        float amount = this.visibleAmount(partialTick);
        float settle = this.settleBounce(partialTick);
        return lerp(0.62F, 1.0F, amount) - settle * 0.012F;
    }

    private float yOffset(float partialTick) {
        if (this.isClosing()) {
            float closeProgress = this.progressSince(this.closeStartedAt, CLOSE_DURATION_MS);
            return lerp(0.0F, 74.0F, easeInQuint(closeProgress));
        }

        float progress = this.progressSince(this.openedAt, OPEN_DURATION_MS);
        float lift = easeOutQuint(progress);
        float settle = (float) Math.sin(progress * Math.PI) * (1.0F - progress);
        return lerp(76.0F, 0.0F, lift) - settle * 7.0F;
    }

    private float fadeAmount(float partialTick) {
        if (this.isClosing()) {
            return 1.0F - easeInCubic(this.progressSince(this.closeStartedAt, CLOSE_DURATION_MS));
        }

        return easeOutCubic(this.progressSince(this.openedAt, OPEN_DURATION_MS));
    }

    private float visibleAmount(float partialTick) {
        if (this.isClosing()) {
            float closeProgress = this.progressSince(this.closeStartedAt, CLOSE_DURATION_MS);
            return 1.0F - easeInQuart(closeProgress);
        }

        return easeOutQuint(this.progressSince(this.openedAt, OPEN_DURATION_MS));
    }

    private float settleBounce(float partialTick) {
        if (this.isClosing()) {
            return 0.0F;
        }

        float progress = this.progressSince(this.openedAt, OPEN_DURATION_MS);
        return (float) Math.sin(progress * Math.PI * 2.0F) * (1.0F - progress);
    }

    private float progressSince(long startTime, long duration) {
        return clamp((float) (Util.getMillis() - startTime) / (float) duration);
    }

    public static float lerp(float from, float to, float progress) {
        return from + (to - from) * progress;
    }

    public static float easeOutCubic(float progress) {
        float inverted = 1.0F - clamp(progress);
        return 1.0F - inverted * inverted * inverted;
    }

    private static float easeInCubic(float progress) {
        progress = clamp(progress);
        return progress * progress * progress;
    }

    private static float easeInQuart(float progress) {
        progress = clamp(progress);
        return progress * progress * progress * progress;
    }

    private static float easeInQuint(float progress) {
        progress = clamp(progress);
        return progress * progress * progress * progress * progress;
    }

    private static float easeOutQuint(float progress) {
        float inverted = 1.0F - clamp(progress);
        return 1.0F - inverted * inverted * inverted * inverted * inverted;
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static int color(int alpha, int rgb) {
        alpha = Math.max(0, Math.min(255, alpha));
        return (alpha << 24) | rgb;
    }
}

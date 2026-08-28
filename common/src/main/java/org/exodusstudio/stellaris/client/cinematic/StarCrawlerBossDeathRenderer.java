package org.exodusstudio.stellaris.client.cinematic;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;

public final class StarCrawlerBossDeathRenderer {
    public static String VICTORY_TITLE_TEXT = "STAR CRAWLER";
    public static String VICTORY_SUBTITLE_TEXT = "SLAIN";
    public static String SKIP_PROMPT_TEXT = "Press [SPACE] or [ESC] to skip";

    private static boolean initialized;

    private StarCrawlerBossDeathRenderer() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ClientGuiEvent.RENDER_HUD.register(StarCrawlerBossDeathRenderer::render);
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!StarCrawlerBossDeathController.isVisualActive()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        float elapsed = StarCrawlerBossDeathController.getDeathElapsedTicks(
                deltaTracker.getGameTimeDeltaPartialTick(true)
        );
        if (elapsed < 0.0F || elapsed >= StarCrawlerBossDeathController.RETURN_END_TICK) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        graphics.nextStratum();

        float barsIn = smootherStep(range(elapsed, 1.0F, 15.0F));
        float barsOut = 1.0F - smootherStep(range(
                elapsed,
                StarCrawlerBossDeathController.RELEASE_END_TICK,
                StarCrawlerBossDeathController.RETURN_END_TICK
        ));
        float bars = Math.min(barsIn, barsOut);
        int maximumBarHeight = Mth.clamp(Math.round(height * 0.082F), 13, 38);
        int barHeight = Math.round(maximumBarHeight * bars);

        float pressure = smootherStep(range(elapsed, 12.0F, 70.0F));
        float aftermath = 1.0F - smootherStep(range(elapsed, 112.0F, 140.0F));
        float darkness = (0.10F + pressure * 0.13F) * Math.min(barsIn, aftermath);
        graphics.fill(0, 0, width, height, argb(darkness, 0x03040A));
        drawVignette(graphics, width, height, darkness, elapsed);

        if (barHeight > 0) {
            graphics.fill(0, 0, width, barHeight, 0xFF000000);
            graphics.fill(0, height - barHeight, width, height, 0xFF000000);
        }

        drawBeatFlash(graphics, width, height, elapsed);
        drawReleasePressure(graphics, width, height, elapsed);
        drawVictory(graphics, minecraft.font, width, height, elapsed, maximumBarHeight);
        drawSkipPrompt(graphics, minecraft.font, width, height, elapsed, barHeight);
    }

    private static void drawBeatFlash(
            GuiGraphicsExtractor graphics,
            int width,
            int height,
            float elapsed
    ) {
        float lethal = 1.0F - smootherStep(range(elapsed, 0.0F, 6.0F));
        float collapse = pulse(elapsed, 82.0F, 6.0F);
        float release = pulse(elapsed, 110.0F, 5.5F);

        if (lethal > 0.001F) {
            graphics.fill(0, 0, width, height, argb(lethal * 0.17F, 0xDCE9F5));
        }
        if (collapse > 0.001F) {
            graphics.fill(0, 0, width, height, argb(collapse * 0.10F, 0xB7A6C8));
        }
        if (release > 0.001F) {
            graphics.fill(0, 0, width, height, argb(release * 0.24F, 0xD8E9FF));
            int edgeAlpha = Mth.clamp(Math.round(release * 75.0F), 0, 75);
            int edgeWidth = Math.max(1, Math.round(width * 0.012F * release));
            graphics.fill(0, 0, edgeWidth, height, edgeAlpha << 24 | 0x006C4F80);
            graphics.fill(width - edgeWidth, 0, width, height, edgeAlpha << 24 | 0x003C7590);
        }
    }

    private static void drawReleasePressure(
            GuiGraphicsExtractor graphics,
            int width,
            int height,
            float elapsed
    ) {
        float wave = range(elapsed, 107.0F, 120.0F);
        if (wave <= 0.0F || wave >= 1.0F) {
            return;
        }

        float eased = 1.0F - (1.0F - wave) * (1.0F - wave);
        float alpha = (1.0F - wave) * 0.16F;
        int halfWidth = Math.max(2, Math.round(width * 0.48F * eased));
        int halfHeight = Math.max(2, Math.round(height * 0.38F * eased));
        int centerX = width / 2;
        int centerY = Math.round(height * 0.48F);
        int color = argb(alpha, 0xD4E7FF);
        graphics.fill(centerX - halfWidth, centerY - halfHeight, centerX + halfWidth, centerY - halfHeight + 1, color);
        graphics.fill(centerX - halfWidth, centerY + halfHeight, centerX + halfWidth, centerY + halfHeight + 1, color);
        graphics.fill(centerX - halfWidth, centerY - halfHeight, centerX - halfWidth + 1, centerY + halfHeight, color);
        graphics.fill(centerX + halfWidth, centerY - halfHeight, centerX + halfWidth + 1, centerY + halfHeight, color);
    }

    private static void drawVictory(
            GuiGraphicsExtractor graphics,
            Font font,
            int width,
            int height,
            float elapsed,
            int maximumBarHeight
    ) {
        float titleIn = smootherStep(range(elapsed, 113.0F, 120.0F));
        float titleOut = 1.0F - smootherStep(range(elapsed, 130.0F, 139.0F));
        float alpha = Math.min(titleIn, titleOut) * 0.82F;
        if (alpha <= 0.001F) {
            return;
        }

        float settle = 1.0F + 0.18F * (1.0F - smootherStep(range(elapsed, 113.0F, 126.0F)));
        int titleWidth = Math.max(1, font.width(VICTORY_TITLE_TEXT));
        float scale = Math.min(1.62F * settle, Math.max(0.8F, (width - 40.0F) / titleWidth));
        float y = Math.max(maximumBarHeight + 22.0F, height * 0.41F);

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(width * 0.5F, y);
        pose.scale(scale, scale);
        graphics.centeredText(font, VICTORY_TITLE_TEXT, 0, 0, argb(alpha, 0xD9DFE8));
        pose.popMatrix();

        float subtitleAlpha = alpha * smootherStep(range(elapsed, 118.0F, 125.0F));
        if (subtitleAlpha > 0.001F) {
            graphics.centeredText(
                    font,
                    VICTORY_SUBTITLE_TEXT,
                    width / 2,
                    Math.round(y + font.lineHeight * scale + 7.0F),
                    argb(subtitleAlpha, 0xA998B7)
            );
        }
    }

    private static void drawSkipPrompt(
            GuiGraphicsExtractor graphics,
            Font font,
            int width,
            int height,
            float elapsed,
            int barHeight
    ) {
        float alpha = 0.68F * Math.min(
                smootherStep(range(elapsed, 12.0F, 25.0F)),
                1.0F - smootherStep(range(elapsed, 112.0F, 126.0F))
        );
        if (alpha <= 0.001F) {
            return;
        }
        int y = Math.min(height - barHeight - font.lineHeight - 6, height - 18);
        graphics.centeredText(font, SKIP_PROMPT_TEXT, width / 2, y, argb(alpha, 0xCED6E2));
    }

    private static void drawVignette(
            GuiGraphicsExtractor graphics,
            int width,
            int height,
            float darkness,
            float elapsed
    ) {
        float collapsePressure = pulse(elapsed, 82.0F, 14.0F) * 0.12F;
        float amount = darkness + collapsePressure;
        if (amount <= 0.001F) {
            return;
        }

        int horizontal = Math.max(9, width / 16);
        int vertical = Math.max(9, height / 13);
        int edge = argb(amount * 1.55F, 0x000000);
        int inner = argb(amount * 0.52F, 0x08040B);
        graphics.fill(0, 0, width, Math.max(1, vertical / 3), edge);
        graphics.fill(0, height - Math.max(1, vertical / 3), width, height, edge);
        graphics.fill(0, 0, Math.max(1, horizontal / 3), height, edge);
        graphics.fill(width - Math.max(1, horizontal / 3), 0, width, height, edge);
        graphics.fill(0, vertical / 3, width, vertical, inner);
        graphics.fill(0, height - vertical, width, height - vertical / 3, inner);
        graphics.fill(horizontal / 3, 0, horizontal, height, inner);
        graphics.fill(width - horizontal, 0, width - horizontal / 3, height, inner);
    }

    private static float pulse(float value, float center, float radius) {
        return 1.0F - smootherStep(Mth.clamp(Math.abs(value - center) / radius, 0.0F, 1.0F));
    }

    private static int argb(float alpha, int rgb) {
        return Mth.clamp(Math.round(alpha * 255.0F), 0, 255) << 24 | rgb & 0x00FFFFFF;
    }

    private static float range(float value, float start, float end) {
        return Mth.clamp((value - start) / (end - start), 0.0F, 1.0F);
    }

    private static float smootherStep(float value) {
        float t = Mth.clamp(value, 0.0F, 1.0F);
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }
}

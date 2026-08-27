package org.exodusstudio.stellaris.client.cinematic;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;

public final class StarCrawlerBossIntroRenderer {
    public static String TITLE_TEXT = "STAR CRAWLER";
    public static String SUBTITLE_TEXT = "THE LUNAR ABOMINATION";
    public static String SKIP_PROMPT_TEXT = "Press [SPACE] or [ESC] to skip";

    private static boolean initialized;

    private StarCrawlerBossIntroRenderer() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        ClientGuiEvent.RENDER_HUD.register(StarCrawlerBossIntroRenderer::render);
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!StarCrawlerBossIntroController.isVisualActive()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        float elapsed = StarCrawlerBossIntroController.getIntroElapsedTicks(partialTick);
        if (elapsed < 0.0F || elapsed >= StarCrawlerBossIntroController.RETURN_END_TICK) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        graphics.nextStratum();

        float barsIn = smootherStep(range(elapsed, 0.0F, 18.0F));
        float barsOut = 1.0F - smootherStep(range(
                elapsed,
                StarCrawlerBossIntroController.TITLE_END_TICK,
                StarCrawlerBossIntroController.RETURN_END_TICK
        ));
        float bars = Math.min(barsIn, barsOut);
        int maximumBarHeight = Mth.clamp(Math.round(height * 0.075F), 12, 34);
        int barHeight = Math.round(maximumBarHeight * bars);

        float titleFocus = Math.min(
                smootherStep(range(elapsed, 102.0F, 109.0F)),
                1.0F - smootherStep(range(elapsed, 130.0F, 138.0F))
        );
        float roarFlash = impactPulse(elapsed, 90.0F, 7.0F);

        float darkness = 0.18F * Math.min(
                smootherStep(range(elapsed, 0.0F, 12.0F)),
                barsOut
        ) + titleFocus * 0.09F + roarFlash * 0.055F;
        graphics.fill(0, 0, width, height, argb(darkness, 0x05070C));
        drawVignette(graphics, width, height, darkness);

        if (roarFlash > 0.001F) {
            graphics.fill(
                    0,
                    0,
                    width,
                    height,
                    argb(roarFlash * 0.075F, 0xB8D9FF)
            );
            drawRoarEdgeImpression(
                    graphics,
                    width,
                    height,
                    roarFlash
            );
        }

        if (barHeight > 0) {
            graphics.fill(0, 0, width, barHeight, 0xFF000000);
            graphics.fill(0, height - barHeight, width, height, 0xFF000000);
        }

        Font font = minecraft.font;
        drawTitle(graphics, font, width, height, elapsed, maximumBarHeight);
        drawSkipPrompt(graphics, font, width, height, elapsed, barHeight);
    }

    private static void drawTitle(
            GuiGraphicsExtractor graphics,
            Font font,
            int width,
            int height,
            float elapsed,
            int maximumBarHeight
    ) {
        float titleIn = smootherStep(range(elapsed, 106.0F, 114.0F));
        float titleOut = 1.0F - smootherStep(range(elapsed, 128.0F, 136.0F));
        float alpha = Math.min(titleIn, titleOut);
        if (alpha <= 0.001F) {
            return;
        }

        float settle = (float) (Math.exp(-range(elapsed, 106.0F, 126.0F) * 4.2F)
                * Math.cos(range(elapsed, 106.0F, 126.0F) * Math.PI * 3.0D));
        float desiredScale = 2.35F + 0.22F * settle;
        int titleWidth = Math.max(1, font.width(TITLE_TEXT));
        float fitScale = Math.max(0.75F, (width - 36.0F) / titleWidth);
        float scale = Math.min(desiredScale, fitScale);
        float titleY = Math.max(maximumBarHeight + 18.0F, height * 0.39F);

        float impactFade =
                1.0F - smootherStep(
                        range(elapsed, 106.0F, 110.0F)
                );

        float titleJitter =
                Mth.sin(elapsed * 8.4F)
                        * 1.35F
                        * impactFade;

        float sweep =
                smootherStep(
                        range(elapsed, 113.0F, 126.0F)
                );

        if (sweep > 0.0F
                && sweep < 1.0F) {
            int visualTitleWidth =
                    Math.max(
                            1,
                            Math.round(titleWidth * scale)
                    );

            int left =
                    width / 2
                            - visualTitleWidth / 2;

            int sweepX =
                    left - 8
                            + Math.round(
                            (visualTitleWidth + 16)
                                    * sweep
                    );

            graphics.fill(
                    sweepX - 5,
                    Math.round(titleY) - 4,
                    sweepX + 5,
                    Math.round(
                            titleY
                                    + font.lineHeight * scale
                                    + 3.0F
                    ),
                    argb(alpha * 0.09F, 0xBFE3FF)
            );
        }

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(
                width * 0.5F + titleJitter,
                titleY
        );
        pose.scale(scale, scale);

        float glow =
                alpha
                        * (0.34F + impactFade * 0.28F);

        graphics.centeredText(
                font,
                TITLE_TEXT,
                -1,
                0,
                argb(glow, 0x6FA6D8)
        );
        graphics.centeredText(
                font,
                TITLE_TEXT,
                1,
                0,
                argb(glow, 0x6FA6D8)
        );
        graphics.centeredText(font, TITLE_TEXT, 0, 0, argb(alpha, 0xF4F1E7));
        pose.popMatrix();

        float subtitleAlpha = alpha * smootherStep(range(elapsed, 111.0F, 119.0F));
        if (subtitleAlpha > 0.001F) {
            int subtitleY = Math.round(titleY + font.lineHeight * scale + 8.0F);
            graphics.centeredText(
                    font,
                    SUBTITLE_TEXT,
                    width / 2,
                    subtitleY,
                    argb(subtitleAlpha, 0xB9C8D9)
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
        float promptIn = smootherStep(range(elapsed, 14.0F, 28.0F));
        float promptOut = 1.0F - smootherStep(range(elapsed, 116.0F, 132.0F));
        float alpha = 0.72F * Math.min(promptIn, promptOut);
        if (alpha <= 0.001F) {
            return;
        }

        int y = Math.min(height - barHeight - font.lineHeight - 6, height - 18);
        graphics.centeredText(font, SKIP_PROMPT_TEXT, width / 2, y, argb(alpha, 0xD7DEE8));
    }

    private static void drawVignette(
            GuiGraphicsExtractor graphics,
            int width,
            int height,
            float darkness
    ) {
        if (darkness <= 0.001F) {
            return;
        }

        int horizontal = Math.max(8, width / 18);
        int vertical = Math.max(8, height / 14);
        int edge = argb(darkness * 1.65F, 0x000000);
        int inner = argb(darkness * 0.55F, 0x000000);

        graphics.fill(0, 0, width, Math.max(1, vertical / 3), edge);
        graphics.fill(0, height - Math.max(1, vertical / 3), width, height, edge);
        graphics.fill(0, 0, Math.max(1, horizontal / 3), height, edge);
        graphics.fill(width - Math.max(1, horizontal / 3), 0, width, height, edge);

        graphics.fill(0, vertical / 3, width, vertical, inner);
        graphics.fill(0, height - vertical, width, height - vertical / 3, inner);
        graphics.fill(horizontal / 3, 0, horizontal, height, inner);
        graphics.fill(width - horizontal, 0, width - horizontal / 3, height, inner);
    }

    private static void drawRoarEdgeImpression(
            GuiGraphicsExtractor graphics,
            int width,
            int height,
            float intensity
    ) {
        int edgeWidth =
                Math.max(2, width / 90);

        int verticalInset =
                Math.max(2, height / 18);

        graphics.fill(
                0,
                verticalInset,
                edgeWidth,
                height - verticalInset,
                argb(intensity * 0.13F, 0x6F8BD8)
        );
        graphics.fill(
                width - edgeWidth,
                verticalInset,
                width,
                height - verticalInset,
                argb(intensity * 0.11F, 0xB85A72)
        );
    }

    private static float impactPulse(
            float elapsed,
            float center,
            float radius
    ) {
        if (elapsed < center) {
            return smootherStep(
                    range(
                            elapsed,
                            center - 1.25F,
                            center
                    )
            );
        }

        return 1.0F - smootherStep(
                range(
                        elapsed,
                        center,
                        center + radius
                )
        );
    }

    private static int argb(float alpha, int rgb) {
        return Mth.clamp(Math.round(alpha * 255.0F), 0, 255) << 24 | rgb & 0x00FFFFFF;
    }

    private static float range(float value, float start, float end) {
        return Mth.clamp((value - start) / (end - start), 0.0F, 1.0F);
    }

    private static float smootherStep(float value) {
        float clamped = Mth.clamp(value, 0.0F, 1.0F);
        return clamped * clamped * clamped
                * (clamped * (clamped * 6.0F - 15.0F) + 10.0F);
    }
}

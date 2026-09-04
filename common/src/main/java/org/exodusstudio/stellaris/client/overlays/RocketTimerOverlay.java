package org.exodusstudio.stellaris.client.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;


public class RocketTimerOverlay {

    public static final Identifier TIMER_1 = IdentifierUtils.texture("overlay/timer/timer_1");
    public static final Identifier TIMER_2 = IdentifierUtils.texture("overlay/timer/timer_2");
    public static final Identifier TIMER_3 = IdentifierUtils.texture("overlay/timer/timer_3");
    public static final Identifier TIMER_4 = IdentifierUtils.texture("overlay/timer/timer_4");
    public static final Identifier TIMER_5 = IdentifierUtils.texture("overlay/timer/timer_5");
    public static final Identifier TIMER_6 = IdentifierUtils.texture("overlay/timer/timer_6");
    public static final Identifier TIMER_7 = IdentifierUtils.texture("overlay/timer/timer_7");
    public static final Identifier TIMER_8 = IdentifierUtils.texture("overlay/timer/timer_8");
    public static final Identifier TIMER_9 = IdentifierUtils.texture("overlay/timer/timer_9");
    public static final Identifier TIMER_10 = IdentifierUtils.texture("overlay/timer/timer_10");


    private static final Identifier[] TIMER_TEXTURES = {
            TIMER_1, TIMER_2, TIMER_3, TIMER_4, TIMER_5,
            TIMER_6, TIMER_7, TIMER_8, TIMER_9, TIMER_10
    };

    /** Number shown by /stellaris countdown, 0 when the overlay is off. */
    private static int countdownNumber = 0;

    /**
     * Shows the countdown overlay without a rocket, until it is hidden again.
     *
     * @param number the number to display, between 1 and 10, or 0 to hide the overlay
     */
    public static void showCountdown(int number) {
        countdownNumber = (number >= 1 && number <= TIMER_TEXTURES.length) ? number : 0;
    }

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) {
            return;
        }

        if (countdownNumber > 0) {
            blitTimer(graphics, TIMER_TEXTURES[countdownNumber - 1]);
            return;
        }

        if (player.getVehicle() instanceof RocketEntity) {
            Entity vehicle = Minecraft.getInstance().player.getVehicle();
            int timer = 0;

            /** GET TIMER */
            if (vehicle instanceof RocketEntity rocket) {
                timer = rocket.getTimer();

                /** CHECK IF ROCKET IS STARTED */
                if (!rocket.getEntityData().get(RocketEntity.ROCKET_START)) {
                    return;
                }

                /** TIMER */
                if (timer > -1 && timer < 20) {
                    blitTimer(graphics, TIMER_10);
                }
                else if (timer > 20 && timer < 40) {
                    blitTimer(graphics, TIMER_9);
                }
                else if (timer > 40 && timer < 60) {
                    blitTimer(graphics, TIMER_8);
                }
                else if (timer > 60 && timer < 80) {
                    blitTimer(graphics, TIMER_7);
                }
                else if (timer > 80 && timer < 100) {
                    blitTimer(graphics, TIMER_6);
                }
                else if (timer > 100 && timer < 120) {
                    blitTimer(graphics, TIMER_5);
                }
                else if (timer > 120 && timer < 140) {
                    blitTimer(graphics, TIMER_4);
                }
                else if (timer > 140 && timer < 160) {
                    blitTimer(graphics, TIMER_3);
                }
                else if (timer > 160 && timer < 180) {
                    blitTimer(graphics, TIMER_2);
                }
                else if (timer > 180 && timer < 200) {
                    blitTimer(graphics, TIMER_1);
                }
            }

        }
    }

    private static void blitTimer(GuiGraphicsExtractor graphics, Identifier texture) {
        int timerWidth = graphics.guiWidth() / 2 - 31;
        int timerHeight = graphics.guiHeight() / 2 / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
    }
}

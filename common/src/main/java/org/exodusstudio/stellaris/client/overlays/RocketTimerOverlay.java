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

    private static final int TICKS_PER_NUMBER = 20;

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

        if (!(player.getVehicle() instanceof RocketEntity rocket)) {
            return;
        }

        if (!rocket.getEntityData().get(RocketEntity.ROCKET_START)) {
            return;
        }

        int timer = rocket.getTimer();
        if (timer >= 0 && timer < TIMER_TEXTURES.length * TICKS_PER_NUMBER) {
            blitTimer(graphics, TIMER_TEXTURES[TIMER_TEXTURES.length - 1 - (timer / TICKS_PER_NUMBER)]);
        }
    }

    private static void blitTimer(GuiGraphicsExtractor graphics, Identifier texture) {
        int timerWidth = graphics.guiWidth() / 2 - 31;
        int timerHeight = graphics.guiHeight() / 2 / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
    }
}

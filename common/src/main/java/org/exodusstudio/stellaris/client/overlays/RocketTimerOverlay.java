package org.exodusstudio.stellaris.client.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
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


    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

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

                int timerWidth = graphics.guiWidth() / 2 - 31;
                int timerHeight = graphics.guiHeight() / 2 / 2;


                /** TIMER */
                if (timer > -1 && timer < 20) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TIMER_10, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
                else if (timer > 20 && timer < 40) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TIMER_9, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
                else if (timer > 40 && timer < 60) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TIMER_8, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
                else if (timer > 60 && timer < 80) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TIMER_7, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
                else if (timer > 80 && timer < 100) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TIMER_6, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
                else if (timer > 100 && timer < 120) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TIMER_5, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
                else if (timer > 120 && timer < 140) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TIMER_4, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
                else if (timer > 140 && timer < 160) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TIMER_3, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
                else if (timer > 160 && timer < 180) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TIMER_2, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
                else if (timer > 180 && timer < 200) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED,  TIMER_1, timerWidth, timerHeight, 0, 0, 60, 38, 60, 38);
                }
            }

        }
    }
}
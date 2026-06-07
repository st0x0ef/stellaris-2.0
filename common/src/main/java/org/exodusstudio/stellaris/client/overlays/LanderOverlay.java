package org.exodusstudio.stellaris.client.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.vehicles.LanderEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public class LanderOverlay {

    public static final Identifier WARNING = IdentifierUtils.texture("overlay/warning");

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Entity vehicle = player.getVehicle();
        Level level = mc.level;

        if (level != null && vehicle instanceof LanderEntity landerEntity && !vehicle.isInWall() && !player.isInWater() && !landerEntity.getEntityData().get(LanderEntity.LANDED)) {
            /** FLASHING */
            float sin = (float) Math.sin((level.getOverworldClockTime() + deltaTracker.getGameTimeDeltaPartialTick(true)) / 6.0f);
            float flash = Mth.clamp(sin, 0.0f, 4.0f);

            int rgba = ARGB.colorFromFloat(flash, flash, flash, flash);

            /** WARNING IMAGE */
            graphics.blit(RenderPipelines.GUI_TEXTURED, WARNING, graphics.guiWidth() / 2 - 58, 50, 0, 0, 116, 21, 116, 21, rgba);

            /** SPEED TEXT */
            double speed = Math.round(100.0 * vehicle.getDeltaMovement().y()) / 100.0;

            Font font = Minecraft.getInstance().font;

            Component speedMessage = Component.translatable("text." + Stellaris.MOD_ID + ".speed", speed);
            graphics.centeredText(font, speedMessage, graphics.guiWidth() / 2 , 80, Utils.getMinecraftColor("red"));

            Component message = Component.translatable("text." + Stellaris.MOD_ID + ".hold_space");
            ;
            graphics.centeredText(font, message, graphics.guiWidth() / 2 , 80 + font.lineHeight * 2, Utils.getMinecraftColor("red"));
        }
    }
}

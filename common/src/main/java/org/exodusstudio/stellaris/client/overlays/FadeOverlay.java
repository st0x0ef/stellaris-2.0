package org.exodusstudio.stellaris.client.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class FadeOverlay {

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) {
            return;
        }


        FadingHolder playerFade = player.stellaris$getDataAttachments(IdentifierUtils.id("player_fade"), FadingHolder.class);
        if (playerFade != null) {
            int alpha = (int) (playerFade.fadeAmount() * 255);
            graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), (alpha << 24) );

            if(playerFade.fading() && playerFade.fadeAmount() < 1.0f) {
                player.stellaris$saveDataAttachments(IdentifierUtils.id("player_fade"), new FadingHolder(playerFade.fading(),  Mth.clamp(playerFade.fadeAmount() + 0.01f, 0, 1F)) );
            } else if(!playerFade.fading() && playerFade.fadeAmount() > 0f) {
                player.stellaris$saveDataAttachments(IdentifierUtils.id("player_fade"), new FadingHolder(playerFade.fading(), playerFade.fadeAmount() - 0.01f) );
                if(playerFade.fadeAmount() - 0.01f < 0f) {
                    player.stellaris$saveDataAttachments(IdentifierUtils.id("player_fade"), new FadingHolder(false, 0f));
                }
            }
        }
    }
}

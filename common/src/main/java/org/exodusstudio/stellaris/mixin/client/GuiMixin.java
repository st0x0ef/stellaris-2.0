package org.exodusstudio.stellaris.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;
import org.exodusstudio.stellaris.common.utils.HeartUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    // Render custom heart for infected effect, cancels original method if infected
    // Note to other modders: if you want to add compatibility with your own custom hearts, please do so before this mixin (higher priority)
    @Inject(method = "renderHeart", at = @At("HEAD"), cancellable = true)
    private static void renderHeart(GuiGraphics guiGraphics, Gui.HeartType heartType, int x, int y, boolean hardcore, boolean halfHeart, boolean blinking, CallbackInfo ci) {
        if (heartType.equals(Gui.HeartType.CONTAINER)) return;
        Player player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(EffectsRegistry.getHolder(EffectsRegistry.INFECTED))) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, HeartUtils.getInfectedSprite(hardcore, blinking, halfHeart), x, y, 9, 9);
            ci.cancel();
        }
    }
}

package org.exodusstudio.stellaris.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;
import org.exodusstudio.stellaris.common.utils.HeartUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public class GuiMixin {

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
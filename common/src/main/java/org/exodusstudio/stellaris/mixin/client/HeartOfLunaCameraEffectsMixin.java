package org.exodusstudio.stellaris.mixin.client;

import net.minecraft.client.renderer.GameRenderer;
import org.exodusstudio.stellaris.client.cinematic.HeartOfLunaCinematic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class HeartOfLunaCameraEffectsMixin {
    @Inject(method = {"bobHurt", "bobView"}, at = @At("HEAD"), cancellable = true)
    private void stellaris$steadyLunarCamera(CallbackInfo ci) {
        if (HeartOfLunaCinematic.isVisualActive()) ci.cancel();
    }
}

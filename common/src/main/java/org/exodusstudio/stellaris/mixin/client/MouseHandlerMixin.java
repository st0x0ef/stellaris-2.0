package org.exodusstudio.stellaris.mixin.client;

import net.minecraft.client.MouseHandler;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossIntroController;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossDeathController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void stellaris$lockLookDuringBossIntro(double elapsedTime, CallbackInfo ci) {
        if (StarCrawlerBossIntroController.isVisualActive()
                || StarCrawlerBossDeathController.isVisualActive()) {
            ci.cancel();
        }
    }
}

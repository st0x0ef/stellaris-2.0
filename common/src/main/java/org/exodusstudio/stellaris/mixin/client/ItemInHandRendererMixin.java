package org.exodusstudio.stellaris.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossIntroController;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossDeathController;
import org.exodusstudio.stellaris.common.components.TimerComponent;
import org.exodusstudio.stellaris.common.items.infection.ParasiteItem;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(method = "renderHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void stellaris$hideHandsDuringBossIntro(
            float partialTick,
            PoseStack poseStack,
            SubmitNodeCollector nodeCollector,
            LocalPlayer player,
            int packedLight,
            CallbackInfo ci
    ) {
        if (StarCrawlerBossIntroController.isVisualActive()
                || StarCrawlerBossDeathController.isVisualActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "shouldInstantlyReplaceVisibleItem", at = @At("HEAD"), cancellable = true)
    private void stellaris$preventParasiteAnimation(ItemStack oldItem, ItemStack newItem, CallbackInfoReturnable<Boolean> cir) {
        if (oldItem.getItem() instanceof ParasiteItem && newItem.getItem() instanceof ParasiteItem) {
            TimerComponent oldTimer = oldItem.get(DataComponentsRegistry.TIMER.get());
            TimerComponent newTimer = newItem.get(DataComponentsRegistry.TIMER.get());
            if (oldTimer != null && newTimer != null && oldTimer.tick().timeLeft() == newTimer.timeLeft()) {
                cir.setReturnValue(true); // We cancel the animation when the parasite is just ticking down
            }
        }
    }
}

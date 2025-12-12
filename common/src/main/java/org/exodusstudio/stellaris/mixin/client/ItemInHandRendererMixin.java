package org.exodusstudio.stellaris.mixin.client;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.items.ParasiteItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(method = "shouldInstantlyReplaceVisibleItem", at = @At("HEAD"), cancellable = true)
    private void stellaris$preventParasiteAnimation(ItemStack oldItem, ItemStack newItem, CallbackInfoReturnable<Boolean> cir) {
        if (oldItem.getItem() instanceof ParasiteItem && newItem.getItem() instanceof ParasiteItem) {
            cir.setReturnValue(true);
        }
    }
}
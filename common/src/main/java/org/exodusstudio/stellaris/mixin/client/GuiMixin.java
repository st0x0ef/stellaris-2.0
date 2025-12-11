package org.exodusstudio.stellaris.mixin.client;

import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.Gui$HeartType")
public class GuiMixin {

    @Inject(method = "forPlayer", at = @At("HEAD"), cancellable = true)
    private static <T extends Enum<T>> void stellaris$onForPlayer(Player player, CallbackInfoReturnable<Enum<T>> cir) {
        if (player.hasEffect(EffectsRegistry.getHolder(EffectsRegistry.INFECTED))) {
            try {
                Class<?> heartTypeClass = Class.forName("net.minecraft.client.gui.Gui$HeartType");
                Enum<T> poisoned = Enum.valueOf((Class<T>) heartTypeClass, "POISIONED");
                cir.setReturnValue(poisoned);
            } catch (ClassNotFoundException ignored) {}
        }
    }
}
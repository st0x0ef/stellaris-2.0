package org.exodusstudio.stellaris.mixin.gravity_modifier;

import net.minecraft.world.entity.item.FallingBlockEntity;
import org.exodusstudio.stellaris.common.utils.GravityUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Unique
    private FallingBlockEntity stellaris$entity = (FallingBlockEntity)(Object)this;

    @Inject(method = "getDefaultGravity", at = @At("RETURN"), cancellable = true)
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(GravityUtils.getEntityGravity(GravityUtils.GRAVITY_FALLING_CONVERSION_RATE, stellaris$entity));
    }
}

package org.exodusstudio.stellaris.mixin.gravity_modifier;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.exodusstudio.stellaris.common.utils.GravityUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartEntityMixin {
    @Unique
    private AbstractMinecart stellaris$entity = (AbstractMinecart)(Object)this;

    @Inject(method = "getDefaultGravity", at = @At("RETURN"), cancellable = true)
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        if (stellaris$entity.isInWater()) {
            cir.setReturnValue(GravityUtils.getEntityGravity(GravityUtils.GRAVITY_WATER_MINECART_CONVERSION_RATE, stellaris$entity));
        } else {
            cir.setReturnValue(GravityUtils.getEntityGravity(GravityUtils.GRAVITY_FALLING_CONVERSION_RATE, stellaris$entity));
        }
    }
}

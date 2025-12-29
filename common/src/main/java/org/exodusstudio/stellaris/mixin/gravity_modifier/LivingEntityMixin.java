package org.exodusstudio.stellaris.mixin.gravity_modifier;

import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.GravityUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private LivingEntity stellaris$entity = (LivingEntity)(Object)this;

    @Unique
    private int stellaris$counter = 0;

    @Inject(method = "getDefaultGravity", at = @At("RETURN"))
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        if (stellaris$counter >= Stellaris.CONFIG.gravityConfig.gravityUpdateInterval) {
            stellaris$counter = 0;
            GravityUtils.setLivingEntityGravity(stellaris$entity);
        }
        stellaris$counter++;
    }
}

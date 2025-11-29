package org.exodusstudio.stellaris.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.exodusstudio.stellaris.common.utils.GravityUtils.EARTH_GRAVITY;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    private Entity stellaris$entity = (Entity)(Object)this;

    @Inject(method = "getDefaultGravity", at = @At("RETURN"), cancellable = true)
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        if (stellaris$entity instanceof LivingEntity)
            cir.cancel();

        double original = cir.getReturnValue();
        if (original != 0.0) {
            Planet planet = PlanetsData.getPlanet(stellaris$entity.level().dimension());

            if (planet != null)
                cir.setReturnValue(
                        new BigDecimal(original).divide(EARTH_GRAVITY, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(planet.gravity())).doubleValue());

        }
    }

}

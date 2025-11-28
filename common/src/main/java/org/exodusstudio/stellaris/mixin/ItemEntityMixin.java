package org.exodusstudio.stellaris.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.utils.GravityUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Unique
    private ItemEntity entity = (ItemEntity)(Object)this;

    @Inject(method = "getDefaultGravity", at = @At("HEAD"), cancellable = true)
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        Planet planet = PlanetsData.getPlanet(entity.level().dimension());
        if (planet != null) {
            cir.setReturnValue(GravityUtils.MPS2ToMCG(planet.gravity()) / 2);
        }
    }
}

package org.exodusstudio.stellaris.mixin.gravity_modifier;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.exodusstudio.stellaris.common.utils.GravityUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(AbstractBoat.class)
public class AbstractBoatEntityMixin {
    @Unique
    private AbstractBoat stellaris$entity = (AbstractBoat)(Object)this;

    @Inject(method = "getDefaultGravity", at = @At("RETURN"), cancellable = true)
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(GravityUtils.getEntityGravity(GravityUtils.GRAVITY_FALLING_CONVERSION_RATE, stellaris$entity));
    }

    @Redirect(
            method = { "getStatus", "getWaterLevelAbove", "checkFallDamage", "isUnderwater", "checkInWater", "getGroundFriction" },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"),
            require = 0
    )
    private boolean stellaris$replaceWaterFlotation(FluidState instance, TagKey<Fluid> tag) {
        if (stellaris$entity.is(TagsRegistry.EntityTags.LUNAR_BOATS)) {
            if (tag == FluidTags.WATER) {
                return instance.is(tag) ||
                       instance.is(FluidsRegistry.BLUE_LIQUID_STILL.get()) ||
                       instance.is(FluidsRegistry.BLUE_LIQUID_FLOWING.get());
            }
        }

        return instance.is(tag);
    }
}

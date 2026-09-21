package org.exodusstudio.stellaris.neoforge.mixin;

import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidType.class)
public class FluidTypeMixin {

    @Inject(
            method = "supportsBoating(Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 1
    )
    private void stellaris$boatsOnlyFloatOnVanillaWater(FluidState state, AbstractBoat boat, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(TagsRegistry.FluidTags.STELLARIS_FLUIDS)) {
            cir.setReturnValue(state.is(TagsRegistry.FluidTags.BLUE_LIQUID)
                    && boat.is(TagsRegistry.EntityTags.LUNAR_BOATS));
        }
    }
}

package org.exodusstudio.stellaris.fabric.mixin;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractBoat.class)
public class AbstractBoatMixin {

    @Redirect(
            method = { "getWaterLevelAbove", "checkFallDamage", "isUnderwater", "checkInWater" },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"),
            require = 1
    )
    private boolean stellaris$onlyLunarBoatsFloatOnBlueLiquid(FluidState instance, TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER && instance.is(TagsRegistry.FluidTags.BLUE_LIQUID)) {
            return ((AbstractBoat) (Object) this).is(TagsRegistry.EntityTags.LUNAR_BOATS);
        }

        return instance.is(tag);
    }
}

package org.exodusstudio.stellaris.neoforge.mixin;

import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.ArchitecturyFluidAttributesForge;
import net.neoforged.neoforge.fluids.FluidType;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArchitecturyFluidAttributesForge.class)
public class ArchitecturyFluidAttributesForgeMixin {

    @Inject(method = "addArchIntoBuilder", at = @At("RETURN"))
    private static void stellaris$markWaterLike(FluidType.Properties properties, ArchitecturyFluidAttributes attributes,
                                                CallbackInfoReturnable<FluidType.Properties> cir) {
        if (FluidsRegistry.WATER_LIKE_ATTRIBUTES.contains(attributes)) {
            cir.getReturnValue().isWaterLike(true);
        }
    }
}

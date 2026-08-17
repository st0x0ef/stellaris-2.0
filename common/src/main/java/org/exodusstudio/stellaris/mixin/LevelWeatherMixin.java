package org.exodusstudio.stellaris.mixin;

import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelWeatherMixin {
    @Inject(method = "canHaveWeather", at = @At("HEAD"), cancellable = true)
    private void cancelWeatherWithoutAtmosphere(CallbackInfoReturnable<Boolean> cir) {
        Planet planet = PlanetsData.getPlanet(((Level) (Object) this).dimension());

        if (planet != null && !planet.hasOxygen()) {
            cir.setReturnValue(false);
        }
    }
}

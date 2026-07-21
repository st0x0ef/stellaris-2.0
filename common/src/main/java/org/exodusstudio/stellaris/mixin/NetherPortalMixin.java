package org.exodusstudio.stellaris.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetherPortalBlock.class)
public class NetherPortalMixin {

    @Inject(method = "getPortalDestination", at = @At("HEAD"), cancellable = true)
    private void stellaris$blockNetherFromPlanets(ServerLevel level, Entity entity, BlockPos pos, CallbackInfoReturnable<TeleportTransition> cir) {
        if (level.dimension().equals(Level.OVERWORLD)) {
            return;
        }

        if (PlanetsData.getPlanet(level.dimension()) != null) {
            cir.setReturnValue(null);
        }
    }
}

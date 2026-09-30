package org.exodusstudio.stellaris.mixin;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void stellaris$extinguishInVacuum(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(OxygenUtils.getPlacementStateWithoutOxygen(context.getLevel(), context.getClickedPos(), cir.getReturnValue()));
    }
}

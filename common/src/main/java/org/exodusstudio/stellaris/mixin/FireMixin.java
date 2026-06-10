package org.exodusstudio.stellaris.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    protected void removeIfNoOxygen(BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random, CallbackInfo info) {
        if (!OxygenUtils.isOxygenated(level, pos)) {
            level.removeBlock(pos, false);
            info.cancel();
        }
    }

}

package org.exodusstudio.stellaris.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChangeOverTimeBlock.class)
public interface WeatheringCopperMixin {

    @Inject(method = "changeOverTime", at = @At("HEAD"), cancellable = true)
    private void oxidizeCopper(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        ChangeOverTimeBlock<WeatheringCopper.WeatherState> changeOverTimeBlock = (ChangeOverTimeBlock<WeatheringCopper.WeatherState>) (Object) this;
        if (changeOverTimeBlock instanceof WeatheringCopper weatheringCopper) {
           if(!OxygenUtils.isOxygenated(level, pos)) {
               ci.cancel();
           }
        }

    }

}

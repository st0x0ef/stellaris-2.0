package org.exodusstudio.stellaris.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CropBlock.class)
public class CropGrowsMixin {

    /**
     * Cancels crop growth if the planet does not have oxygen and the crop is not tagged as alien crops.
     * This is to prevent crops from growing in environments without oxygen.
     */
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void cancelCropGrowth(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {

        boolean hasOxygen = OxygenUtils.isOxygenated(level, pos);

        if(!state.is(TagsRegistry.BlockTags.ALIEN_CROPS) && !hasOxygen) {
            ci.cancel();
        }
    }

    /**
     * Cancels crop placement if the planet does not have oxygen and the crop is not tagged as alien crops.
     * This is to prevent crops from being placed in environments without oxygen.
     */
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    private void cancelCropPlacement(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {

        if(level instanceof ServerLevel serverLevel) {
            boolean hasOxygen = OxygenUtils.isOxygenated(serverLevel, pos);

            if((!state.is(TagsRegistry.BlockTags.ALIEN_CROPS) && !hasOxygen) || !state.is(TagsRegistry.BlockTags.NO_OXYGEN_CROP_BASE)) {
                cir.setReturnValue(false);
            }
        }
    }
}
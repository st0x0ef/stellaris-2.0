package org.exodusstudio.stellaris.mixin;

import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.exodusstudio.stellaris.common.world.attribute.EnvironmentAttributesRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @Shadow
    @Final
    private Fluid content;

    @ModifyVariable(method = "emptyContents", at = @At(value = "STORE"), name = "canPlaceFluidInsideBlock")
    public boolean FlashFreezeFunctionality(boolean canPlaceFluidInsideBlock, LivingEntity user, Level level, BlockPos pos, BlockHitResult hitResult, @Cancellable CallbackInfoReturnable<Boolean> cir)
    {
        if (canPlaceFluidInsideBlock && level.environmentAttributes().getValue(EnvironmentAttributesRegistry.FLASH_FREEZE.get(), pos)) {

            if (this.content.is(FluidTags.WATER)) {
                this.stellaris$PlaySoundAndSpawnParticles(user, level, pos);
                level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                cir.setReturnValue(true);
            } else if (this.content.is(FluidTags.LAVA)) {
                this.stellaris$PlaySoundAndSpawnParticles(user, level, pos);
                level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
                cir.setReturnValue(true);
            }
        }
        return canPlaceFluidInsideBlock;
    }

    @Unique
    private void stellaris$PlaySoundAndSpawnParticles(LivingEntity user, Level level, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        RandomSource random = level.getRandom();
        level.playSound(user, pos, SoundEvents.PLAYER_HURT_FREEZE, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
        for(int i = 0; i < 8; ++i) {
            level.addParticle(ParticleTypes.SNOWFLAKE, (float)x + random.nextFloat(), (float)y + random.nextFloat(), (float)z + random.nextFloat(), 0.0F, 0.0F, 0.0F);
        }
    }
}

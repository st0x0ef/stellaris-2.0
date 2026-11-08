package org.exodusstudio.stellaris.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.RadiationUtils;
import org.exodusstudio.stellaris.platform.DataAttachmentsPlatform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void stellaris$popRadioactiveDrops(Level level, BlockPos pos, ItemStack itemStack, CallbackInfo ci) {
        List<Pair<BlockPos, Float>> radioactiveBlocks = RadiationUtils.getRadioactiveBlocks(level.getChunk(pos));
        Map<BlockPos, Float> radioactiveBlocksMap = RadiationUtils.pairListToMap(radioactiveBlocks);

        if(radioactiveBlocksMap.containsKey(pos)) {
            float radiationLevel = radioactiveBlocksMap.get(pos);
            itemStack.set(DataComponentsRegistry.RADIATION.get(), radiationLevel);
        }
    }

    @Inject(method = "playerDestroy", at = @At("TAIL"))
    public void stellaris$radioactiveBlockDestroyed(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack destroyedWith, CallbackInfo ci) {

        //When a radioactive block is broken, remove it from the chunk data
        //We dont use the event because we need this to happen after the drops
        RadiationUtils.modifyBlockMap(level.getChunk(pos), pos, 0f, true);
    }



}

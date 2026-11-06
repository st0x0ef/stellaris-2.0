package org.exodusstudio.stellaris.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.exodusstudio.stellaris.common.utils.RadiationUtils;
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

    @Inject(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void placeRadioactiveBlock(BlockPlaceContext placeContext, CallbackInfoReturnable<InteractionResult> cir) {

        if(!Stellaris.CONFIG.radiationConfig.enableRadiationSystem || !placeContext.getLevel().isClientSide()) {
            return;
        }

        BlockItem blockItem = (BlockItem) (Object) this;
        BlockPlaceContext updatedPlaceContext = blockItem.updatePlacementContext(placeContext);

        BlockPos pos = updatedPlaceContext.getClickedPos();
        Level level = updatedPlaceContext.getLevel();
        ItemStack itemStack = updatedPlaceContext.getItemInHand();

        if(itemStack.getOrDefault(DataComponentsRegistry.RADIATION.get(), 0f) > 0f) {
            RadiationUtils.modifyBlockMap(level.getChunk(pos), pos, itemStack.get(DataComponentsRegistry.RADIATION.get()), false);
        }

    }
}

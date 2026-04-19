package org.exodusstudio.stellaris.common.blocks;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;

public class CoalLanternBlock extends LanternBlock {

    public CoalLanternBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, false).setValue(WATERLOGGED, false));
    }


    @Override
    protected InteractionResult useItemOn(ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (OxygenUtils.isOxygenated(level, pos) && (itemstack.is(Items.FLINT_AND_STEEL) || itemstack.is(Items.FIRE_CHARGE))) {
                if (level.getBlockState(pos).getValue(CoalLanternBlock.HANGING)) {
                    level.setBlock(pos, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true), 3);
                    this.fireManager(itemstack, (ServerPlayer) player, pos, (ServerLevel) level);
                    return InteractionResult.SUCCESS;
                } else {
                    level.setBlock(pos, Blocks.LANTERN.defaultBlockState(), 3);
                    this.fireManager(itemstack, (ServerPlayer) player, pos, (ServerLevel) level);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (itemstack.is(Items.FLINT_AND_STEEL) || itemstack.is(Items.FIRE_CHARGE)) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public void fireManager(ItemStack itemstack, ServerPlayer player, BlockPos pos, ServerLevel level) {
        if (itemstack.is(Items.FLINT_AND_STEEL)) {
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1, 1);

            itemstack.hurtAndBreak(1, level, player, (item) -> {});
        }

        else if (itemstack.is(Items.FIRE_CHARGE)) {
            level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1, 1);

            if (!player.getAbilities().instabuild && !player.isSpectator()) {
                itemstack.setCount(itemstack.getCount() - 1);
            }
        }
    }
}

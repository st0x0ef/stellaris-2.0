package org.exodusstudio.stellaris.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

import java.util.List;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (player != null && player.isShiftKeyDown()) {
            if (state.is(TagsRegistry.BlockTags.WRENCH_DESTROYABLE)) {
                if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);

                    // 1. Calculate what drops this block produces
                    List<ItemStack> drops = Block.getDrops(
                            state,
                            serverLevel,
                            pos,
                            blockEntity,
                            player,
                            context.getItemInHand()
                    );

                    // 2. Insert items directly into player inventory
                    for (ItemStack drop : drops) {
                        boolean added = player.getInventory().add(drop);

                        // If inventory is full, drop whatever didn't fit at player's feet
                        if (!added && !drop.isEmpty()) {
                            player.drop(drop, false);
                        }
                    }

                    // 3. Destroy the block WITHOUT spawning items on the ground (dropBlock = false)
                    level.destroyBlock(pos, false, player);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }
}

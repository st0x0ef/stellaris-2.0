package org.exodusstudio.stellaris.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import org.jetbrains.annotations.NotNull;

public class RoverItem extends Item {

    public RoverItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        ItemStack itemStack = context.getItemInHand();

        if (context.getLevel() instanceof ServerLevel level) {
            RoverEntity rover = RoverEntity.fromItemStack(level, itemStack);
            rover.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);

            if (level.addFreshEntity(rover)) {
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }

                /** PLACE SOUND */
                this.roverPlaceSound(pos, level);

                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    public void roverPlaceSound(BlockPos pos, Level world) {
        world.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1, 1);
    }
}

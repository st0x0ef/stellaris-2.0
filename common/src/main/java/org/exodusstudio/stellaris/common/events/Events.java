package org.exodusstudio.stellaris.common.events;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.FlagBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

public class Events {

    public static void init() {
        blockEvents();
    }


    public static void blockEvents() {
        BlockEvent.BREAK.register((level, pos, state, player, xp) -> {

            if(level.getBlockEntity(pos) instanceof FlagBlockEntity flagBlock) {
                ItemStack stack = new ItemStack(BlocksRegistry.FLAG.item().get());

                if(player.isCrouching()) {
                    stack.set(DataComponentsRegistry.DYE_COLOR.get(), flagBlock.getColor());
                    if(flagBlock.getGameProfile() != null){
                        stack.set(DataComponents.PROFILE, flagBlock.getGameProfile());
                    }
                }
                Block.popResource(level, pos, stack);
            }


            return EventResult.pass();
        });
    }

}

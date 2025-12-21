package org.exodusstudio.stellaris.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.RocketLaunchPadBlock;
import org.exodusstudio.stellaris.common.module.Modules;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

import java.util.function.Consumer;

public class RocketItem extends Item {

    public RocketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        //TODO: Create the rocket
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());

        if(blockState.is(BlocksRegistry.ROCKET_LAUNCH_PAD.block().get()) && blockState.getValue(RocketLaunchPadBlock.STAGE)) {


        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        Modules<RocketModule> modules = stack.get(DataComponentsRegistry.ROCKET_MODULES.get());

        if (modules != null && !modules.items().isEmpty()) {
            tooltipAdder.accept(Component.literal("Modules:"));
            for (RocketModule module : modules.modules) {
                tooltipAdder.accept(Component.literal("- ").append( module.displayName()).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltipAdder.accept(Component.literal("No Modules"));
        }
    }
}

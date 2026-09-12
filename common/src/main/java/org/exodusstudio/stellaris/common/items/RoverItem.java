package org.exodusstudio.stellaris.common.items;

import com.fej1fun.potentials.fluid.ItemFluidStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rover.RoverModule;
import org.exodusstudio.stellaris.common.modules.rover.RoverModules;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RoverItem extends Item implements FluidProvider.ITEM {

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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        UniversalFluidItemStorage storage = getFluidTank(stack);
        if (storage == null) {
            return;
        }

        FluidStack fuel = storage.getFluidInTank(0);
        tooltipAdder.accept(Component.translatable("tooltip.stellaris.fluid_amount", fuel.getAmount(), storage.getTankCapacity(0)).withStyle(ChatFormatting.GRAY));
        if (!fuel.isEmpty()) {
            tooltipAdder.accept(Component.translatable("tooltip.stellaris.fuel", fuel.getName()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack stack) {
        Modules<RoverModule> modules = stack.getOrDefault(DataComponentsRegistry.ROVER_MODULES.get(), RoverModules.empty());
        return new ItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), stack, 1, RoverEntity.getTankCapacity(modules));
    }
}

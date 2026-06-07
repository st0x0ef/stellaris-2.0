package org.exodusstudio.stellaris.common.items;

import org.exodusstudio.stellaris.common.components.RoverComponent;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;

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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
            RoverEntity rover = this.getRover(context.getLevel(), itemStack);
            rover.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);

            if (level.addFreshEntity(rover)) {
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }

                level.addFreshEntity(rover);

                /** PLACE SOUND */
                this.roverPlaceSound(pos, level);

                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    public RoverEntity getRover(Level level, ItemStack stack) {
        RoverEntity rover = new RoverEntity(EntityTypesRegistry.ROVER.get(), level);
        RoverComponent roverComponent = stack.get(DataComponentsRegistry.ROVER_COMPONENT.get());
        if (roverComponent != null) {
            rover.setRoverComponent(new RoverComponent(
                    roverComponent.fuelType(),
                    Math.max(roverComponent.fuel(), 0),
                    roverComponent.getFuelType().getFuelTexture(),
                    rover.tankUpgrade.getTankCapacity(),
                    rover.speedUpgrade.getSpeedModifier()));
        }
        return rover;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        RoverComponent roverComponent = stack.get(DataComponentsRegistry.ROVER_COMPONENT.get());
        if (roverComponent == null) {
            return;
        }
        tooltipComponents.add(Component.translatable("tooltip.item.stellaris.diesel", roverComponent.fuel())
                .withStyle(ChatFormatting.GRAY));
    }

    public void roverPlaceSound(BlockPos pos, Level world) {
        world.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1, 1);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        RoverComponent roverComponent = stack.get(DataComponentsRegistry.ROVER_COMPONENT.get());
        return 13 * roverComponent.fuel() / roverComponent.getTankCapacity();
    }

    @Override
    public int getBarColor(ItemStack stack) {
        RoverComponent roverComponent = stack.get(DataComponentsRegistry.ROVER_COMPONENT.get());
        return switch (roverComponent.getFuelType()) {
            case FUEL -> 0xA7E6ED;
            case DIESEL -> 0x5B2C14;
            case HYDROGEN -> 0x00D8FF;
            case RADIOACTIVE, URANIUM, NEPTUNIUM, PLUTONIUM -> 0x00C12F;
            case null -> 0xA7E6ED;

        };
    }

}

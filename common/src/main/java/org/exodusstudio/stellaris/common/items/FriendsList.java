package org.exodusstudio.stellaris.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.blocks.entities.AntennaBlockEntity;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FriendsList extends Item {

    public FriendsList(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {


        ItemStack heldStack = context.getItemInHand();
        Player player = context.getPlayer();

        if(player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }



        if(player.level().getBlockEntity(context.getClickedPos()) instanceof AntennaBlockEntity antenna) {
            List<ResolvableProfile> existingFriends = heldStack.getOrDefault(DataComponentsRegistry.GAMEPROFILE_LIST.get(), List.of());
            AntennaSavedData antennaSavedData = AntennaSavedData.getSavedAntennas(player.level().getServer());

            if(antenna.launchPadId == null) {
                player.displayClientMessage(Component.literal("This antenna hasn't been configured yet.").withStyle(ChatFormatting.GRAY), true);
                return InteractionResult.FAIL;
            }

            if(!antennaSavedData.isPlayerOwner(antenna.launchPadId, player)) {
                player.displayClientMessage(Component.literal("You don't have permission to access this antenna.").withStyle(ChatFormatting.GRAY), true);
                return InteractionResult.FAIL;
            }

            if(existingFriends.isEmpty()){
                player.displayClientMessage(Component.literal("Your friends list is empty. Use this item on another player to add them to your friends list.").withStyle(ChatFormatting.GRAY), true);
                return InteractionResult.PASS;
            } else {
                antennaSavedData.whitelistPlayers(antenna.launchPadId, existingFriends);
                player.displayClientMessage(Component.literal("Your friends has been whitelisted.").withStyle(ChatFormatting.GRAY), true);

                return InteractionResult.SUCCESS;

            }
        }
        player.displayClientMessage(Component.literal("Right click it on our.").withStyle(ChatFormatting.GRAY), true);


        return super.useOn(context);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (!(interactionTarget instanceof Player interactedPlayer)) {
            return InteractionResult.PASS;
        }

        if (!player.level().isClientSide()) {
            ItemStack heldStack = player.getItemInHand(usedHand);
            List<ResolvableProfile> existingFriends = heldStack.getOrDefault(DataComponentsRegistry.GAMEPROFILE_LIST.get(), List.of());
            String interactedName = interactedPlayer.getGameProfile().name();

            boolean alreadyFriend = existingFriends.stream().anyMatch(profile ->
                    profile.name().map(interactedName::equals).orElse(false)
            );
            if (!alreadyFriend) {
                List<ResolvableProfile> friendsList = new ArrayList<>(existingFriends);
                friendsList.add(ResolvableProfile.createResolved(interactedPlayer.getGameProfile()));
                heldStack.set(DataComponentsRegistry.GAMEPROFILE_LIST.get(), friendsList);
                player.setItemInHand(usedHand, heldStack);
                player.getInventory().setChanged();
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        MutableComponent component = Component.literal("Friends: ").withStyle(ChatFormatting.GRAY);
        List<ResolvableProfile> friendsList = stack.getOrDefault(DataComponentsRegistry.GAMEPROFILE_LIST.get(), List.of());
        if(friendsList.isEmpty()) {
            component.append(Component.literal("None"));
        } else {
            for(ResolvableProfile profile : friendsList) {
                component.append("\n").append(Component.literal("- " + profile.name().orElse("Unknown")).withStyle(ChatFormatting.GRAY));
            }
            component = Component.literal(component.getString().substring(0, component.getString().length() - 2));
        }
        tooltipAdder.accept(component);
    }
}

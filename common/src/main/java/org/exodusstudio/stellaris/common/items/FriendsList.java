package org.exodusstudio.stellaris.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

    private static Component unknownIfEmpty(ResolvableProfile profile) {
        return profile.name().<Component>map(Component::literal)
                .orElseGet(() -> Component.translatable("tooltip.stellaris.unknown"));
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
                player.sendOverlayMessage(Component.translatable("message.stellaris.friends_list.antenna_not_configured").withStyle(ChatFormatting.GRAY));
                return InteractionResult.FAIL;
            }

            if(!antennaSavedData.isPlayerOwner(antenna.launchPadId, player)) {
                player.sendOverlayMessage(Component.translatable("message.stellaris.antenna.not_owner").withStyle(ChatFormatting.GRAY));
                return InteractionResult.FAIL;
            }

            if(existingFriends.isEmpty()){
                player.sendOverlayMessage(Component.translatable("message.stellaris.friends_list.empty").withStyle(ChatFormatting.GRAY));
                return InteractionResult.PASS;
            } else {
                antennaSavedData.whitelistPlayers(antenna.launchPadId, existingFriends);
                player.sendOverlayMessage(Component.translatable("message.stellaris.friends_list.use_success").withStyle(ChatFormatting.GRAY));

                return InteractionResult.SUCCESS;

            }
        }
        player.sendOverlayMessage(Component.translatable("message.stellaris.friends_list.use_on_antenna").withStyle(ChatFormatting.GRAY));


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

            List<ResolvableProfile> friendsList = new ArrayList<>(existingFriends);
            ResolvableProfile interactedPlayerProfile = ResolvableProfile.createResolved(interactedPlayer.getGameProfile());

            if (!alreadyFriend) {
                friendsList.add(interactedPlayerProfile);
                player.sendOverlayMessage(Component.translatable("message.stellaris.friends_list.added", unknownIfEmpty(interactedPlayerProfile)).withStyle(ChatFormatting.GRAY));
            } else {
                friendsList.remove(interactedPlayerProfile);
                player.sendOverlayMessage(Component.translatable("message.stellaris.friends_list.removed", unknownIfEmpty(interactedPlayerProfile)).withStyle(ChatFormatting.GRAY));

            }

            heldStack.set(DataComponentsRegistry.GAMEPROFILE_LIST.get(), friendsList);

            player.setItemInHand(usedHand, heldStack);
            player.getInventory().setChanged();

            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        MutableComponent component = Component.translatable("tooltip.item.stellaris.friends_list.title").withStyle(ChatFormatting.GRAY);
        List<ResolvableProfile> friendsList = stack.getOrDefault(DataComponentsRegistry.GAMEPROFILE_LIST.get(), List.of());
        tooltipAdder.accept(component);

        if(friendsList.isEmpty()) {
            tooltipAdder.accept(Component.translatable("tooltip.stellaris.none"));
        } else {
            for(ResolvableProfile profile : friendsList) {
                tooltipAdder.accept(Component.translatable("tooltip.stellaris.list_entry", unknownIfEmpty(profile)).withStyle(ChatFormatting.GRAY));
            }

        }
    }
}

package org.exodusstudio.stellaris.common.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

public class MoonLoreUtils {
    public static final Identifier MOON_LORE_PROGRESSION = IdentifierUtils.id("moon_lore_progression");
    public static final Identifier PLAYER_IMMUNISED_TO_INFECTION = IdentifierUtils.id("player_immunised_to_infection");

    public static final int MAX_STAGE = 4;

    public static final Component PLAYER_ALREADY_IMMUNE_MESSAGE = Component.translatable("message.stellaris.player_already_immune");
    public static final Component PLAYER_NOW_IMMUNISED_MESSAGE = Component.translatable("message.stellaris.player_now_immunised");
    public static final Component PLAYER_NOT_READY_FOR_VACCINE = Component.translatable("message.stellaris.player_not_ready_for_vaccine");


    public static int getResearchProgressionStage(Player player) {
        if (player.stellaris$hasDataAttachments(MOON_LORE_PROGRESSION)) {
            return player.stellaris$getDataAttachments(MOON_LORE_PROGRESSION, Integer.class);
        } else {
            player.stellaris$saveDataAttachments(MOON_LORE_PROGRESSION, -1);
        }

        return -1;
    }

    public static boolean tryIncrementResearchProgressionStageIfLucky(Player player, int parasiteUsed) {
        RandomSource random = player.getRandom();
        int currentStage = getResearchProgressionStage(player);

        if (currentStage >= MAX_STAGE) {
            return false;
        }

        if (parasiteUsed >= 1) {
            // to have 100% chance to progress to the next stage, you need :
            // to go stage 0 : 1 parasite
            // to go stage 1 : 100 parasites
            // to go stage 2 : 200 parasites
            // to go stage 3 : 300 parasites
            // to go stage 4 : 400 parasites
            if (currentStage == -1) {
                player.stellaris$saveDataAttachments(MOON_LORE_PROGRESSION, 0);
                return true;
            } else {
                if (random.nextInt(100) <= parasiteUsed / (currentStage + 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static ItemStack getSdCardForStage(int stage) {
        ItemStack stack = ItemsRegistry.SD_CARD.get().getDefaultInstance();
        stack.set(DataComponentsRegistry.SD_CARD_NAME.get(), "stellaris:infection_vaccine_stage_" + stage);
        return stack;
    }

    public static boolean isPlayerImmunisedToInfection(Player player) {
        if (player.stellaris$hasDataAttachments(PLAYER_IMMUNISED_TO_INFECTION)) {
            return player.stellaris$getDataAttachments(PLAYER_IMMUNISED_TO_INFECTION, Boolean.class);
        } else {
            player.stellaris$saveDataAttachments(PLAYER_IMMUNISED_TO_INFECTION, false);
        }

        return false;
    }

    public static void immunisePlayerToInfection(Player player) {
        player.stellaris$saveDataAttachments(PLAYER_IMMUNISED_TO_INFECTION, true);
    }

    public static boolean isImmuneToInfection(LivingEntity entity) {
        if (entity.is(TagsRegistry.EntityTags.INFECTION_IMMUNE)) {
            return true;
        }

        return entity instanceof Player player && isPlayerImmunisedToInfection(player);
    }
}

package org.exodusstudio.stellaris.common.items.infection;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;

public class VaccineItem extends Item {
    public VaccineItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            if (MoonLoreUtils.getResearchProgressionStage(player) == MoonLoreUtils.MAX_STAGE) {
                if (MoonLoreUtils.isPlayerImmunisedToInfection(player)) {
                    player.displayClientMessage(MoonLoreUtils.PLAYER_ALREADY_IMMUNE_MESSAGE, false);
                } else {
                    player.displayClientMessage(MoonLoreUtils.PLAYER_NOW_IMMUNISED_MESSAGE, false);
                    MoonLoreUtils.immunisePlayerToInfection(player);
                }
            } else {
                player.displayClientMessage(MoonLoreUtils.PLAYER_NOT_READY_FOR_VACCINE, false);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}

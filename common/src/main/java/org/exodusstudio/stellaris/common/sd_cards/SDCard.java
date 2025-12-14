package org.exodusstudio.stellaris.common.sd_cards;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SDCard {

    private final SDCardInfo cardInfo;

    public SDCard(SDCardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    /**
     * This method will be run when the player click the decode button in the tablet app.
     * @param player the player that runs the card
     * @param itemStack the current sd card item stack
     */
    public abstract void run(Player player, ItemStack itemStack);

    public SDCardInfo getCardInfo() { return this.cardInfo; }

}

package org.exodusstudio.stellaris.common.sd_cards;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Base class for all SD Cards.
 * SD cards are a system that allows to give player infos like lore and more.
 */
public abstract class SDCard {

    public SDCard() {
    }


    public abstract Component getName();
    public abstract Component getDescription();

    /**
     * This method will be run when the player click the decode button in the tablet app.
     * @param player the player that runs the card
     * @param itemStack the current sd card item stack
     */
    public abstract void run(Player player, ItemStack itemStack);

}

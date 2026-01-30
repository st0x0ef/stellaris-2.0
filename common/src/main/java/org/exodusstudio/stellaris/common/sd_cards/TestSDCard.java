package org.exodusstudio.stellaris.common.sd_cards;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TestSDCard extends SDCard {

    public TestSDCard() {
        super();
    }

    @Override
    public Component getName() {
        return Component.literal("Lorem ipsum");
    }

    @Override
    public Component getDescription() {
        return Component.literal("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    }

    @Override
    public void run(Player player, ItemStack itemStack) {
        System.out.println("Run method called on Test SD Card");
    }

}

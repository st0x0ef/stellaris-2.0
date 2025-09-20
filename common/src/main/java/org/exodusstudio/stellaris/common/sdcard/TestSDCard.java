package org.exodusstudio.stellaris.common.sdcard;

import net.minecraft.network.chat.Component;

public class TestSDCard extends SDCard {

    public TestSDCard() {
        super(new SDCardInfo(Component.literal("Lorem ipsum"), Component.literal("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")));
    }

    @Override
    public void run() {
        System.out.println("Run method called on Test SD Card");
    }

}

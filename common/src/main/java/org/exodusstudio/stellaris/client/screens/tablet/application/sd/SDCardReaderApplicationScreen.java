package org.exodusstudio.stellaris.client.screens.tablet.application.sd;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.exodusstudio.stellaris.common.menus.SDCardReaderApplicationMenu;

@Environment(EnvType.CLIENT)
public class SDCardReaderApplicationScreen extends AbstractContainerScreen<SDCardReaderApplicationMenu> {

    public static SDCardReaderApplicationScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {
        return new SDCardReaderApplicationScreen(SDCardReaderApplicationMenu.create(menuHolder.))
    }

    public SDCardReaderApplicationScreen(SDCardReaderApplicationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

    }

}

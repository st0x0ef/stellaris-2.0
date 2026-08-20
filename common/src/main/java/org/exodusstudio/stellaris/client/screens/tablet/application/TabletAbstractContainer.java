package org.exodusstudio.stellaris.client.screens.tablet.application;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.lwjgl.glfw.GLFW;

public abstract class TabletAbstractContainer<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public static final Identifier BACKGROUND = IdentifierUtils.guiTexture("tablet/tablet_background");
    public static final Identifier BLANCK_BACKGROUND = IdentifierUtils.guiTexture("tablet/tablet_background_blanck");
    public static final Identifier SIDE_LIGHTS = IdentifierUtils.guiTexture("tablet/side_light");


    public TabletAbstractContainer(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    public TabletAbstractContainer(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);
    }



    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if((event.button() == GLFW.GLFW_MOUSE_BUTTON_LAST || event.button() == GLFW.GLFW_MOUSE_BUTTON_4) && this.canGoBack()) {
            NetworkManager.sendToServer(new OpenMenuPacket("main_tablet"));
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if(event.key() == GLFW.GLFW_KEY_ESCAPE && this.canGoBack()) {
            NetworkManager.sendToServer(new OpenMenuPacket("main_tablet"));
            return true;
        }

        return super.keyPressed(event);
    }

    public boolean canGoBack() {
        return true;
    }

}

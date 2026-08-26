package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class TabbedMachineScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public static final int TAB_WIDTH = 16;

    protected final int backgroundWidth;

    protected TabbedMachineScreen(T menu, Inventory inventory, Component title, int backgroundWidth, int backgroundHeight) {
        super(menu, inventory, title, backgroundWidth + TAB_WIDTH, backgroundHeight);

        this.backgroundWidth = backgroundWidth;

        this.titleLabelX = (backgroundWidth - Minecraft.getInstance().font.width(title)) / 2;
        this.titleLabelY = 2;
    }

    protected int getTabsX() {
        return this.leftPos + this.backgroundWidth;
    }
}

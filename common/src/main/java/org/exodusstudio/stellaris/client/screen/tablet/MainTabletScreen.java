package org.exodusstudio.stellaris.client.screen.tablet;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screen.components.ScrollableContainer;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;

public class MainTabletScreen extends AbstractContainerScreen<MainTabletMenu> {

    public ScrollableContainer<AbstractWidget> container;

    public MainTabletScreen(MainTabletMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

    }

    @Override
    protected void init() {
        //container = new ScrollableContainer<>(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        //        .addChild(this, Button.builder(Component.literal("ee"), button -> {
        //            // Handle button click
        //        }).bounds(20, 20, 100, 20).build());


        //this.addRenderableWidget(container);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(minecraft.font, Component.literal("eeee"), leftPos + imageWidth / 2, topPos + 6, 0xFFFFFF);
    }
}

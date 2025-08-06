package org.exodusstudio.stellaris.client.screen.tablet;

import com.mojang.datafixers.kinds.App;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.client.screen.components.TexturedButton;
import org.exodusstudio.stellaris.client.screen.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MainTabletScreen extends AbstractContainerScreen<MainTabletMenu> {

    public static final ResourceLocation BACKGROUND = ResourceLocationUtils.guiTexture("tablet/tablet_background");

    public final ArrayList<ArrayList<TexturedButton>> APPLICATIONS = new ArrayList<>();
    private int currentPage = 0;

    public ScrollableContainer container;

    public final Player player;

    public MainTabletScreen(MainTabletMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
    }

    @Override
    protected void init() {
        container = (ScrollableContainer) new ScrollableContainer(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
                .addChild(this, Button.builder(Component.literal("ee"), button -> {
                    // Handle button click
                }).bounds(20, 20, 100, 20).build());



        createAppsButton();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }


    private void createAppsButton() {
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger column = new AtomicInteger(0);

        ApplicationRegistry.TABLET_APPLICATION.entrySet().forEach(entry -> {
            ApplicationRegistry.ApplicationFactory infos = entry.getValue();
            TexturedButton tabletButton = new TexturedButton(this.leftPos + 68 + (column.get() * 30), this.topPos + 60 + (row.get() * 30), 20, 20, infos.getName(), (button -> minecraft.setScreen(infos.createScreen(this))))
                    .tex(infos.getIconLocation(), infos.getIconLocation());

            if (column.get() == 3) {
                column.set(0);
                row.getAndIncrement();
            }
            else {
                column.getAndIncrement();
            }

            if (row.get() == 2 && column.get() == 3) {
                column.set(0);
                row.set(0);
            }

            this.addButtonToList(tabletButton, 6);
            tabletButton.visible = true;
            this.addRenderableWidget(tabletButton);

        });
    }

    public void addButtonToList(TexturedButton button, int size){
        if (APPLICATIONS.isEmpty()) {
            ArrayList<TexturedButton> list = new ArrayList<>();
            list.add(button);
            APPLICATIONS.add(list);
            return;
        }

        for (ArrayList<TexturedButton> buttons : APPLICATIONS) {
            if(buttons.size() < size){
                buttons.add(button);
                break;
            } else if (buttons.size() == size) {
                if (APPLICATIONS.indexOf(buttons) + 1 >= APPLICATIONS.size()) {
                    ArrayList<TexturedButton> list = new ArrayList<>();
                    list.add(button);
                    APPLICATIONS.add(list);
                    break;
                }
            }
        }

    }

    public int getLeftPos() {
        return this.leftPos;
    }

    public int getTopPos() {
        return this.topPos;
    }

    public Player getPlayer() {
        return player;
    }
}

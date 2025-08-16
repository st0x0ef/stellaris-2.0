package org.exodusstudio.stellaris.client.screen.tablet;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.client.screen.components.TexturedButton;
import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MainTabletScreen extends AbstractContainerScreen<MainTabletMenu> {

    public static final ResourceLocation BACKGROUND = ResourceLocationUtils.guiTexture("tablet/tablet_background");

    public final ArrayList<ArrayList<TexturedButton>> APPLICATIONS = new ArrayList<>();
    private int currentPage = 0;

    public final Player player;
    public final Inventory inventory;

    public MainTabletScreen(MainTabletMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.player = playerInventory.player;
        this.imageHeight = 162;
        this.imageWidth = 250;
        this.inventory = playerInventory;
        this.inventoryLabelY = -this.imageHeight;
        this.titleLabelY = -this.imageHeight;

    }

    @Override
    protected void init() {
        super.init();

        createAppsButton();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0, 0, 250, 162, 250, 162);
    }


    private void createAppsButton() {
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger column = new AtomicInteger(0);

        ApplicationRegistry.TABLET_APPLICATION.entrySet().forEach(entry -> {
            ApplicationRegistry.ApplicationFactory infos = entry.getValue();
            TexturedButton tabletButton = new TexturedButton(this.leftPos + 68 + (column.get() * 30), this.topPos + 60 + (row.get() * 30), 20, 20, infos.getName(), (button -> minecraft.setScreen(infos.createScreen(this.createMenuHolder()))))
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

            ClientUtils.addButtonToList(APPLICATIONS, tabletButton, 6);
            tabletButton.visible = true;
            this.addRenderableWidget(tabletButton);

        });
    }

    //Add button to the current page list, if the current page is full, create a new page
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

    public ApplicationRegistry.MenuHolder createMenuHolder() {
        return new ApplicationRegistry.MenuHolder(this.menu, this.inventory, this);
    }
}

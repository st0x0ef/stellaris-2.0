package org.exodusstudio.stellaris.client.screens.tablet;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
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
        this.imageHeight = 192;
        this.imageWidth = 310;
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
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }


    private void createAppsButton() {
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger column = new AtomicInteger(0);

        ApplicationRegistry.TABLET_APPLICATION.entrySet().forEach(entry -> {
            ApplicationRegistry.ApplicationFactory infos = entry.getValue();

            MutableComponent tooltip = infos.name().copy();
            tooltip.append("\n").append(infos.description().withStyle(ChatFormatting.GRAY));

            TexturedButton tabletButton = new TexturedButton(this.leftPos + 68 + (column.get() * 30), this.topPos + 60 + (row.get() * 30), 20, 20, infos.name(), (button ->  {
                Screen screen = infos.createScreen(this.createMenuHolder());
                if (screen != null) {
                    minecraft.setScreen(screen);
                }

            }))
                    .tex(infos.iconLocation(), infos.iconHoverLocation())
                    .useSprite(true)
                    .tooltip(Tooltip.create(tooltip, infos.description()));

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

    public int getLeftPos() {
        return this.leftPos;
    }

    public int getTopPos() {
        return this.topPos;
    }

    public int getImageWidth() {
        return this.imageWidth;
    }

    public int getImageHeight() {
        return this.imageHeight;
    }

    public Player getPlayer() {
        return player;
    }

    public ApplicationRegistry.MenuHolder<?> createMenuHolder() {
        return new ApplicationRegistry.MenuHolder<>(this.menu, this.inventory, this);
    }
}

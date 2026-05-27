package org.exodusstudio.stellaris.client.screens.tablet;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.lwjgl.glfw.GLFW;

public class MainTabletScreen extends AbstractContainerScreen<MainTabletMenu> {

    public static final Identifier BACKGROUND = IdentifierUtils.guiTexture("tablet/tablet_background");

    public final ArrayList<ArrayList<TexturedButton>> APPLICATIONS = new ArrayList<>();
    private int currentPage = 0;

    public final Player player;
    public final Inventory inventory;
    private final TabletAnimation animation = new TabletAnimation();


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
        this.openNextScreen(menu.nextScreen);

        createAppsButton();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.animation.finishClosing(this::closeWithoutAnimation)) {
            return;
        }

        float centerX = this.leftPos + this.imageWidth / 2.0F;
        float centerY = this.topPos + this.imageHeight / 2.0F;
        int transformedMouseX = this.animation.transformMouseX(mouseX, centerX, partialTick);
        int transformedMouseY = this.animation.transformMouseY(mouseY, centerY, partialTick);

        this.animation.renderBackdrop(guiGraphics, this.width, this.height, partialTick);
        this.animation.renderTabletShadow(guiGraphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, partialTick);
        this.animation.pushScreen(guiGraphics, centerX, centerY, partialTick);
        this.renderBg(guiGraphics, partialTick, transformedMouseX, transformedMouseY);
        super.render(guiGraphics, transformedMouseX, transformedMouseY, partialTick);
        this.animation.renderGlassEffects(guiGraphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, partialTick);
        this.animation.popScreen(guiGraphics);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // The tablet texture is drawn inside render() so it shares the tablet transform.
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}


    private void createAppsButton() {
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger column = new AtomicInteger(0);

        ApplicationRegistry.getApplications().forEach((key, value) -> {
            ApplicationRegistry.ApplicationFactory<?> infos = value;

            MutableComponent tooltip = infos.name().copy();
            tooltip.append("\n").append(infos.description().withStyle(ChatFormatting.GRAY));

            TexturedButton tabletButton = new TexturedButton(this.leftPos + 68 + (column.get() * 30), this.topPos + 60 + (row.get() * 30), 20, 20, Component.empty(), (button -> {
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
            } else {
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

    public void openNextScreen(@Nullable Identifier nextScreen) {
        if(nextScreen != null) {
            ApplicationRegistry.ApplicationFactory<?> infos = ApplicationRegistry.getApplications().get(nextScreen);
            if (infos != null) {

                Screen screen = infos.createScreen(this.createMenuHolder());
                if (screen != null) {
                    Minecraft.getInstance().setScreen(screen);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.animation.isClosing()) {
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.animation.startClosing();
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        if (this.animation.shouldInterceptClose()) {
            return;
        }

        this.closeWithoutAnimation();
    }

    private void closeWithoutAnimation() {
        super.onClose();
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

    @SuppressWarnings("unchecked")
    public <T extends AbstractContainerMenu> ApplicationRegistry.MenuHolder<T> createMenuHolder() {
        return new ApplicationRegistry.MenuHolder<>((T) this.menu, this.inventory, this);
    }
}

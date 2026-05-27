package org.exodusstudio.stellaris.client.screens.tablet.application.stats;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stats;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.components.stats.StatWidget;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.TabletAnimation;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.registries.StatsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.lwjgl.glfw.GLFW;

public class StatsApplicationScreen extends Screen {

    private static final Identifier BACKGROUND = IdentifierUtils.guiTexture("tablet/stats/stat_screen");

    private final MainTabletScreen mainTabletScreen;

    private ScrollableContainer scrollableContainer;
    private final TabletAnimation animation = new TabletAnimation();

    public StatsApplicationScreen(MainTabletScreen mainTabletScreen) {
        super(Component.literal("Stats"));
        this.mainTabletScreen = mainTabletScreen;
    }

    public static StatsApplicationScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {
        return new StatsApplicationScreen(menuHolder.mainTabletScreen());
    }

    @Override
    protected void init() {
        super.init();

        setupScrollableContainer();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.getChildAt(mouseX, mouseY).filter((guiEventListener) -> {
            if(this.scrollableContainer.children().contains(guiEventListener)) {
                return this.scrollableContainer.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            } else {
                return guiEventListener.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        }).isPresent();

    }

    private void setupScrollableContainer() {
        scrollableContainer = new CustomStatsScrollableContainer(this.getLeftPos() + 26, this.getTopPos() + 30, 258, this.mainTabletScreen.getImageHeight() - 50, Component.empty());

        int height = 5;

        for (RegistrySupplier<Identifier> val : StatsRegistry.STATS) {
            Component name = Component.translatable("stat.stellaris." + val.get().toString().split(":")[1]);
            Component value = Component.literal(String.valueOf(Minecraft.getInstance().player.getStats().getValue(Stats.CUSTOM.get(val.get()))));

            StatWidget statWidget = new StatWidget(this.scrollableContainer.getX() + 4, this.scrollableContainer.getY() + height, 260, 20, name, value, scrollableContainer);
            this.scrollableContainer.addChild(this, statWidget);
            height += 25;
        }

        scrollableContainer.setContentHeight(height);
        this.addRenderableWidget(scrollableContainer);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.animation.finishClosing(this::closeWithoutAnimation)) {
            return;
        }

        float centerX = this.getLeftPos() + this.mainTabletScreen.getImageWidth() / 2.0F;
        float centerY = this.getTopPos() + this.mainTabletScreen.getImageHeight() / 2.0F;
        int transformedMouseX = this.animation.transformMouseX(mouseX, centerX, partialTick);
        int transformedMouseY = this.animation.transformMouseY(mouseY, centerY, partialTick);

        this.animation.renderBackdrop(guiGraphics, this.width, this.height, partialTick);
        this.animation.renderTabletShadow(guiGraphics, this.getLeftPos(), this.getTopPos(), this.mainTabletScreen.getImageWidth(), this.mainTabletScreen.getImageHeight(), partialTick);
        this.animation.pushScreen(guiGraphics, centerX, centerY, partialTick);
        this.renderTabletBackground(guiGraphics);
        super.render(guiGraphics, transformedMouseX, transformedMouseY, partialTick);
        this.animation.renderGlassEffects(guiGraphics, this.getLeftPos(), this.getTopPos(), this.mainTabletScreen.getImageWidth(), this.mainTabletScreen.getImageHeight(), partialTick);
        this.animation.popScreen(guiGraphics);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // The tablet texture is drawn inside render() so it shares the tablet transform.
    }

    @Override
    public void resize(int width, int height) {
        this.mainTabletScreen.resize(width, height);
        super.resize(width, height);
    }

    public int getLeftPos() {
        return this.mainTabletScreen.getLeftPos();
    }

    public int getTopPos() {
        return this.mainTabletScreen.getTopPos();
    }

    private void renderTabletBackground(GuiGraphics guiGraphics) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.mainTabletScreen.getImageWidth(), this.mainTabletScreen.getImageHeight(), this.mainTabletScreen.getImageWidth(),this.mainTabletScreen.getImageHeight());
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

    private static class CustomStatsScrollableContainer extends ScrollableContainer {

        public CustomStatsScrollableContainer(int x, int y, int width, int height, Component component) {
            super(x, y, width, height, component);
        }

        @Override
        protected int scrollBarX() {
            return this.getRight() - 3;
        }

    }

}

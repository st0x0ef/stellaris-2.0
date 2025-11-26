package org.exodusstudio.stellaris.client.screens.tablet.application.stats;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.components.stats.StatWidget;
import org.exodusstudio.stellaris.client.screens.components.wiki.WikiEntryButton;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.exodusstudio.stellaris.common.registries.StatsRegistry;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class StatsApplicationScreen extends Screen {

    private static final ResourceLocation BACKGROUND = ResourceLocationUtils.guiTexture("tablet/stats/stat_screen");

    private final MainTabletScreen mainTabletScreen;

    private ScrollableContainer scrollableContainer;

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

    private void setupScrollableContainer() {
        scrollableContainer = new ScrollableContainer(this.getLeftPos() + 27, this.getTopPos() + 30, 258, this.mainTabletScreen.getImageHeight() - 50, Component.empty());

        int height = 5;

        for (RegistrySupplier<ResourceLocation> val : StatsRegistry.STATS) {
            Component name = Component.translatable("stat.stellaris." + val.get().toString().split(":")[1]);
            Component value = Component.literal(String.valueOf(Minecraft.getInstance().player.getStats().getValue(Stats.CUSTOM.get(val.get())))).append(" km");

            StatWidget statWidget = new StatWidget(this.scrollableContainer.getX() + 4, this.scrollableContainer.getY() + height, 260, 20, name, value, scrollableContainer);
            this.scrollableContainer.addChild(this, statWidget);
            height += 25;
        }

        scrollableContainer.setContentHeight(height);
        this.addRenderableWidget(scrollableContainer);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.mainTabletScreen.getImageWidth(), this.mainTabletScreen.getImageHeight(), this.mainTabletScreen.getImageWidth(),this.mainTabletScreen.getImageHeight());
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.mainTabletScreen.resize(minecraft, width, height);
        super.resize(minecraft, width, height);
    }

    public int getLeftPos() {
        return this.mainTabletScreen.getLeftPos();
    }

    public int getTopPos() {
        return this.mainTabletScreen.getTopPos();
    }

}

package org.exodusstudio.stellaris.client.screens.tablet.application.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.client.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.screens.components.wiki.WikiInfosWidget;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.lwjgl.glfw.GLFW;

/**
 * WikiEntryScreen
 * This screen displays a specific wiki entry and allows navigation between infos.
 */
public class WikiEntryScreen extends Screen {

    public EntryInfo info;
    public WikiInfosWidget widget;
    public MainTabletScreen tabletScreen;
    public WikiApplicationScreen.WikiState wikiState;

    protected WikiEntryScreen(MainTabletScreen tabletScreen, WikiApplicationScreen.WikiState wikiState, EntryInfo info) {
        super(Component.literal(info.title()));
        this.tabletScreen = tabletScreen;
        this.info = info;
        this.wikiState = wikiState;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, title, this.width / 2 + 3, this.getTopPos() + 30, Utils.getMinecraftColor("white"));
    }

    @Override
    protected void init() {
        super.init();
        this.widget = new WikiInfosWidget(this.getLeftPos() + 40,  this.getTopPos() + 45,230, 128, this.info);
        this.addRenderableWidget(this.widget);

    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.tabletScreen.resize(minecraft, width, height);
        this.tabletScreen.init(minecraft, width, height);
        super.resize(minecraft, width, height);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ApplicationScreen.BLANCK_BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.tabletScreen.getImageWidth(), this.tabletScreen.getImageHeight(), this.tabletScreen.getImageWidth(),this.tabletScreen.getImageHeight());

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ApplicationScreen.BLANCK_BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.tabletScreen.getImageWidth(), this.tabletScreen.getImageHeight(), this.tabletScreen.getImageWidth(),this.tabletScreen.getImageHeight());

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.widget.mouseMoved(mouseX, mouseY);

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.setScreen(this.wikiState.toScreen(this.tabletScreen));
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    public int getLeftPos() {
        return this.tabletScreen.getLeftPos();
    }

    public int getTopPos() {
        return this.tabletScreen.getTopPos();
    }

}

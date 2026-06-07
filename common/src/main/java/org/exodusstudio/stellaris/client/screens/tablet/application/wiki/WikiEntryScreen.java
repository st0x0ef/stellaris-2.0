package org.exodusstudio.stellaris.client.screens.tablet.application.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.common.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.components.wiki.WikiInfosWidget;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.lwjgl.glfw.GLFW;

/**
 * WikiEntryScreen
 * This screen displays a specific wiki entry and allows navigation between infos.
 */
public class WikiEntryScreen extends Screen {

    public EntryInfo info;
    public WikiInfosWidget widget;
    public WikiApplicationScreen wikiApplicationScreen;
    public WikiApplicationScreen.WikiState wikiState;

    protected WikiEntryScreen(WikiApplicationScreen wikiApplicationScreen, WikiApplicationScreen.WikiState wikiState, EntryInfo info) {
        super(Component.literal(info.title()));
        this.wikiApplicationScreen = wikiApplicationScreen;
        this.info = info;
        this.wikiState = wikiState;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.widget.info.title(), this.width / 2 + 3, this.getTopPos() + 30, Utils.getMinecraftColor("white"));
    }

    @Override
    protected void init() {
        super.init();

        int wikiEntryX = this.getLeftPos() + 40;
        int wikiEntryY = this.getTopPos() + 45;

        this.addRenderableWidget(new TexturedButton(wikiEntryX - 18,  wikiEntryY - 18, 16, 16,
                (b) -> this.minecraft.setScreen(this.wikiState.toScreen(wikiApplicationScreen)))
                .tex(IdentifierUtils.texture("gui/tablet/back_page"), IdentifierUtils.texture("gui/tablet/back_page_hover")));

        this.widget = new WikiInfosWidget(wikiEntryX,  wikiEntryY,230, 128, this.info);
        this.addRenderableWidget(this.widget);

    }

    @Override
    public void resize(int width, int height) {
        this.wikiApplicationScreen.resize(width, height);
        super.resize(width, height);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ApplicationScreen.BLANCK_BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, wikiApplicationScreen.getImageWidth(), this.wikiApplicationScreen.getImageHeight(), this.wikiApplicationScreen.getImageWidth(),this.wikiApplicationScreen.getImageHeight());

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.widget.mouseMoved(mouseX, mouseY);

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if(event.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.setScreen(this.wikiState.toScreen(this.wikiApplicationScreen));
            //return true;
        }

        return super.keyPressed(event);
    }

    public int getLeftPos() {
        return this.wikiApplicationScreen.getLeftPos();
    }

    public int getTopPos() {
        return this.wikiApplicationScreen.getTopPos();
    }

}

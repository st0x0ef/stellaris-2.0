package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.common.data.wiki.MarkdownPage;
import org.exodusstudio.stellaris.client.screens.components.Padding;
import org.exodusstudio.stellaris.client.screens.components.wiki.ScrollStellarDownWidget;
import org.exodusstudio.stellaris.common.data.wiki.WikiMarkdownData;

public class TestScreen extends Screen {

    ScrollStellarDownWidget stellarDownWidget;

    public TestScreen() {
        super(Component.empty());
        this.width = 310;
        this.height = 192;
    }


    @Override
    protected void init() {
        super.init();

        MarkdownPage page = WikiMarkdownData.ENTRY_PAGES.values().iterator().next();


        int x = this.width / 2 - 200;
        this.stellarDownWidget = new ScrollStellarDownWidget(x, 50, 800, height, page);
        this.stellarDownWidget.setPadding(new Padding(0, 0, 0, 40));


        this.addRenderableWidget(stellarDownWidget);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.stellarDownWidget.mouseMoved(mouseX, mouseY);

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.stellarDownWidget != null && this.stellarDownWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

}

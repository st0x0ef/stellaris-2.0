package org.exodusstudio.stellaris.client.screens.components.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.markdown.MarkdownPage;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.utils.ActionBox;
import org.exodusstudio.stellaris.client.utils.stellardown.StellardownRenderer;
import org.exodusstudio.stellaris.common.data.wiki.MarkdownData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CopyOnWriteArrayList;

public class ScrollStellarDownWidget extends ScrollableContainer {


    public MarkdownPage page;

    public StellardownRenderer renderer;

    public int finalHeight = 0;

    private final CopyOnWriteArrayList<ActionBox> actionBoxes = new CopyOnWriteArrayList<>();

    private boolean firstRender = true;

    public ScrollStellarDownWidget(int x, int y, int width, int height, MarkdownPage page) {
        super(x, y, width, height, Component.empty());
        this.page = page;
        this.firstRender = true;
        this.renderer = new StellardownRenderer(page.content, width, Minecraft.getInstance().font);
    }


    @Override
    protected int contentHeight() {
        return this.finalHeight;
    }

    @Override
    public void renderContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.finalHeight = renderer.render(this.getX(), (int) this.getOffsetHeight(), guiGraphics, mouseX, mouseY, this::addClickBox);
        firstRender = false;
    }

    public void addClickBox(ActionBox box) {
        if(this.firstRender) {
            this.actionBoxes.add(box);
        }
    }

    @Override
    @SuppressWarnings("nullness")
    public void mouseMoved(double mouseX, double mouseY) {
        for (ActionBox clickBox : actionBoxes) {

            if (clickBox.isHovered(mouseX,mouseY, this.scrollAmount())) {
                //clickBox.onHover(this);
            }
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("nullness")
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        for (ActionBox clickBox : actionBoxes) {
            if (clickBox.isHovered(event.x(), event.y(), this.scrollAmount())) {
                changePage(clickBox.getData("ref"));
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    public void changePage(@Nullable String page) {
        if (page == null) return;

        Identifier pageId = Identifier.tryParse(page);
        //Find the page
        MarkdownPage newPage = MarkdownData.ENTRY_PAGES.get(pageId);

        if(newPage ==null) {
            Stellaris.LOG.error("Could not find page with id: " + pageId);
            return;
        }

        refresh(newPage);

    }

    public void refresh(@NotNull MarkdownPage newPage) {
        this.page = newPage;
        this.setScrollAmount(0);
        actionBoxes.clear();
        this.finalHeight = 0;
        this.firstRender = true;
        this.renderer = new StellardownRenderer(newPage.content, this.getWidth(), Minecraft.getInstance().font);

    }
}

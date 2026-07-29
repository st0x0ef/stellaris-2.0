package org.exodusstudio.stellaris.client.screens.components.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.markdown.MarkdownPage;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.utils.ActionBox;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.client.utils.minedown.StellardownRenderer;
import org.exodusstudio.stellaris.common.data.wiki.EntryInfo;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ScrollStellarDownWidget extends ScrollableContainer {


    public MarkdownPage page;

    public StellardownRenderer renderer;

    public int finalHeight = 0;

    private final CopyOnWriteArrayList<ActionBox<ScrollStellarDownWidget>> actionBoxes = new CopyOnWriteArrayList<>();

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
        Stellaris.LOG.error("height {}", this.finalHeight);
        firstRender = false;
    }

    public void addClickBox(ActionBox<ScrollStellarDownWidget> box) {

        if(this.firstRender) {
            this.actionBoxes.add(box);
        }
    }

    @Override
    @SuppressWarnings("nullness")
    public void mouseMoved(double mouseX, double mouseY) {
        for (ActionBox<ScrollStellarDownWidget> clickBox : actionBoxes) {

            if (clickBox.isHovered(mouseX,mouseY, this.scrollAmount())) {
                clickBox.onHover(this);
            }
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("nullness")
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        for (ActionBox<ScrollStellarDownWidget> clickBox : actionBoxes) {
            if (clickBox.isHovered(event.x(), event.y(), this.scrollAmount())) {
                clickBox.onClick(this);
            }
        }

        return super.mouseClicked(event, isDoubleClick);
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

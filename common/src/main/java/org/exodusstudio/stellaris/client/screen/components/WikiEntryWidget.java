package org.exodusstudio.stellaris.client.screen.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.screen.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.client.screen.tablet.application.wiki.WikiEntryScreen;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.client.utils.WikiEntryTextRenderer;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WikiEntryWidget extends AbstractScrollArea {

    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocationUtils.id("icon/scroller");

    private final AtomicInteger finalHeight = new AtomicInteger(0);
    private WikiEntry.EntryInfo info;
    private int baseScreenWidth;
    private final WikiEntryScreen screen;
    private final ArrayList<ClickBox> clickBoxes = new ArrayList<>();

    public WikiEntryWidget(int x, int y, int width, int height, WikiEntry.EntryInfo info, WikiEntryScreen screen) {
        super(x, y, width, height, Component.literal(info != null ? info.title() : "Wiki Entry"));
        this.info = info;
        this.baseScreenWidth = screen.width;
        this.screen = screen;
    }

    @Override
    protected int contentHeight() {
        return 200;
    }

    @Override
    protected double scrollRate() {
        return 9;
    }
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        Stellaris.LOG.error("{} {}", this.getX() + 5, this.getY() + 5);
        Stellaris.LOG.error("{} {}", mouseX, mouseY);

        guiGraphics.drawString(getFont(), "Test", this.getX() + 5, this.getY() + 5, Utils.getColorHexCode("white"), false);
        if (this.visible) {
            guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());

            this.renderContents(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.disableScissor();
            this.renderScrollbar(guiGraphics);
        }
    }


    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.info == null) {
            return;
        }

        finalHeight.set(0);
        for(WikiEntry.InfoComponent component : info.components()) {

            switch (component.type()) {
                case "text" -> component.text().ifPresent((text) -> {
                    int descriptionHeight = new WikiEntryTextRenderer(component.text().get(), getWidth() - 20)
                            .renderWords(guiGraphics, getX() + 5, getY() + finalHeight.get() + 30, clickBoxes::add);
                    finalHeight.addAndGet(descriptionHeight);
                });
                case "image" -> component.image().ifPresent((image) -> {
                    int height = getY() + 40 + finalHeight.get() + 20;
                    guiGraphics.blit(image.location().withSuffix(".png"), this.baseScreenWidth / 2 - image.width() / 2, height, 0, 0, image.width(), image.height(), image.width(), image.height());
                    finalHeight.addAndGet(image.height() + 40);
                });
                case "item" -> component.item().ifPresent((item) -> {
                    if (item.onlyIcon().isEmpty() || !item.onlyIcon().get()) {
                        guiGraphics.renderItem(item.stack(), this.baseScreenWidth / 2 - (int) item.size() / 2, getY() + finalHeight.get() + 35 + 20);
                        finalHeight.addAndGet(35 + (int) (item.size() / 4));
                    }
                });
                case "entity" -> component.entity().ifPresent((entity) -> {
                    int height = getY() + 40 + finalHeight.get() + entity.scale();
                    Entity entity1 = ClientUtils.createEntity(Minecraft.getInstance().level, entity.entity());
                    if(entity1 instanceof LivingEntity livingEntity) {
                        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, this.baseScreenWidth / 2, height + 45, (this.baseScreenWidth / 2) + 20, height + 45 + 20, entity.scale(), 0.25F, mouseX, mouseY, livingEntity);
                        finalHeight.addAndGet(80);
                        //TODO : set the right height
                    }
                });
            }
        }
    }

    public void resize(WikiEntryScreen screen) {
        this.baseScreenWidth = screen.width;

        this.setInfo(screen.currentPage);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    protected void renderScrollbar(GuiGraphics guiGraphics) {
        if (this.scrollbarVisible()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_SPRITE, this.scrollBarX(), this.scrollerHeight(), 6, this.scrollBarY());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ClickBox clickBox : clickBoxes) {
            if (clickBox.isHovered((int) mouseX, (int) mouseY, (int) scrollAmount())) {
                clickBox.changePage(screen);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public WikiEntry.EntryInfo getInfo() {
        return info;
    }

    public Font getFont() {
        return Minecraft.getInstance().font;
    }

    public void setInfo( WikiEntry.EntryInfo info) {
        this.info = info;
        this.setScrollAmount(0);
    }

    public record ClickBox(int x, int y, int width, int height, String action) {

        public boolean isHovered(int mouseX, int mouseY, int finalHeight) {
            mouseY += finalHeight;
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }

        public void changePage(WikiEntryScreen entryScreen) {
            ResourceLocation location = ResourceLocation.parse(action);

            var entryInfo = WikiApplicationScreen.getEntryInfo(location);

            if(entryInfo != null) {
                entryScreen.widget.setInfo(entryInfo);
            }
        }
    }
}

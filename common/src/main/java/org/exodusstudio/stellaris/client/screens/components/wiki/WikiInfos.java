package org.exodusstudio.stellaris.client.screens.components.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiEntryScreen;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.client.utils.WikiEntryTextRenderer;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WikiInfos extends ScrollableContainer {

    private AtomicInteger finalHeight = new AtomicInteger(0);


    public EntryInfo entry;
    private final ArrayList<ClickBox> clickBoxes = new ArrayList<>();

    public WikiInfos(int baseX, int baseY, int width, int height) {
        super(baseX, baseY, width, height, Component.empty());
    }

    @Override
    protected int contentHeight() {
        return this.finalHeight.intValue();
    }

    @Override
    public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        finalHeight.set(0);

        if(entry == null) return;

        for(EntryInfo.InfoComponent component : entry.components()) {

            switch (component.type().toLowerCase()) {
                case "text" -> component.text().ifPresent((text) -> {
                    int descriptionHeight = new WikiEntryTextRenderer(component.text().get(), getWidth() - 30)
                            .renderWords(guiGraphics, this.getX() + 5, (int) (this.getOffsetHeight() + finalHeight.get() + 30), clickBoxes::add);
                    finalHeight.addAndGet(descriptionHeight);
                });
                case "image" -> component.image().ifPresent((image) -> {
                    int height = (int) (this.getOffsetHeight() + 40 + finalHeight.get() + 20);
                    guiGraphics.blit(image.location().withSuffix(".png"), this.getWidth() / 2 - image.width() / 2, height, 0, 0, image.width(), image.height(), image.width(), image.height());
                    finalHeight.addAndGet(image.height() + 40);
                });
                case "item" -> component.item().ifPresent((item) -> {
                    if (item.onlyIcon().isEmpty() || !item.onlyIcon().get()) {
                        guiGraphics.renderItem(item.stack(), guiGraphics.guiWidth() / 2, (int) (this.getOffsetHeight() + finalHeight.get()));
                        finalHeight.addAndGet(35);
                    }
                });
                case "entity" -> component.entity().ifPresent((entity) -> {
                    int height = (int) (this.getOffsetHeight() + finalHeight.get() + entity.scale());
                    Entity entity1 = ClientUtils.createEntity(Minecraft.getInstance().level, entity.entity());
                    if(entity1 instanceof LivingEntity livingEntity) {

                        int cornerX = guiGraphics.guiWidth() / 2 - 25;

                        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, cornerX, height, cornerX + 50, height + entity.scale() + 30, entity.scale(), 0.25F, mouseX, mouseY, livingEntity);
                        finalHeight.addAndGet(height + entity.scale() + 30);
                    }
                });
            }
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ClickBox clickBox : clickBoxes) {
            if (clickBox.isHovered(mouseX,mouseY,this.getOffsetHeight())) {
                Stellaris.LOG.error("Clicked on box: {}", clickBox.action());
                //clickBox.changePage(screen);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    public void refresh(EntryInfo entryInfo) {
        this.entry = entryInfo;
        this.clickBoxes.clear();
        this.setScrollAmount(0);
    }


    public record ClickBox(int x, int y, int width, int height, String action) {

        public boolean isHovered(double mouseX, double mouseY, double finalHeight) {
            mouseY += finalHeight;
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }

        public void changePage(WikiEntryScreen entryScreen) {
            ResourceLocation location = ResourceLocation.parse(action);

            var entryInfo = WikiApplicationScreen.getEntryInfo(location);
            if(entryInfo != null) {
                //entryScreen.widget.refreshList();
                //entryScreen.widget.setInfo(entryInfo);
            }
        }
    }
}

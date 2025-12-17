package org.exodusstudio.stellaris.client.screens.components.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.client.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.utils.ActionBox;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.client.utils.WikiEntryTextRenderer;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WikiInfosWidget extends ScrollableContainer {

    private AtomicInteger finalHeight = new AtomicInteger(0);

    public EntryInfo info;
    private final ArrayList<ActionBox> actionBoxes = new ArrayList<>();


    public WikiInfosWidget(int baseX, int baseY, int width, int height, EntryInfo info) {
        this(baseX, baseY, width, height);
        this.info = info;
    }

    public WikiInfosWidget(int baseX, int baseY, int width, int height) {
        super(baseX, baseY, width, height, Component.empty());
    }

    @Override
    protected int contentHeight() {
        return this.finalHeight.intValue();
    }

    @Override
    public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        finalHeight.set(0);

        if(info == null) return;

        for(EntryInfo.InfoComponent component : info.components()) {

            switch (component.type().toLowerCase()) {
                case "text" -> component.text().ifPresent((text) -> {
                    int descriptionHeight = new WikiEntryTextRenderer(component.text().get(), getWidth() - 30)
                            .renderWords(guiGraphics, this.getX() + 5, (int) (this.getOffsetHeight() + finalHeight.get() + 5), mouseX, mouseY, this::addClickBox);
                    finalHeight.addAndGet(descriptionHeight);
                });
                case "image" -> component.image().ifPresent((image) -> {
                    int height = (int) (this.getOffsetHeight() + 40 + finalHeight.get() + 20);
                    guiGraphics.blit(image.formatFileLocation(), this.getWidth() / 2 - image.width() / 2, height, 0, 0, image.width(), image.height(), image.width(), image.height());
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
                    Entity entity1 = ClientUtils.createEntity(Minecraft.getInstance().level, entity.location());
                    if(entity1 instanceof LivingEntity livingEntity) {

                        int cornerX = guiGraphics.guiWidth() / 2 - 25;

                        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, cornerX, height, cornerX + 50, height + entity.scale() + 30, entity.scale(), 0.25F, mouseX, mouseY, livingEntity);
                        finalHeight.addAndGet(height + entity.scale() + 30);
                    }
                });
            }
        }
    }

    public void addClickBox(ActionBox box) {

        boolean isBoxAlreadyIn = this.actionBoxes.stream().anyMatch((b) -> b.id().equals(box.id()));

        if(!isBoxAlreadyIn) {
            this.actionBoxes.add(box);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {

        for (ActionBox clickBox : actionBoxes) {

            if (clickBox.isHovered(mouseX,mouseY, 0)) {
                clickBox.onHover(this);
            }
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ActionBox clickBox : actionBoxes) {
            if (clickBox.isHovered(mouseX,mouseY, this.scrollAmount())) {
                clickBox.onClick(this);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    public void refresh(EntryInfo entryInfo) {
        this.info = entryInfo;
        this.setScrollAmount(0);

    }

}

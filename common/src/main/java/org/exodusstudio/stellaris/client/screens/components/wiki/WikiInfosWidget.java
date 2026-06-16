package org.exodusstudio.stellaris.client.screens.components.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.utils.minedown.StellardownRenderer;
import org.exodusstudio.stellaris.common.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.utils.ActionBox;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WikiInfosWidget extends ScrollableContainer {

    private AtomicInteger finalHeight = new AtomicInteger(0);

    public EntryInfo info;
    private final CopyOnWriteArrayList<ActionBox> actionBoxes = new CopyOnWriteArrayList<>();

    private boolean firstRender = true;

    public WikiInfosWidget(int baseX, int baseY, int width, int height, EntryInfo info) {
        this(baseX, baseY, width, height);
        this.info = info;
        this.firstRender = true;
    }

    public WikiInfosWidget(int baseX, int baseY, int width, int height) {
        super(baseX, baseY, width, height, Component.empty());
    }

    @Override
    protected int contentHeight() {
        return this.finalHeight.intValue();
    }

    @Override
    public void renderContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        finalHeight.set(0);

        if(info == null) return;

        for(EntryInfo.InfoComponent component : info.components()) {
            switch (component.type().toLowerCase()) {
                case "text" -> component.text().ifPresent((text) -> {

                    int descriptionHeight = new StellardownRenderer(text, getWidth() - 40, Minecraft.getInstance().font)
                            .render(this.getX() + 5, (int) (this.getOffsetHeight() + finalHeight.get() + 5), guiGraphics, this::addClickBox);
                    finalHeight.addAndGet(descriptionHeight);

//                    int descriptionHeight = new WikiEntryTextRenderer(component.text().get(), getWidth() - 40)
//                            .renderWords(guiGraphics, this.getX() + 5, (int) (this.getOffsetHeight() + finalHeight.get() + 5), mouseX, mouseY, this::addClickBox);
                    //finalHeight.addAndGet(descriptionHeight + 5);
                });
                case "image" -> component.image().ifPresent((image) -> {
                    int height = (int) (this.getOffsetHeight() + finalHeight.get() + 20);
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, image.formatFileLocation(), this.getX() + this.getWidth() / 2 - image.width() / 2, height, 0, 0, image.width(), image.height(), image.width(), image.height());
                    if (image.legend().isPresent()) {
                        guiGraphics.centeredText(Minecraft.getInstance().font, image.legend().get(), this.getX() + this.getWidth() / 2, height + image.height() + 5, 0xFFFFFFFF);
                        int legendHeight = Minecraft.getInstance().font.lineHeight + 5;
                        finalHeight.addAndGet(legendHeight);
                    }
                    finalHeight.addAndGet(image.height() + 20);
                });
                case "item" -> component.item().ifPresent((item) -> {

                    if (item.onlyIcon().isEmpty() || !item.onlyIcon().get()) {
                        Matrix3x2fStack matrixStack = guiGraphics.pose();
                        matrixStack.pushMatrix();

                        float scale = item.scale().orElse(1f);
                        int itemSize = 16;
                        int padding = 8;

                        float centerX = this.getX() + this.getWidth() / 2f;
                        int yPos = (int) (this.getOffsetHeight() + finalHeight.get() + padding);

                        float tx = centerX - (itemSize * scale) / 2f;

                        matrixStack.translate(tx, yPos);
                        matrixStack.scale(scale, scale);

                        guiGraphics.item(item.stack().create(), 0, 0);

                        finalHeight.addAndGet(Math.round(itemSize * scale) + (padding * 2));

                        matrixStack.popMatrix();
                    }
                });
                case "entity" -> component.entity().ifPresent((entity) -> {
                    int height = (int) (this.getOffsetHeight() + finalHeight.get() + entity.scale());
                    Entity entity1 = ClientUtils.createEntity(Minecraft.getInstance().level, entity.location());
                    if(entity1 instanceof LivingEntity livingEntity) {
                        int cornerX = guiGraphics.guiWidth() / 2 - 25;

                        InventoryScreen.extractEntityInInventoryFollowsMouse(guiGraphics, cornerX, height, cornerX + 50, height + entity.scale() + 30, entity.scale(), 0.25F, mouseX, mouseY, livingEntity);
                        finalHeight.addAndGet(height + entity.scale() + 30);
                    }
                });
            }
        }

        finalHeight.addAndGet(10); // Extra padding at the bottom

        firstRender = false;
    }

    public void addClickBox(ActionBox box) {

        if(this.firstRender) {
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
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        for (ActionBox clickBox : actionBoxes) {
            if (clickBox.isHovered(event.x(), event.y(), this.scrollAmount())) {
                clickBox.onClick(this);
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    public void refresh(EntryInfo entryInfo) {
        this.info = entryInfo;
        this.setScrollAmount(0);
        actionBoxes.clear();
        this.firstRender = true;

    }
}

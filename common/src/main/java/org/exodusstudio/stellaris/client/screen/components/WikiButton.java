package org.exodusstudio.stellaris.client.screen.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.data.wiki.WikiEntry;
import org.exodusstudio.stellaris.client.utils.ClientUtils;

public class WikiButton extends TexturedButton{

    private final WikiEntry.EntryInfo info;


    public WikiButton(int x, int y, int widthIn, int heightIn, OnPress onPressIn, WikiEntry.EntryInfo info) {
        super(x, y, widthIn, heightIn, onPressIn);
        this.info = info;
        setTooltip();
    }


    public void setTooltip() {
        switch (info.iconType()) {
            case "item":
                info.components().stream().filter((c) -> c.type().equals("item")).toList().getFirst();
                break;
            case "entity":
                info.components().stream().filter((c) -> c.type().equals("entity")).findFirst().ifPresent((entity) -> {
                    Entity entity1 = ClientUtils.createEntity(Minecraft.getInstance().level, entity.entity().get().entity());
                    this.tooltip(Tooltip.create(entity1.getDisplayName()));
                });
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        int i = this.yTexStart;
        if (this.isHoveredOrFocused()) {
            i += this.yDiffText;
        }

        /** TEXTURE MANAGER */
        ResourceLocation texture = this.getTypeTexture();

        /** TEXTURE RENDERER */

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), (float) this.xTexStart, (float) i,
                this.width, this.height, this.textureWidth, this.textureHeight);

        /** FONT RENDERER */
        switch (info.iconType()) {
            case "item":
                info.components().stream().filter((c) -> c.type().equals("item")).findFirst().ifPresent((item) -> graphics.renderItem(item.item().get().stack(), this.getX(), this.getY()));;
                break;
            case "entity":
                info.components().stream().filter((c) -> c.type().equals("entity")).findFirst().ifPresent((entity) -> {
                    Entity entity1 = ClientUtils.createEntity(Minecraft.getInstance().level, entity.entity().get().entity());
                    if(entity1 instanceof LivingEntity livingEntity) {
                        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, this.getX() + 2, this.getY(), this.getX() + 18, this.getY() + 16, 8, 0.25F, mouseX, mouseY, livingEntity);
                    }
                });
        }
    }

    @Override
    public void onPress() {
        super.onPress();
    }
}

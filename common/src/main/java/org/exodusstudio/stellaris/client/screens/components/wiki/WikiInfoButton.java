package org.exodusstudio.stellaris.client.screens.components.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.client.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.utils.ClientUtils;

public class WikiInfoButton extends TexturedButton {

    private final EntryInfo info;


    public WikiInfoButton(int x, int y, int widthIn, int heightIn, OnPress onPressIn, EntryInfo info) {
        super(x, y, widthIn, heightIn, onPressIn);
        this.info = info;
        setTooltip();
    }


    public void setTooltip() {
        Component title = Component.literal(info.title());
        switch (info.iconType()) {
            case "item":
                info.components().stream().filter((c) -> c.type().equals("item")).findFirst().ifPresentOrElse(c -> {
                    this.tooltip(Tooltip.create(c.item().get().stack().getHoverName()));
                }, () -> this.tooltip(Tooltip.create(title)));
                break;
            case "entity":
                info.components().stream().filter((c) -> c.type().equals("entity")).findFirst().ifPresent((entity) -> {
                    Entity entity1 = ClientUtils.createEntity(Minecraft.getInstance().level, entity.entity().get().location());
                    this.tooltip(Tooltip.create(entity1.getDisplayName()));
                });
            default:
                this.tooltip(Tooltip.create(title));
        }
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int i = this.yTexStart;
        if (this.isHoveredOrFocused()) {
            i += this.yDiffText;
        }

        /** TEXTURE MANAGER */
        Identifier texture = this.getTypeTexture();

        /** TEXTURE RENDERER */

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), (float) this.xTexStart, (float) i,
                this.width, this.height, this.textureWidth, this.textureHeight);

        /** FONT RENDERER */
        switch (info.iconType()) {
            case "item":
                info.components().stream().filter((c) -> c.type().equals("item")).findFirst().ifPresent((item) -> graphics.renderItem(item.item().get().stack(), this.getX() + 2, this.getY() + 2));
                break;
            case "entity":
                info.components().stream().filter((c) -> c.type().equals("entity")).findFirst().ifPresent((entity) -> {
                    Entity entity1 = ClientUtils.createEntity(Minecraft.getInstance().level, entity.entity().get().location());
                    if(entity1 instanceof LivingEntity livingEntity) {
                        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, this.getX() + 2, this.getY(), this.getX() + 18, this.getY() + 16, 8, 0.25F, mouseX, mouseY, livingEntity);
                    }
                });
        }
    }
}

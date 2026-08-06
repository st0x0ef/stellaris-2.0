package org.exodusstudio.stellaris.client.screens.components.wiki;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.client.markdown.MarkdownPage;
import org.exodusstudio.stellaris.client.utils.stellardown.StellardownParser;
import org.exodusstudio.stellaris.common.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.joml.Vector3f;

public class WikiInfoButton extends TexturedButton {

    private final MarkdownPage page;


    public WikiInfoButton(int x, int y, int widthIn, int heightIn, OnPress onPressIn, MarkdownPage page) {
        super(x, y, widthIn, heightIn, onPressIn);
        this.page = page;
        setTooltip();
    }


    public void setTooltip() {
        Component title = Component.literal(page.title);
        switch (page.iconType) {
            case MarkdownPage.IconType.ITEM:

                StellardownParser.ItemStyle itemStyle = page.getItemIcon();
                if(itemStyle != null) {
                    BuiltInRegistries.ITEM.get(itemStyle.identifier()).ifPresent((item) -> {
                        this.tooltip(Tooltip.create(new ItemStack(item).getHoverName()));
                    });
                    return;
                }
                break;
            case MarkdownPage.IconType.ENTITY:

                StellardownParser.EntityStyle entityStyle = page.getEntityIcon();
                if(entityStyle != null) {

                    Entity entity = ClientUtils.createEntity(Minecraft.getInstance().level, entityStyle.identifier());
                    this.tooltip(Tooltip.create(entity.getDisplayName()));
                    return;
                }
        }
        this.tooltip(Tooltip.create(title));

    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
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

        switch (page.iconType) {
            case MarkdownPage.IconType.ITEM:

                StellardownParser.ItemStyle itemStyle = page.getItemIcon();
                if(itemStyle != null) {
                    BuiltInRegistries.ITEM.get(itemStyle.identifier()).ifPresent((item) -> {
                        graphics.item(new ItemStack(item), this.getX() + 2, this.getY() + 2);
                    });
                }
                break;
            case MarkdownPage.IconType.ENTITY:

                StellardownParser.EntityStyle entityStyle = page.getEntityIcon();
                if(entityStyle != null) {
                    Entity entity = ClientUtils.createEntity(Minecraft.getInstance().level, entityStyle.identifier());
                    if(entity instanceof LivingEntity livingEntity) {
                        ClientUtils.renderEntityInGui(graphics, this.getX() + 2, this.getY(), this.getX() + 18, this.getY() + 16, 8, 0.25F, mouseX, mouseY, livingEntity, entityStyle.rotation());
                    }
                }
        }
    }
}

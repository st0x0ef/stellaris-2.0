package org.exodusstudio.stellaris.client.screens.laboratory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.menus.laboratory.VaccineMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class VaccineScreen extends AbstractContainerScreen<VaccineMenu> {
    private static final Identifier GUI_LOCATION = IdentifierUtils.guiTexture("laboratory_vaccine");
    public static final Component TAB_NAME = Component.literal("Vaccine");

    public VaccineScreen(VaccineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, TAB_NAME);
        this.imageWidth = 180;
        this.imageHeight = 188;

        this.titleLabelX = (180 - Minecraft.getInstance().font.width(TAB_NAME)) / 2;
        this.titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        TexturedButton researchButton = new TexturedButton(this.leftPos + this.imageWidth, this.topPos + 50, 16,16,
                button -> menu.openResearchMenu())
                .tex(GUISprites.RESEARCH_TAB, GUISprites.RESEARCH_TAB_HOVER)
                .tooltip(Tooltip.create(ResearchScreen.TAB_NAME))
                .useSprite(true);

        TexturedButton vaccineButton = new TexturedButton(this.leftPos + this.imageWidth, this.topPos + 66, 16,16,null)
                .tex(GUISprites.VACCINE_TAB, GUISprites.VACCINE_TAB_HOVER)
                .tooltip(Tooltip.create(VaccineScreen.TAB_NAME))
                .useSprite(true)
                .setUVs(2, 0);

        this.addRenderableWidget(researchButton);
        this.addRenderableWidget(vaccineButton);

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, TAB_NAME, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}

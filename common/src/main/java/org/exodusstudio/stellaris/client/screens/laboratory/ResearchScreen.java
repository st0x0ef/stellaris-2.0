package org.exodusstudio.stellaris.client.screens.laboratory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.menus.laboratory.ResearchMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public class ResearchScreen extends AbstractContainerScreen<ResearchMenu> {
    private static final Identifier GUI_LOCATION = IdentifierUtils.guiTexture("laboratory_research"); //temporary
    public static final Component TAB_NAME = Component.literal("Research");

    private boolean should_display_success_message;
    private boolean research_success;

    private static final Component SUCCESS_MESSAGE = Component.translatable("message.stellaris.success");
    private static final Component FAILURE_MESSAGE = Component.translatable("message.stellaris.failure");

    private TexturedButton startResearchButton;

    public ResearchScreen(ResearchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, TAB_NAME, 180, 188);

        this.titleLabelX = (180 - Minecraft.getInstance().font.width(TAB_NAME)) / 2;
        this.titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        TexturedButton researchButton = new TexturedButton(this.leftPos + this.imageWidth, this.topPos + 40, 16,16, null)
                .tex(GUISprites.RESEARCH_TAB, GUISprites.RESEARCH_TAB_HOVER)
                .tooltip(Tooltip.create(ResearchScreen.TAB_NAME))
                .useSprite(true)
                .setUVs(2, 0);

        TexturedButton vaccineButton = new TexturedButton(this.leftPos + this.imageWidth, this.topPos + 56, 16,16,
                button -> menu.openVaccineTab())
                .tex(GUISprites.VACCINE_TAB, GUISprites.VACCINE_TAB_HOVER)
                .tooltip(Tooltip.create(VaccineScreen.TAB_NAME))
                .useSprite(true);

        startResearchButton = new TexturedButton(this.leftPos + (this.imageWidth - 96) / 2, this.topPos + 69, 96, 16,
                Component.literal("Start Research"), button -> menu.researchButton())
                .tex(GUISprites.RESEARCH_BUTTON, GUISprites.RESEARCH_BUTTON_HOVER)
                .tooltip(Tooltip.create(Component.literal("Start the research, the more parasite you have, the more chance you have to progress toward the vaccine recipe.")))
                .useSprite(true);

        this.addRenderableWidget(researchButton);
        this.addRenderableWidget(vaccineButton);
        this.addRenderableWidget(startResearchButton);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(graphics, mouseX, mouseY, partialTick);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        if (this.menu.getSlot(0).getItem().isEmpty()) {
            this.startResearchButton.active = false;
            this.menu.blockEntity.progressTickLeft = -1;
        } else if (this.menu.blockEntity.progressTickLeft > 0) {
            int u = (Stellaris.CONFIG.parasiteConfig.researchDelay - this.menu.blockEntity.progressTickLeft) * 54 / Stellaris.CONFIG.parasiteConfig.researchDelay;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUISprites.RESEARCH_PROGRESS, this.leftPos + 63, this.topPos + 47, u,  0, u, 2, 54, 2);
            this.startResearchButton.active = false;
            this.should_display_success_message = false;
        } else if(this.menu.blockEntity.progressTickLeft == 0) {
            this.research_success = menu.tryResearch();
            this.should_display_success_message = true;
            this.startResearchButton.active = true;
        } else {
            this.startResearchButton.active = true;
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, TAB_NAME, this.titleLabelX, this.titleLabelY, -11050641, false);

        if (this.should_display_success_message) {
            int color  = this.research_success ? Utils.getMinecraftColor("green") : Utils.getMinecraftColor("red");
            Component message = this.research_success ? SUCCESS_MESSAGE : FAILURE_MESSAGE;
            guiGraphics.text(this.font, message, (this.imageWidth - this.font.width(message)) / 2, 90, color);
        }
    }
}

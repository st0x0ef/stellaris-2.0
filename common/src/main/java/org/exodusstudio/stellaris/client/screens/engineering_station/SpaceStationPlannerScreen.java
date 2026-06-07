package org.exodusstudio.stellaris.client.screens.engineering_station;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.Padding;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.tablet.application.sd.SDCardReaderApplicationScreen;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationData;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.menus.engineering_station.SpaceStationPlannerMenu;
import org.exodusstudio.stellaris.common.network.packets.PlanSpaceStationPacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class SpaceStationPlannerScreen extends AbstractContainerScreen<SpaceStationPlannerMenu> {

    private ScrollableContainer container;
    private SpaceStationRecipe selectedRecipe;
    private TexturedButton buildButton;

    public static final Component TAB_NAME = Component.literal("Space Station Planner");

    public SpaceStationPlannerScreen(SpaceStationPlannerMenu menu, Inventory inventory, net.minecraft.network.chat.Component title) {
        super(menu, inventory, title);
        imageWidth = 310;
        imageHeight = 192;

    }

    @Override
    protected void init() {
        super.init();
        setRecipes();
        EngineUpgraderScreen.addTabsButton(this.leftPos + this.imageWidth, this.topPos + 40, this, menu.engineeringStationPos, "space_station");
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SDCardReaderApplicationScreen.TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        this.buildButton.active = selectedRecipe != null;

    }

    public void setRecipes() {
        this.container = new ScrollableContainer(this.leftPos + 20, this.topPos + 27, 85, 147, Component.empty());
        this.container.setPadding(new Padding(5));

        int i = 0;
        for(SpaceStationRecipe recipe : SpaceStationData.SPACE_STATION_RECIPES) {
            TexturedButton button = new TexturedButton(container.getX() + 5, this.container.getY() + 5 + i * 25, 84, 20, btn -> {
                this.selectedRecipe = recipe;
                this.menu.checkItems(this.selectedRecipe);
            }).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button"))
                    .setText(recipe.getDisplayName());
            this.container.addChild(this, button);
            i++;
        }

        container.setContentHeight(i * 25)
                .setBackground(IdentifierUtils.guiTexture("tablet/tablet_entries_background"));
        this.addRenderableWidget(container);


        this.buildButton = new TexturedButton(this.leftPos + 210, this.topPos + 50, 80, 20, menu.checked ? Component.literal("Build") : Component.literal("Check"), btn -> {
           if(!menu.checked) {
               this.menu.checkItems(selectedRecipe);
           } else {
               NetworkManager.sendToServer(new PlanSpaceStationPacket(selectedRecipe));
           }
        }).tex(IdentifierUtils.guiTexture("tablet/tablet_button"), IdentifierUtils.guiTexture("tablet/tablet_button_hover"));

        this.addRenderableWidget(buildButton);
    }

    public void onCheckChange(boolean check) {
        this.buildButton.setText(check ? Component.literal("Build") : Component.literal("Check"));
    }

}

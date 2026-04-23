package org.exodusstudio.stellaris.client.screens.engineering_station;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.Padding;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationData;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.menus.engineering_station.SpaceStationPlannerMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class SpaceStationPlannerScreen extends AbstractContainerScreen<SpaceStationPlannerMenu> {

    private ScrollableContainer container;

    public SpaceStationPlannerScreen(SpaceStationPlannerMenu menu, Inventory inventory, net.minecraft.network.chat.Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        setRecipes();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

    }

    public void setRecipes() {
        this.container = new ScrollableContainer(this.leftPos + 20, this.topPos + 27, 110, 147, Component.empty());
        this.container.setPadding(new Padding(5));

        int i = 0;
        for(SpaceStationRecipe recipe : SpaceStationData.SPACE_STATION_RECIPES) {
            TexturedButton button = new TexturedButton(container.getX() + 5, this.container.getY() + 5 + i * 25, 95, 20, btn -> {

            }).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button"))
                    .setText(Component.translatable(recipe.structureId().getPath()));
            this.container.addChild(this, button);
            i++;
        }

        container.setContentHeight(i * 25)
                .setBackground(IdentifierUtils.guiTexture("tablet/tablet_entries_background"));
        this.addRenderableWidget(container);
    }

}

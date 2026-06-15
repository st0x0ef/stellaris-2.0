package org.exodusstudio.stellaris.client.screens.engineering_station;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.Padding;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
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
    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("space_station_planner");

    public SpaceStationPlannerScreen(SpaceStationPlannerMenu menu, Inventory inventory, net.minecraft.network.chat.Component title) {
        super(menu, inventory, title, 180, 224);
        this.titleLabelX = (180 - Minecraft.getInstance().font.width(TAB_NAME)) / 2;
        this.titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();
        setRecipes();
        EngineUpgraderScreen.addTabsButton(this.leftPos + this.imageWidth, this.topPos + 40, this, menu.engineeringStationPos, "space_station");
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
        extractTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        this.buildButton.active = selectedRecipe != null;

    }

    public void setRecipes() {
        this.container = new ScrollableContainer(this.leftPos - 72, this.topPos , 72, 138, Component.empty());
        this.container.setPadding(new Padding(5));

        int i = 0;
        for(SpaceStationRecipe recipe : SpaceStationData.SPACE_STATION_RECIPES) {
            TexturedButton button = new TexturedButton(container.getX() + 5, this.container.getY() + 5 + i * 25, 60, 20, btn -> {
                this.changeSelectRecipe(recipe, btn);
            }).tex(IdentifierUtils.id("util/machine_button"), IdentifierUtils.id("util/machine_button_hover"))
                    .useSprite(true)
                    .setText(recipe.getDisplayName())
                    .setTextPadding(new Padding(2, 0))
                    .tooltip(Tooltip.create(recipe.getTooltip()));
            this.container.addChild(this, button);
            i++;
        }

        container.setContentHeight(i * 25)
                .setBackground(IdentifierUtils.guiTexture("sprites/util/space_station_panel"));
        this.addRenderableWidget(container);


        this.buildButton = new TexturedButton(this.leftPos + 101, this.topPos + 86, 60, 16, menu.checked ? Component.literal("Build") : Component.literal("Check"), btn -> {
           if(!menu.checked) {
               this.menu.checkItems(selectedRecipe);
           } else {
               NetworkManager.sendToServer(new PlanSpaceStationPacket(selectedRecipe));
           }
        })
                .useSprite(true)
                .tex(IdentifierUtils.id("util/machine_button"), IdentifierUtils.id("util/machine_button_hover"));

        this.addRenderableWidget(buildButton);
    }

    public void onCheckChange(boolean check) {
        this.buildButton.setText(check ? Component.literal("Build") : Component.literal("Check"));
    }

    public void changeSelectRecipe(SpaceStationRecipe selectedRecipe, Button button) {
        this.selectedRecipe = selectedRecipe;
        this.menu.checkItems(this.selectedRecipe);

        for(GuiEventListener listener : container.children()) {
            if(listener instanceof TexturedButton tb) {
                if(tb == button) {
                    tb.tex(IdentifierUtils.id("util/machine_button_hover"), IdentifierUtils.id("util/machine_button"));
                } else {
                    tb.tex(IdentifierUtils.id("util/machine_button"), IdentifierUtils.id("util/machine_button_hover"));
                }
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, TAB_NAME, this.titleLabelX, this.titleLabelY, -11050641, false);
    }

}

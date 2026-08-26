package org.exodusstudio.stellaris.client.screens.engineering_station;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.TabbedMachineScreen;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.menus.engineering_station.EngineUpgradeMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenBlockEntityMenusPacket;
import org.exodusstudio.stellaris.common.registries.MenuProviderRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public class EngineUpgraderScreen extends TabbedMachineScreen<EngineUpgradeMenu> {
    private static final Identifier GUI_LOCATION = IdentifierUtils.guiTexture("upgrade_station");
    public static final Component TAB_NAME = Component.literal("Engine Upgrader");

    public static final TabInfo[] TABS =  new TabInfo[]{
            new TabInfo(MenuProviderRegistry.ROCKET_CRAFTING, GUISprites.ROCKET_CRAFTING_TAB, GUISprites.ROCKET_CRAFTING_TAB_HOVER, RocketStationScreen.TAB_NAME),
            new TabInfo(MenuProviderRegistry.ROCKET_UPGRADE, GUISprites.MODULES_TAB, GUISprites.MODULES_TAB_HOVER, TAB_NAME),
            new TabInfo(MenuProviderRegistry.SPACE_STATION_PLANNER, GUISprites.SPACE_STATION_PLANNER_TAB, GUISprites.SPACE_STATION_PLANNER_TAB_HOVER, SpaceStationPlannerScreen.TAB_NAME),
    };

    public EngineUpgraderScreen(EngineUpgradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, TAB_NAME, 180, 188);
    }

    public static void addTabsButton(int x, int y, Screen screen, BlockPos pos, String currentScreen) {

        int i = 0;
        for(TabInfo tab : TABS) {
            TexturedButton tabWidget = new TexturedButton(x, y + i++ * 16, 16,16,
                    Component.empty(), button -> {
                    if (!currentScreen.equals(tab.provider.id())) {
                            EngineUpgradeMenu.openScreen(tab.provider, pos);
                        }
                    })
                    .tex(tab.icon, tab.iconHover)
                    .tooltip(Tooltip.create(tab.tabName))
                    .useSprite(true);
            if(!tab.provider.id().equals(currentScreen)) {
                tabWidget.setUVs(2, 0);
            }
            screen.addRenderableWidget(tabWidget);
        }

    }


    @Override
    protected void init() {
        super.init();

        addTabsButton(getTabsX(), this.topPos + 40, this, menu.engineeringStationPos, "upgrade");

    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(graphics, mouseX, mouseY, partialTick);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        extractTooltip(graphics, mouseX, mouseY);

        EngineUpgradeMenu.Error error = this.menu.getErrorMessage(menu.getInputModule(), menu.getInputStack());
        if(error != EngineUpgradeMenu.Error.NONE) {
            graphics.centeredText(Minecraft.getInstance().font, error.errorMessage, leftPos + backgroundWidth / 2, topPos + 26, Utils.getMinecraftColor("red"));
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_LOCATION, this.leftPos, this.topPos, 0, 0, this.backgroundWidth, this.imageHeight, this.backgroundWidth, this.imageHeight);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, TAB_NAME, this.titleLabelX, this.titleLabelY, -11050641, false);
    }

    public record TabInfo(OpenBlockEntityMenusPacket.BlockEntityMenuProvider provider, Identifier icon, Identifier iconHover, Component tabName) {}
}

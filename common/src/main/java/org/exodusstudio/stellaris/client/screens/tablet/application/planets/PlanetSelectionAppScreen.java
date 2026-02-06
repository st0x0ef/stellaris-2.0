package org.exodusstudio.stellaris.client.screens.tablet.application.planets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.client.screens.components.Padding;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.client.utils.WikiEntryTextRenderer;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public class PlanetSelectionAppScreen extends Screen {

    private ScrollableContainer container;
    private final MainTabletScreen mainTabletScreen;
    private Planet selectedPlanet;

    public PlanetSelectionAppScreen(MainTabletScreen mainTabletScreen) {
        super(Component.literal(""));
        this.mainTabletScreen = mainTabletScreen;
    }

    @Override
    protected void init() {
        super.init();
        setPlanets();
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.container.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public static PlanetSelectionAppScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {

        return new PlanetSelectionAppScreen(menuHolder.mainTabletScreen());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderPlanetInfo(guiGraphics);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ApplicationScreen.BLANCK_BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.mainTabletScreen.getImageWidth(), this.mainTabletScreen.getImageHeight(), this.mainTabletScreen.getImageWidth(),this.mainTabletScreen.getImageHeight());

    }

    /**
     * Sets up the scrollable container with buttons for each planet. Each button, when clicked, sets the selected planet.
     */
    public void setPlanets() {
        this.container = new ScrollableContainer(this.getLeftPos() + 20, this.getTopPos() + 27, 110, 147, Component.empty());
        this.container.setPadding(new Padding(5));

        int i = 0;
        for(Planet planet : PlanetsData.PLANETS) {
            this.container.addChild(this, new Button.Builder(Component.translatable(planet.translationKey()), btn -> {
                this.selectedPlanet = planet;
            }).size(95, 20)
                    .pos(container.getX() + 5, (container.getY() + 5) + i * 25)
                    .build());
            i++;
        }

        container.setContentHeight(20 * 25)
                .setBackground(IdentifierUtils.guiTexture("tablet/tablet_entries_background"));
        this.addRenderableWidget(container);
    }

    /**
     * Renders the information of the selected planet on the right side of the screen. If no planet is selected, it does nothing.
     * @param guiGraphics The GuiGraphics object used for rendering the planet information.
     */
    public void renderPlanetInfo(GuiGraphics guiGraphics) {
        if (this.selectedPlanet != null) {


            //guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ApplicationScreen.BLANCK_BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, this.mainTabletScreen.getImageWidth(), this.mainTabletScreen.getImageHeight(), this.mainTabletScreen.getImageWidth(),this.mainTabletScreen.getImageHeight());

            Component name = Component.translatable(this.selectedPlanet.translationKey());

            int x = this.getLeftPos() + 140;
            int y = this.getTopPos() + 30;

            guiGraphics.drawString(this.font, name, x + 10, y, Utils.getMinecraftColor("white"));
            renderInfo(guiGraphics, x, y + 20);
        }
    }

    public void renderInfo(GuiGraphics guiGraphics, int x, int y) {
        if(this.selectedPlanet == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("----- Planet Info -----").append("[br] ");
        sb.append("Planet: ").append(this.selectedPlanet.translationKey()).append("[br] ");
        sb.append("Dimension: ").append(this.selectedPlanet.dimension().toString()).append("[br] ");
        sb.append("Gravity: ").append(this.selectedPlanet.gravity()).append(" m/s²").append("[br] ");
        sb.append("Has Oxygen: ").append(this.selectedPlanet.hasOxygen() ? "Yes" : "No").append("[br] ");
        sb.append("-----------------------");

        WikiEntryTextRenderer textRenderer =  new WikiEntryTextRenderer(sb.toString(), 300);
        textRenderer.renderWords(guiGraphics, x, y, 0, 0, (s) -> {});
    }

    public int getLeftPos() {
        return this.mainTabletScreen.getLeftPos();
    }

    public int getTopPos() {
        return this.mainTabletScreen.getTopPos();
    }
}

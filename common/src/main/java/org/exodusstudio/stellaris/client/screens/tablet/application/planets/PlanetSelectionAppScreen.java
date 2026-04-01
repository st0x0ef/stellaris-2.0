package org.exodusstudio.stellaris.client.screens.tablet.application.planets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.client.overlays.FadingHolder;
import org.exodusstudio.stellaris.client.screens.components.Padding;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.client.utils.WikiEntryTextRenderer;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.network.packets.TeleportToPlanetPacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public class PlanetSelectionAppScreen extends Screen {

    private ScrollableContainer container;
    private final MainTabletScreen mainTabletScreen;
    private TexturedButton teleportButton;
    private Planet selectedPlanet;
    private final boolean inSpace;

    public PlanetSelectionAppScreen(MainTabletScreen mainTabletScreen) {
        this(mainTabletScreen, mainTabletScreen.player.stellaris$isPlanetMenuOpen());
    }

    public PlanetSelectionAppScreen(MainTabletScreen mainTabletScreen, boolean inSpace) {
        super(Component.empty());
        this.mainTabletScreen = mainTabletScreen;
        this.inSpace = inSpace;

    }

    @Override
    protected void init() {
        super.init();
        setPlanets();

        this.teleportButton = new TexturedButton(this.getLeftPos() + 165, this.container.getBottom() - 20, 100, 20, btn -> {
            if (this.selectedPlanet != null && this.inSpace && this.canTeleportToPlanet()) {
                NetworkManager.sendToServer(new TeleportToPlanetPacket(this.selectedPlanet));
            }
        }).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button")).setText(Component.translatable("application.stellaris.planet_selection.teleport_button"));
        this.teleportButton.visible = this.isTeleportButtonVisible();

        this.addRenderableWidget(this.teleportButton);
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

            TexturedButton button = new TexturedButton(container.getX() + 5, (this.getTopPos() + 5) + i * 25, 95, 20, btn -> this.selectedPlanet = planet).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button")).setText(Component.translatable(planet.translationKey()));
            this.container.addChild(this, button);
            i++;
        }

        container.setContentHeight(i * 25)
                .setBackground(IdentifierUtils.guiTexture("tablet/tablet_entries_background"));
        this.addRenderableWidget(container);
    }

    /**
     * Renders the information of the selected planet on the right side of the screen. If no planet is selected, it does nothing.
     * @param guiGraphics The GuiGraphics object used for rendering the planet information.
     */
    public void renderPlanetInfo(GuiGraphics guiGraphics) {

        this.teleportButton.visible = this.isTeleportButtonVisible();
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

        WikiEntryTextRenderer.Builder builder = new WikiEntryTextRenderer.Builder();

        builder.addText("----- Planet Info -----").breakL();
        builder.addText("Planet:").addText(this.selectedPlanet.translationKey()).breakL();
        builder.addText("Dimension: ").addText(this.selectedPlanet.dimension().toString()).breakL();
        builder.addText("Gravity:").addText(this.selectedPlanet.gravity()).addText("m/s²").breakL();
        builder.addText("Has Oxygen:").conditionColorText(this.selectedPlanet.hasOxygen() ? "Yes" : "No", "green", "red", this.selectedPlanet.hasOxygen()).breakL();
        builder.addText("-----------------------");

        builder.build(300).renderWords(guiGraphics, x, y, 0, 0, (s) -> {});
    }

    public int getLeftPos() {
        return this.mainTabletScreen.getLeftPos();
    }

    public int getTopPos() {
        return this.mainTabletScreen.getTopPos();
    }

    /**
     * The teleport button should only be visible if a planet is selected and the player is in space. This method checks those conditions and returns true if the button should be visible, false otherwise.
     * @return true if the teleport button should be visible, false otherwise.
     */
    public boolean isTeleportButtonVisible() {
        return (this.selectedPlanet != null) && this.inSpace;
    }

    /**
     * Checks if the player can teleport to the selected planet..
     * @return true if the player can teleport to the selected planet, false otherwise.
     */
    public boolean canTeleportToPlanet(){
        //TODO: create the real check for teleportation, this is just a placeholder that always returns true.
        return true;
    }

    @Override
    public void onClose() {
        FadingHolder fadingHolder = mainTabletScreen.player.getDataAttachments(IdentifierUtils.id("player_fade"), FadingHolder.class);

        if(fadingHolder != null && fadingHolder.fadeAmount() == 1.0f) {
            Utils.stopFade(mainTabletScreen.player);
        }
        this.mainTabletScreen.player.stellaris$setPlanetMenuOpen(false, this.mainTabletScreen.player, true);
        super.onClose();
    }
}

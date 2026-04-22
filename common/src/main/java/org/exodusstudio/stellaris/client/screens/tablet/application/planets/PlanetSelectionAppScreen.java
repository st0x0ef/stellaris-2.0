package org.exodusstudio.stellaris.client.screens.tablet.application.planets;

import com.mojang.authlib.GameProfile;
import dev.architectury.networking.NetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.overlays.FadingHolder;
import org.exodusstudio.stellaris.client.screens.components.Padding;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationScreen;
import org.exodusstudio.stellaris.client.utils.WikiEntryTextRenderer;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.menus.PlanetSelectionMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.network.packets.TeleportToPlanetPacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlanetSelectionAppScreen extends AbstractContainerScreen<PlanetSelectionMenu> {

    private ScrollableContainer container;
    private Planet selectedPlanet;
    private final PlanetSelectionMenu selectionMenu;
    private final boolean inSpace;
    private PlanetInfoComponent planetInfoComponent;
    public AntennaSavedData antennaSavedData;

    public PlanetSelectionAppScreen(PlanetSelectionMenu selectionMenu, Inventory playerInventory, Component component) {
        super(selectionMenu, playerInventory, Component.empty());
        this.inSpace = selectionMenu.player.stellaris$isPlanetMenuOpen() || true; //TODO change this
        this.antennaSavedData = selectionMenu.antennaSavedData;
        this.selectionMenu = selectionMenu;
        this.imageHeight = 192;
        this.imageWidth = 310;
        this.inventoryLabelY = -this.imageHeight;
        this.titleLabelY = -this.imageHeight;

    }

    @Override
    protected void init() {
        super.init();
        setPlanets();
        this.planetInfoComponent = new PlanetInfoComponent(this.getLeftPos() + 135, this.getTopPos() + 27, 153, 147, this);
        this.addRenderableWidget(planetInfoComponent);

    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.container.isMouseOver(mouseX, mouseY)) {
            this.container.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        }else if(this.planetInfoComponent.isMouseOver(mouseX, mouseY)) {
            this.planetInfoComponent.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

    }

    public static PlanetSelectionAppScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {
        NetworkManager.sendToServer(new OpenMenuPacket("planet_selection"));
        return null;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if(this.planetInfoComponent != null) {
            this.planetInfoComponent.visible = this.selectedPlanet != null;
        }

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ApplicationScreen.BLANCK_BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

    }

    /**
     * Sets up the scrollable container with buttons for each planet. Each button, when clicked, sets the selected planet.
     */
    public void setPlanets() {
                                                                            //Old 27
        this.container = new ScrollableContainer(this.getLeftPos() + 20, this.getTopPos() + 27, 110, 147, Component.empty());
        this.container.setPadding(new Padding(5));

        int i = 0;
        for(Planet planet : PlanetsData.PLANETS) {
            TexturedButton button = new TexturedButton(container.getX() + 5, this.container.getY() + 5 + i * 25, 95, 20, btn -> {
                this.selectedPlanet = planet;
                this.planetInfoComponent.onPlanetChange();
            }).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button")).setText(Component.translatable(planet.translationKey()));
            this.container.addChild(this, button);
            i++;
        }

        container.setContentHeight(i * 25)
                .setBackground(IdentifierUtils.guiTexture("tablet/tablet_entries_background"));
        this.addRenderableWidget(container);
    }

    public int getLeftPos() {
        return this.leftPos;
    }

    public int getTopPos() {
        return this.topPos;
    }

    /**
     * The teleport button should only be visible if a planet is selected and the player is in space. This method checks those conditions and returns true if the button should be visible, false otherwise.
     * @return true if the teleport button should be visible, false otherwise.
     */
    public boolean isTeleportButtonVisible() {
        return (this.selectedPlanet != null)
                && this.inSpace;
    }

    /**
     * Checks if the player can teleport to the selected planet.
     * @return true if the player can teleport to the selected planet, false otherwise.
     */
    public boolean canTeleportToPlanet(){
        //TODO: create the real check for teleportation, this is just a placeholder that always returns true.
        return true;
    }

    @Override
    public void onClose() {

        if(this.selectionMenu.player.stellaris$isPlanetMenuOpen()) return;

        FadingHolder fadingHolder = selectionMenu.player.getDataAttachments(IdentifierUtils.id("player_fade"), FadingHolder.class);

        if(fadingHolder != null && fadingHolder.fadeAmount() == 1.0f) {
            Utils.stopFade(selectionMenu.player);
        }
        this.selectionMenu.player.stellaris$setPlanetMenuOpen(false, this.selectionMenu.player, true);
        super.onClose();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.planetInfoComponent.mouseMoved(mouseX, mouseY);

        super.mouseMoved(mouseX, mouseY);
    }


        public static class PlanetInfoComponent extends ScrollableContainer {

        public Planet planet;
        public AntennaSavedData antennas;
        public PlanetSelectionAppScreen selectionAppScreen;
        public TexturedButton teleportButton;
        public List<AbstractWidget> antennaWidgets = new ArrayList<>();

        public int customContentHeight;

        public PlanetInfoComponent(int x, int y, int width, int height, PlanetSelectionAppScreen selectionAppScreen) {
            super(x, y, width, height, Component.empty());
            this.selectionAppScreen =selectionAppScreen;
            this.antennas = selectionAppScreen.antennaSavedData;
            setWidget();
        }

        public void setWidget() {
            this.customContentHeight = 0;
            int antennasHeight = setupAntennas();
            customContentHeight += antennasHeight ;


            this.teleportButton = new TexturedButton(this.getX(),this.getY() + customContentHeight, 100, 20, btn -> {
                if (this.selectionAppScreen.selectedPlanet != null
                        && this.selectionAppScreen.inSpace
                        && this.selectionAppScreen.canTeleportToPlanet()) {
                    NetworkManager.sendToServer(new TeleportToPlanetPacket(this.selectionAppScreen.selectedPlanet));
                }
            }).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button")).setText(Component.translatable("application.stellaris.planet_selection.teleport_button"));
            this.teleportButton.visible = this.selectionAppScreen.isTeleportButtonVisible();
            this.addChild(this.selectionAppScreen, this.teleportButton);

        }

        private int setupAntennas() {
            if(this.selectionAppScreen.selectedPlanet == null) return 0;

            Font font = Minecraft.getInstance().font;

            StringWidget stringWidget = new StringWidget(200, font.lineHeight, Component.literal("Available Antenna"), Minecraft.getInstance().font);
            stringWidget.setPosition(this.getX(), this.getY() + 20 + 6 * font.lineHeight);
            this.addChild(this.selectionAppScreen, stringWidget);

            List<Antenna> availableAntennas = this.antennas.getAvailableAntennaPerLevel(this.selectionAppScreen.selectionMenu.player.getGameProfile().id(),  ResourceKey.create(Registries.DIMENSION, this.selectionAppScreen.selectedPlanet.dimension()));
            int i = 1;
            for(Antenna antenna : availableAntennas) {

                var nameWidget = new StringWidget(this.getX(), stringWidget.getY() + 7 + i++ * font.lineHeight,200, font.lineHeight, Component.literal(antenna.name), Minecraft.getInstance().font);
                addAntennaWidget(nameWidget);
                addAntennaWidget(new TexturedButton(this.getRight() - font.lineHeight * 2 - 6, nameWidget.getY(), font.lineHeight * 2, font.lineHeight * 2, btn -> {}));
                var componentTest = Component.literal("Owned by : " );
                addAntennaWidget(new StringWidget(this.getX(), stringWidget.getY() + 7 + i++ * font.lineHeight,200, font.lineHeight, componentTest.withStyle(ChatFormatting.GRAY), Minecraft.getInstance().font));
                resolveUUIDAsync(antenna.ownerUUID, (opt) -> {
                    if(opt.isPresent()) {
                        componentTest.append(Component.literal(opt.get().name()).withStyle(ChatFormatting.GRAY));
                    } else {
                        componentTest.append( Component.literal("Unknown")).withStyle(ChatFormatting.GRAY);
                    }
                });
                i++;

            }

            return stringWidget.getHeight() + (i * font.lineHeight + 7);
        }

        public void resolveUUIDAsync(UUID uuid, Consumer<Optional<GameProfile>> uuidSupplier) {
                CompletableFuture.supplyAsync(() -> Minecraft.getInstance().services().profileResolver().fetchById(uuid))
                    .whenComplete((optionalGameProfile, throwable) -> {
                        uuidSupplier.accept(optionalGameProfile);
                    });
        }

        public void addAntennaWidget(AbstractWidget antennaWidget) {
            this.antennaWidgets.add(antennaWidget);
            this.addChild(this.selectionAppScreen, antennaWidget);

        }


        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderContent(guiGraphics, mouseX, mouseY, partialTick);

            this.teleportButton.visible = this.selectionAppScreen.isTeleportButtonVisible();

            int infoHeight = renderInfo(guiGraphics, getX(),  getY() - (int) this.scrollAmount());

            setContentHeight(customContentHeight + infoHeight);
        }

        public void onPlanetChange() {
            this.customContentHeight = 0;
            this.antennaWidgets.forEach((w) -> {
                this.selectionAppScreen.removeWidget(w);
                this.removeChild(this.selectionAppScreen, w);
            });
            this.setScrollAmount(0);
            int antennasHeight = setupAntennas();
            customContentHeight += antennasHeight;
        }

        public int renderInfo(GuiGraphics guiGraphics, int x, int y) {
            Planet planet = this.selectionAppScreen.selectedPlanet;

            if(planet == null) return 0;

            Component planetName = Component.translatable(planet.translationKey());

            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable(planet.translationKey()), x + this.getWidth() / 2 - Minecraft.getInstance().font.width(planetName) / 2 , y + 2, Utils.getMinecraftColor("white"));


            WikiEntryTextRenderer.Builder builder = new WikiEntryTextRenderer.Builder();

            builder.addText("----- Planet Info -----").breakL();
            builder.addText("Planet:").addText(planet.translationKey()).breakL();
            builder.addText("Dimension: ").addText(planet.dimension().toString()).breakL();
            builder.addText("Gravity:").addText(planet.gravity()).addText("m/s²").breakL();
            builder.addText("Has Oxygen:").conditionColorText(planet.hasOxygen() ? "Yes" : "No", "green", "red", this.selectionAppScreen.selectedPlanet.hasOxygen()).breakL();
            builder.addText("-----------------------");

            builder.build(300).renderWords(guiGraphics, x, y + 17, 0, 0, (s) -> {});

            return Minecraft.getInstance().font.lineHeight * 6  + 17;
        }

    }
}

package org.exodusstudio.stellaris.client.screens.tablet.application.planets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.overlays.FadingHolder;
import org.exodusstudio.stellaris.client.screens.components.Padding;
import org.exodusstudio.stellaris.client.screens.components.StellarDownWidget;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.components.containers.ScrollableContainer;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.client.screens.tablet.application.TabletAbstractContainer;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.items.modules.rocket.AutopilotModuleItem;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.menus.PlanetSelectionMenu;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.network.packets.SelectPlanetPacket;
import org.exodusstudio.stellaris.common.network.packets.TeleportToPlanetPacket;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.StellarisRegistries;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlanetSelectionAppScreen extends TabletAbstractContainer<PlanetSelectionMenu> {

    private ScrollableContainer container;
  
    private Planet selectedPlanet;
    private final PlanetSelectionMenu selectionMenu;
    private final boolean inSpace;
    private PlanetInfoComponent planetInfoComponent;
    private final boolean isSelectingAutoPilot;
    public AntennaSavedData antennaSavedData;

    public Modules<RocketModule> rocketModules = new Modules<RocketModule>(List.<RocketModule>of());

    public PlanetSelectionAppScreen(PlanetSelectionMenu selectionMenu, Inventory playerInventory, Component component) {
        super(selectionMenu, playerInventory, Component.empty(), 310, 192);
        this.inSpace = selectionMenu.player.stellaris$isPlanetMenuOpen();
        this.antennaSavedData = selectionMenu.antennaSavedData;
        this.selectionMenu = selectionMenu;
        this.isSelectingAutoPilot = !AutopilotModuleItem.findHeldModule(selectionMenu.player).isEmpty();
        this.rocketModules = getRocketModules(playerInventory.player);

    }

    public Modules<RocketModule> getRocketModules(Player player) {
        if(player.getVehicle() instanceof RocketEntity rocket) {
            Stellaris.LOG.error("is rocket");
            rocket.getRocketModules().forEach((rocketModule) -> Stellaris.LOG.error("module name {}", rocketModule.getDisplayName().getString()));

            return rocket.getRocketModules();
        }
        return new Modules<RocketModule>(List.<RocketModule>of());
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

    public static PlanetSelectionAppScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {
        NetworkManager.sendToServer(new OpenMenuPacket("planet_selection"));
        return null;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
        if(this.planetInfoComponent != null) {
            this.planetInfoComponent.visible = this.selectedPlanet != null;
        }

    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TabletAbstractContainer.BLANCK_BACKGROUND, this.getLeftPos(), this.getTopPos(), 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    /**
     * Sets up the scrollable container with buttons for each planet. Each button, when clicked, sets the selected planet.
     */
    public void setPlanets() {
                                                                            //Old 27
        this.container = new ScrollableContainer(this.getLeftPos() + 20, this.getTopPos() + 27, 110, 147, Component.empty());
        this.container.setPadding(new Padding(5));

        int i = 0;

        PlanetsData.PLANETS.sort((p1, p2) -> (int) (p1.screenOrder() - p2.screenOrder()));

        for(Planet planet : PlanetsData.PLANETS) {
            TexturedButton button = new TexturedButton(container.getX() + 5, this.container.getY() + 5 + i * 25, 110, 20, btn -> {
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


    public List<Identifier> getRemainingModulesForPlanet(Planet planet) {

        List<Identifier> modules = new ArrayList<>(planet.modulesRequired()) ;
        this.rocketModules.forEach((rocketModule) -> Stellaris.LOG.error("module name {}", rocketModule.getDisplayName().getString()));

        for(RocketModule installedModules : this.rocketModules) {
            Identifier moduleId = StellarisRegistries.ROCKET_MODULES.getId(installedModules);
            modules.remove(moduleId);

        }
        return modules;

    }

    /**
     * The teleport button should only be visible if a planet is selected and the player is in space. This method checks those conditions and returns true if the button should be visible, false otherwise.
     * @return true if the teleport button should be visible, false otherwise.
     */
    public boolean isTeleportButtonVisible(Planet planet) {

        return this.selectedPlanet != null && this.inSpace && this.canTeleportToPlanet(planet);
    }

    /**
     * The select planet button should only be visible if a planet is selected and the player is not in space. This method checks those conditions and returns true if the button should be visible, false otherwise.
     * @return true if the select planet button should be visible, false otherwise.
     */
    public boolean isSelectPlanetButtonVisible(Planet planet) {
        return this.selectedPlanet != null && !this.inSpace && this.canTeleportToPlanet(planet) && this.isSelectingAutoPilot;
    }

    /**
     * Checks if the player can teleport to the selected planet.
     * @return true if the player can teleport to the selected planet, false otherwise.
     */
    public boolean canTeleportToPlanet(Planet planet){
        //TODO: create the real check for teleportation, this is just a placeholder that always returns true.
        return getRemainingModulesForPlanet(planet).isEmpty();
    }

    @Override
    public boolean canGoBack() {
        return !this.selectionMenu.player.stellaris$isPlanetMenuOpen();
    }

    @Override
    public void onClose() {
        if (this.selectionMenu.player.stellaris$isPlanetMenuOpen()) return;

        FadingHolder fadingHolder = selectionMenu.player.stellaris$getDataAttachments(IdentifierUtils.id("player_fade"), FadingHolder.class);

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


    @Nullable
    public SpaceStationRecipe getSpaceStationFromRocket() {
        if(menu.player.getVehicle() instanceof RocketEntity rocketEntity) {
            return rocketEntity.inventory.getItem(2).get(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get());
        }
        return null;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {}

    public static class PlanetInfoComponent extends ScrollableContainer {

        public AntennaSavedData antennas;
        public PlanetSelectionAppScreen selectionAppScreen;
        public TexturedButton teleportButton;
        private TexturedButton selectPlanetButton;
        public List<AbstractWidget> antennaWidgets = new ArrayList<>();


        public PlanetInfoComponent(int x, int y, int width, int height, PlanetSelectionAppScreen selectionAppScreen) {
            super(x, y, width, height, Component.empty());
            this.selectionAppScreen =selectionAppScreen;
            this.antennas = selectionAppScreen.antennaSavedData;
            setWidget();
        }

        @Override
        public void renderContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderContent(guiGraphics, mouseX, mouseY, partialTick);

            if(this.selectionAppScreen.selectedPlanet != null) {
                Component planetName = Component.translatable(this.selectionAppScreen.selectedPlanet.translationKey());
                guiGraphics.text(Minecraft.getInstance().font, planetName, getX() + this.getWidth() / 2 - Minecraft.getInstance().font.width(planetName) / 2 , getY() + 2 - (int) scrollAmount(), Utils.getMinecraftColor("white"));
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
            for(AbstractWidget widget : this.antennaWidgets) {
                if(widget.isHovered()) {
                    widget.mouseClicked(event, isDoubleClick);
                }
            }
            return super.mouseClicked(event, isDoubleClick);
        }

        public void setWidget() {
            if(this.selectionAppScreen.selectedPlanet == null) return;

            Planet planet = this.selectionAppScreen.selectedPlanet;
            int infoHeight = setupInfoWidget();
            int antennasHeight = setupAntennas(infoHeight);
            int requiredModulesHeight = this.setupRequiredModules(antennasHeight);


            this.teleportButton = new TexturedButton(this.getX(), requiredModulesHeight, 100, 20, btn -> {
                //We don't need to check if we can teleport to the planet because if we don't, the button don't appear
                NetworkManager.sendToServer(new TeleportToPlanetPacket(planet, Optional.empty(), false));
            }).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button")).setText(Component.translatable("application.stellaris.planet_selection.teleport_button"));

            this.selectPlanetButton = new TexturedButton(this.getX(), requiredModulesHeight, 100, 20, btn -> {
                if (this.selectionAppScreen != null) {
                    NetworkManager.sendToServer(new SelectPlanetPacket(planet));
                }
            }).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button")).setText(Component.translatable("application.stellaris.planet_selection.select_button"));

            if(this.selectionAppScreen.isTeleportButtonVisible(planet)) {
                addAntennaWidget(this.teleportButton);
            }
            if(this.selectionAppScreen.isSelectPlanetButtonVisible(planet)) {
                addAntennaWidget(this.selectPlanetButton);
            }

            SpaceStationRecipe spaceStationRecipe = this.selectionAppScreen.getSpaceStationFromRocket();
            if(selectionAppScreen.isTeleportButtonVisible(planet) && spaceStationRecipe != null && selectionAppScreen.selectedPlanet.allowSpaceStation()) {
                int height = setupSpaceStation(this.antennaWidgets.getLast().getBottom() + (selectionAppScreen.isTeleportButtonVisible(planet) ? teleportButton.getHeight() + 5 : 0), spaceStationRecipe);
            }

            var lastWidget = this.antennaWidgets.getLast();
            this.setContentHeight(this.antennaWidgets.getLast().getY());

        }

        private int setupSpaceStation(int y, SpaceStationRecipe spaceStationRecipe) {
            Planet planet = this.selectionAppScreen.selectedPlanet;

            StringWidget title = new StringWidget(this.getX(), y, this.getWidth(), Minecraft.getInstance().font.lineHeight, Component.translatable("stellaris.screen.space_station"), Minecraft.getInstance().font);
            StringWidget description = new StringWidget(this.getX(), y + Minecraft.getInstance().font.lineHeight, getWidth(), Minecraft.getInstance().font.lineHeight, Component.translatable("stellaris.screen.blueprint_detected").withStyle(ChatFormatting.GRAY), Minecraft.getInstance().font);

            addAntennaWidget(title);
            addAntennaWidget(description);

            TexturedButton stationButton = new TexturedButton(this.getX(), description.getY() + description.getHeight() + 2, 100, 20, btn -> {
                if (this.selectionAppScreen.isTeleportButtonVisible(planet)
                        && this.selectionAppScreen.canTeleportToPlanet(planet)) {
                    NetworkManager.sendToServer(new TeleportToPlanetPacket(this.selectionAppScreen.selectedPlanet, Optional.empty(), true));
                }
            }).tex(IdentifierUtils.guiTexture("tablet/tablet_entry_button"), IdentifierUtils.guiTexture("tablet/tablet_entry_button"))
                    .setText(Component.translatable("stellaris.screen.build_space_station"));
            addAntennaWidget(stationButton);
            return this.antennaWidgets.getLast().getBottom();
        }

        private int setupAntennas(int y) {
            if(this.selectionAppScreen.selectedPlanet == null) return 0;

            Font font = Minecraft.getInstance().font;
            Planet selectedPlanetSnapshot = this.selectionAppScreen.selectedPlanet;

            StringWidget stringWidget = new StringWidget(this.getX(), y, 200, font.lineHeight, Component.translatable("stellaris.screen.antenna_available"), Minecraft.getInstance().font);
            addAntennaWidget(stringWidget);

            List<Antenna> availableAntennas = this.antennas.getAvailableAntennaPerLevel(this.selectionAppScreen.selectionMenu.player.getGameProfile().id(),  ResourceKey.create(Registries.DIMENSION, this.selectionAppScreen.selectedPlanet.dimension()));
            int i = 1;
            for(Antenna antenna : availableAntennas) {

                var nameWidget = new StringWidget(this.getX(), stringWidget.getY() + 7 + i++ * font.lineHeight,200, font.lineHeight, Component.literal(antenna.name), Minecraft.getInstance().font);
                addAntennaWidget(nameWidget);
                addAntennaWidget(new TexturedButton(this.getRight() - font.lineHeight * 2 - 6, nameWidget.getY(), font.lineHeight * 2, font.lineHeight * 2, btn -> {
                    if (this.selectionAppScreen.selectedPlanet != null
                            && this.selectionAppScreen.inSpace
                            && this.selectionAppScreen.canTeleportToPlanet(selectedPlanetSnapshot)) {
                        NetworkManager.sendToServer(new TeleportToPlanetPacket(this.selectionAppScreen.selectedPlanet, Optional.of(antenna.blockPos), false));
                    }
                }));
                var ownerWidget = new StringWidget(this.getX(), stringWidget.getY() + 7 + i++ * font.lineHeight,200, font.lineHeight, Component.translatable("stellaris.screen.owned_by_searching").withStyle(ChatFormatting.GRAY), Minecraft.getInstance().font);
                addAntennaWidget(ownerWidget);

                ClientUtils.resolveUUIDAsync(antenna.ownerUUID, (opt) -> {
                    // Ignore stale async results if user changed selected planet meanwhile.
                    if(this.selectionAppScreen.selectedPlanet != selectedPlanetSnapshot) return;

                    var ownerLine = Component.translatable("stellaris.screen.owned_by").withStyle(ChatFormatting.GRAY);
                    opt.ifPresentOrElse(profile -> ownerLine.append(Component.literal(profile.name()).withStyle(ChatFormatting.GRAY)),
                            () -> ownerLine.append(Component.translatable("tooltip.stellaris.unknown").withStyle(ChatFormatting.GRAY)));

                    ownerWidget.setMessage(ownerLine);
                });
                i++;
            }

            if(i == 1) addAntennaWidget(new StringWidget(this.getX(), y + font.lineHeight, 200, font.lineHeight * i++, Component.translatable("stellaris.screen.no_antenna_available").withStyle(ChatFormatting.GRAY), Minecraft.getInstance().font));

            return antennaWidgets.getLast().getBottom() + 5;
        }


        public int setupInfoWidget() {
            Planet planet = this.selectionAppScreen.selectedPlanet;

            if(planet == null) return 0;

            StellarDownWidget.Builder builder = new StellarDownWidget.Builder();

            builder.addText("----- Planet Info -----").breakL();
            builder.addText("Planet:").addTranslatableText(planet.translationKey()).breakL();
            builder.addText("Dimension:").addText(planet.dimension().toString()).breakL();
            builder.addText("Gravity:").addText(planet.gravity()).addText("m/s²").breakL();
            builder.addText("Has Oxygen:").conditionColorText(planet.hasOxygen() ? "Yes" : "No", "green", "red", this.selectionAppScreen.selectedPlanet.hasOxygen()).breakL();
            builder.addText("-----------------------");

            StellarDownWidget widget = builder.build(this.getX(), this.getY() + 20, 300, Minecraft.getInstance().font.lineHeight * 6);
            addAntennaWidget(widget);


            return antennaWidgets.getLast().getBottom()  + 5;
        }

        public int setupRequiredModules(int y) {
            Planet planet = this.selectionAppScreen.selectedPlanet;

            if(planet == null) return antennaWidgets.getLast().getBottom()  + 5;

            List<Identifier> neededModules = this.selectionAppScreen.getRemainingModulesForPlanet(planet);

            if(neededModules.isEmpty()) return antennaWidgets.getLast().getBottom()  + 5;

            StellarDownWidget.Builder builder = new StellarDownWidget.Builder();

            builder.addText("Modules Required :").breakL();

            for(Identifier neededModule : neededModules) {
                RocketModule module = StellarisRegistries.ROCKET_MODULES.get(neededModule);

                if(module != null) {
                    builder.addText("- [color=red]" + module.getDisplayName().getString() + "[color]").breakL();
                } else {
                    builder.addText("- [color=red]" + neededModule + "[color]").breakL();
                }

            }


            StellarDownWidget widget = builder.build(this.getX() - 3, y + 5, 300, Minecraft.getInstance().font.lineHeight * (neededModules.size() + 1));
            addAntennaWidget(widget);

            return antennaWidgets.getLast().getBottom()  + 5;
        }


        public void onPlanetChange() {

            this.antennaWidgets.forEach(w -> {
                this.selectionAppScreen.removeWidget(w);
                this.removeChild(this.selectionAppScreen, w);
            });
            this.setScrollAmount(0);
            this.setWidget();

        }

        //Used to add a children to the container and the custom list of antenna widgets, so that we can easily remove them later when the planet changes.
        public void addAntennaWidget(AbstractWidget antennaWidget) {
            this.antennaWidgets.add(antennaWidget);
            this.addChild(this.selectionAppScreen, antennaWidget);
        }

    }
}

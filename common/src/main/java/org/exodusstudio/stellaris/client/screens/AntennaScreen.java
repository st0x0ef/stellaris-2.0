package org.exodusstudio.stellaris.client.screens;

import com.mojang.authlib.GameProfile;
import dev.architectury.networking.NetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.components.CustomCheckBox;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.blocks.entities.AntennaBlockEntity;
import org.exodusstudio.stellaris.common.menus.AntennaMenu;
import org.exodusstudio.stellaris.common.network.packets.AntennasOperations;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class AntennaScreen extends AbstractContainerScreen<AntennaMenu> {

    private static final Identifier TEXTURE = IdentifierUtils.texture("/gui/antenna");

    private final AntennaBlockEntity blockEntity = getMenu().getBlockEntity();

    private EditBox nameBox;
    private EditBox whitelistBox;
    private CustomCheckBox publicCheckbox;
    private TexturedButton saveButton;

    public Antenna antenna;

    // Cache local des noms de whitelist pour ne jamais faire d'appel bloquant dans render().
    private final Map<UUID, String> whitelistNameCache = new ConcurrentHashMap<>();
    private final Set<UUID> resolvingWhitelist = ConcurrentHashMap.newKeySet();

    public AntennaScreen(AntennaMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 180;
        imageHeight = 188;
        inventoryLabelY = imageHeight - 94;


        if(menu.antennaId != null && menu.antenna !=null) {
            this.antenna = menu.antenna;
        }
    }

    @Override
    protected void init() {
        super.init();

        addWidgets(antenna);
        queueWhitelistNameResolves();
    }

    public void shareLaunchPad() {
        if (this.antenna == null) return;
        onClose();
        ChatScreen screen = new ChatScreen("/stellaris antennas share \"" + this.antenna.name + "\" ", false);
        this.minecraft.setScreen(screen);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        renderTooltip(guiGraphics, mouseX, mouseY);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, IdentifierUtils.guiTexture("tablet/tablet_entries_background"), this.leftPos + this.imageWidth, topPos, 0, 0, 100, 132, 100, 132);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.literal("WhiteListed").withStyle(ChatFormatting.GRAY), this.leftPos + this.imageWidth + 100 / 2, topPos + 7, ARGB.white(1f));

        if(this.antenna == null) return;
        int i = 1;
        for(UUID whitelist : this.antenna.whitelist) {
            queueWhitelistResolve(whitelist);
            String playerName = this.whitelistNameCache.getOrDefault(whitelist, whitelist.toString());

            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("- " +  playerName)
                    .withStyle(ChatFormatting.GRAY), this.leftPos + this.imageWidth + 7, topPos + 12 + i * 9, ARGB.white(1f));

            i++;
        }
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if ((event.key() == GLFW.GLFW_KEY_ESCAPE || event.key() == GLFW.GLFW_KEY_ENTER) && this.nameBox.isFocused()) {
            this.nameBox.setFocused(false);
            return true;

        }

        if ((this.nameBox.isHovered() || this.nameBox.isFocused()) && event.key() == GLFW.GLFW_KEY_E) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
    }

    private void addWidgets(@Nullable Antenna pad) {
        this.nameBox = new EditBox(this.font, this.leftPos + 50, this.topPos + 40, 59, 14, Component.translatable("gui.stellaris.launchpad_creator.name"));
        this.whitelistBox = new EditBox(this.font, this.leftPos + 120, this.topPos + 38, 80, 20, Component.translatable("gui.stellaris.launchpad_creator.name"));

        nameBox.setBordered(false);

        this.publicCheckbox = new CustomCheckBox(this.leftPos + 120, this.topPos + 38, 17, Component.literal(""), this.font, false)
                .setTexture(GUISprites.INDUSTRIAL_CHECKBOX, GUISprites.INDUSTRIAL_CHECKBOX_SELECTED);

        this.saveButton = new TexturedButton(this.leftPos + (this.imageWidth / 2 - 40),  this.inventoryLabelY, 60, 20, Component.literal("Create"), (b) -> onClose())
                .tex(IdentifierUtils.texture("gui/util/buttons/antenna_button"), IdentifierUtils.texture("gui/util/buttons/antenna_button_hovered"))
                .setText(Component.literal("Create"));

        TexturedButton shareButton = new TexturedButton(saveButton.getX() + saveButton.getWidth()  + 5, saveButton.getY(), 20, 20, Component.literal(""), (b) -> shareLaunchPad())
                .tex(IdentifierUtils.texture("gui/util/buttons/share_button"), IdentifierUtils.texture("gui/util/buttons/share_button_hovered"))
                .tooltip(Tooltip.create(Component.literal("Share Launch Pad")));
        this.addRenderableWidget(shareButton);


        if(pad != null) {
            this.nameBox.setValue(pad.name);
            //this.whitelistBox.setValue(String.join(",", pad.whitelist()));
            this.publicCheckbox.setSelected(pad.isPublic);
            this.saveButton.setMessage(Component.literal("Save"));
        }

        this.addRenderableWidget(this.nameBox);
        this.addRenderableWidget(this.saveButton);
        this.addRenderableWidget(this.publicCheckbox);
    }


    private void queueWhitelistNameResolves() {
        if (this.antenna == null) return;
        for (UUID whitelist : this.antenna.whitelist) {
            queueWhitelistResolve(whitelist);
        }
    }

    private void queueWhitelistResolve(UUID whitelist) {
        if (this.whitelistNameCache.containsKey(whitelist) || !this.resolvingWhitelist.add(whitelist)) {
            return;
        }

        CompletableFuture
                .supplyAsync(() -> Minecraft.getInstance().services().profileResolver().fetchById(whitelist))
                .whenComplete((optionalGameProfile, throwable) -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    minecraft.execute(() -> {
                        this.resolvingWhitelist.remove(whitelist);

                        if (throwable == null && optionalGameProfile != null && optionalGameProfile.isPresent()) {
                            GameProfile profile = optionalGameProfile.get();
                            if (profile.name() != null && !profile.name().isEmpty()) {
                                this.whitelistNameCache.put(whitelist, profile.name());
                            }
                        }
                    });
                });
    }

    @Override
    public void onClose() {
        this.saveLaunchPad();
        super.onClose();
    }

    private void saveLaunchPad() {

        if (this.nameBox.getValue().isEmpty() || this.nameBox.getValue().equals(" ")) return;


        UUID uuid = blockEntity.launchPadId;
        if (this.antenna == null) {
            this.antenna = new Antenna(
                    blockEntity.getBlockPos(),
                    blockEntity.getLevel().dimension(),
                    this.nameBox.getValue(),
                    this.publicCheckbox.selected,
                    menu.getPlayer().getGameProfile().id(),
                    List.of()
            );
            Stellaris.LOG.info("creating new antenna named "  + this.nameBox.getValue());
        } else {
            this.antenna = new Antenna(antenna.blockPos, antenna.dimension, this.nameBox.getValue(), this.publicCheckbox.selected, antenna.ownerUUID, List.of());
            NetworkManager.sendToServer(new AntennasOperations(this.antenna, "modify"));

        }

        blockEntity.setAntenna(this.antenna, uuid, uuid == null);
    }
}
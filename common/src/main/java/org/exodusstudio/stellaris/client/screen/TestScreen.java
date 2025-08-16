package org.exodusstudio.stellaris.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.exodusstudio.stellaris.client.screen.components.TexturedButton;
import org.exodusstudio.stellaris.client.screen.components.WikiInfos;
import org.exodusstudio.stellaris.client.screen.components.containers.DraggableContainer;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.awt.*;

import static net.minecraft.world.item.Items.ALLAY_SPAWN_EGG;

public class TestScreen extends Screen {

    public static final ResourceLocation BACKGROUND = ResourceLocationUtils.guiTexture("tablet/tablet_background");


    public TestScreen() {
        super(Component.literal("Test Screen"));
    }

    @Override
    protected void init() {

        Button testButton = Button.builder(Component.literal("Test Button"), button -> {
            System.out.println("Button clicked!");
        }).bounds(20, 20, 100, 20).build();
        WikiInfos wikiInfos = new WikiInfos(20, 60, 200, 100);

        DraggableContainer container = new DraggableContainer(0, 0, 500, 500, testButton, wikiInfos);

        this.addRenderableWidget(wikiInfos);
        this.addRenderableWidget(testButton);
        this.addRenderableWidget(container);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, 162, 250, 162, 250);

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawString(this.font, "Test Screen", this.width / 2 - this.font.width("Test Screen") / 2, this.height / 2 - 10, ARGB.white(1f));
        guiGraphics.renderItem(new ItemStack(ALLAY_SPAWN_EGG), guiGraphics.guiWidth() / 2,  this.height / 2 + 30);

    }
}

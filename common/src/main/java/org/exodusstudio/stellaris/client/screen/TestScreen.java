package org.exodusstudio.stellaris.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.client.screen.components.containers.DraggableContainer;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

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

        DraggableContainer container = new DraggableContainer(0, 0, 500, 500, testButton);

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

        guiGraphics.drawString(this.font, "Test Screen", this.width / 2 - this.font.width("Test Screen") / 2, this.height / 2 - 10, 0xFFFFFF);
    }
}

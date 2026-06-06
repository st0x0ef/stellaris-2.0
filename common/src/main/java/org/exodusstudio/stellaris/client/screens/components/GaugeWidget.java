package org.exodusstudio.stellaris.client.screens.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.exodusstudio.stellaris.client.screens.utils.GUIUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class GaugeWidget extends AbstractWidget {

    protected long capacity;
    protected long amount = 0L;
    protected Identifier sprite;
    protected Identifier overlay_sprite;
    protected final Direction4 DIRECTION;


    public GaugeWidget(int x, int y, int width, int height, Component message, Identifier sprite, @Nullable Identifier overlay_sprite, long capacity, Direction4 direction) {
        super(x, y, width, height, message);
        this.sprite = sprite;
        this.overlay_sprite = overlay_sprite;
        this.capacity = capacity;
        this.DIRECTION = direction;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        switch (DIRECTION) {
            case DOWN_UP -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getHeight()));
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getWidth(), getHeight(), 0, getHeight() - i, getX(), getY() + getHeight() - i, getWidth(), i);
            }
            case UP_DOWN -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getHeight()));
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getWidth(), getHeight(), 0, 0, getX(), getY(), getWidth(), i);
            }
            case LEFT_RIGHT -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getWidth()));
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getWidth(), getHeight(), 0, 0, getX(), getY(), i, getHeight());
            }
            case RIGHT_LEFT -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getWidth()));
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getWidth(), getHeight(), getWidth() - i, 0, getX() + getWidth() - i, getY(), i, getHeight());
            }
        }
        if (this.overlay_sprite != null) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, overlay_sprite, getX(), getY(), width, height);
        }
    }

    public void renderTooltips(GuiGraphics graphics, int mouseX, int mouseY, Font font, Consumer<List<ClientTooltipComponent>> components) {
        renderTooltip(graphics, mouseX, mouseY, font);
    }

    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, Font font) {
        String GaugeComponent = getMessage().getString() + " : " + amount + " / " + this.capacity;
        ClientTooltipComponent capacity;

        if (amount >= this.capacity) {
            capacity = GUIUtils.getMessageComponent(GaugeComponent, "Lime");
        }
        else if (amount <= 0) {
            capacity = GUIUtils.getMessageComponent(GaugeComponent, "Red");
        }
        else {
            capacity = GUIUtils.getMessageComponent(GaugeComponent, "Orange");
        }

        List<ClientTooltipComponent> components1 = new ArrayList<>();
        components1.addFirst(capacity);
        if (mouseX >= this.getX() && mouseX <= this.getX() + width && mouseY >= this.getY() && mouseY <= this.getY() + this.height) {
            graphics.renderTooltip(font, components1, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }

    public void updateAmount(long value) {
        this.amount = Math.clamp(value, 0, capacity);
    }



    public void updateCapacity(long capacity) {
        this.capacity = capacity;
        this.amount = Math.min(this.amount, capacity);
    }

    public void updateSprite(Identifier sprite) {
        this.sprite = sprite;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    protected double getProgress(Long amount, Long capacity) {
        return Mth.clamp((double) amount / (double) capacity, 0.0D, 1.0D);
    }

    public enum Direction4 {
        DOWN_UP,
        UP_DOWN,
        LEFT_RIGHT,
        RIGHT_LEFT
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        return false;
    }
}

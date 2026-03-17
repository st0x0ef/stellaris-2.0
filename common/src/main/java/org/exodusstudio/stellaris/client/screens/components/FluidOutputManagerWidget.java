package org.exodusstudio.stellaris.client.screens.components;

import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.client.screens.components.containers.DraggableContainer;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.FluidOutputManager;
import org.exodusstudio.stellaris.common.network.packets.SyncOutputManager;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.*;

/**
 * This widget is used to manage the fluid outputs of a machine.
 */
public class FluidOutputManagerWidget extends DraggableContainer {

    public FluidOutputManager fluidOutputManager;
    public Screen screen;
    public BlockEntity blockEntity;
    private final HashMap<Direction, Button> directionButtons = new HashMap<>();

    public FluidOutputManagerWidget(int x, int y, int width, int height, FluidOutputManager fluidOutputManager, Screen screen, BlockEntity blockEntity) {
        super(x, y, 100, 132);
        this.fluidOutputManager = fluidOutputManager;
        this.screen = screen;
        this.blockEntity = blockEntity;
    }

    @Override
    public FluidOutputManagerWidget addDefaultChildren() {
        ArrayList<AbstractWidget> newChildren = new ArrayList<>();

        int startY = getY() + 10;

        int i = 0;
        for (Direction direction : Direction.values()) {
            final Direction dir = direction;
            Button.Builder buttonToAdd = new Button.Builder(Component.literal(Direction.values()[i].getName()), button -> {
                //Get the current fluid for this direction and set this direction to the next fluid in the list of storages
                List<Fluid> fluids = this.fluidOutputManager.blockEntity.getFluidsOutput();
                FluidStack currentFluid = this.fluidOutputManager.outputs.get(dir);

                if (currentFluid == null) {
                    this.fluidOutputManager.outputs.put(dir, FluidStack.create(fluids.get(0), 1000));
                } else {
                    int currentIndex = fluids.indexOf(currentFluid.getFluid());
                    if (currentIndex == -1 || currentIndex == fluids.size() - 1) {
                        this.fluidOutputManager.outputs.remove(dir);
                    } else {
                        this.fluidOutputManager.outputs.put(dir, FluidStack.create(fluids.get(currentIndex + 1), 1000));
                    }
                }

                NetworkManager.sendToServer(new SyncOutputManager.C2S(this.blockEntity.getBlockPos(), dir, this.fluidOutputManager.outputs.getOrDefault(dir, FluidStack.empty())));

            });
            if (i < 3) {
                buttonToAdd.bounds(getX() + 10 + 22 * i, startY + 22 * 2, 20, 20);
            } else {
                if (i == 5) i++;
                buttonToAdd.bounds(getX() + 10 + 22, startY + (i - 3) * 22, 20, 20);
            }
            Button button = buttonToAdd.build();
            directionButtons.put(dir, button);
            newChildren.add(button);
            i++;
        }
        newChildren.forEach((b) -> this.screen.addWidget(b));
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, IdentifierUtils.guiTexture("tablet/tablet_entries_background"), getX(), getY(), 0, 0, width, height, 100, 132);

        // Mise à jour dynamique des tooltips à chaque frame
        for (Direction direction : directionButtons.keySet()) {
            Button button = directionButtons.get(direction);
            var fluid = fluidOutputManager.outputs.get(direction);
            MutableComponent basecomponent = Component.literal("Direction: " + direction.toString());
            if (fluid != null) {
                basecomponent
                        .append(Component.literal("\nFluid Output: "))
                        .append(fluid.getName());
            }
            button.setTooltip(Tooltip.create(basecomponent));
            button.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}

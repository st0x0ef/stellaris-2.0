package org.exodusstudio.stellaris.client.screens.components;

import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.client.screens.components.containers.DraggableContainer;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.FluidOutputManager;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.FluidOutputable;
import org.exodusstudio.stellaris.common.network.packets.SyncOutputManager;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Matrix3x2fStack;

import java.util.*;

/**
 * This widget is used to manage the fluid outputs of a machine.
 */
public class FluidOutputManagerWidget extends DraggableContainer {

    public FluidOutputManager fluidOutputManager;
    public Screen screen;
    public BlockEntity blockEntity;
    private final HashMap<Direction, TexturedButton> directionButtons = new HashMap<>();

    public static int BUTTON_WIDTH = 16;

    public FluidOutputManagerWidget(int x, int y, int width, int height, FluidOutputManager fluidOutputManager, Screen screen, BlockEntity blockEntity) {
        super(x, y, 100, 132);
        this.fluidOutputManager = fluidOutputManager;
        this.screen = screen;
        this.blockEntity = blockEntity;
    }

    @Override
    public FluidOutputManagerWidget addDefaultChildren() {
        ArrayList<AbstractWidget> newChildren = new ArrayList<>();

        int startY = getY() + 20;

        int i = 0;
        for (Direction direction : Direction.values()) {
            final Direction dir = direction;

            TexturedButton texturedButton = new TexturedButton(0, 0, BUTTON_WIDTH, BUTTON_WIDTH, Component.literal(Direction.values()[i].getName()),
                    button -> {
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

                    })
                    .tex(GUISprites.OUTPUT_BUTTON, GUISprites.OUTPUT_BUTTON).useSprite(true);

            if (i < 3) {

                texturedButton.setX(getX() + 10 + (BUTTON_WIDTH + 2) * i);
                texturedButton.setY(startY + (BUTTON_WIDTH + 2) * 2);

            } else {
                if (i == 5) i++;

                texturedButton.setX(getX() + 10 + BUTTON_WIDTH + 2);
                texturedButton.setY(startY + (i - 3) * (BUTTON_WIDTH + 2));

            }
            directionButtons.put(dir, texturedButton);
            newChildren.add(texturedButton);
            i++;
        }
        newChildren.forEach((b) -> this.screen.addWidget(b));
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, IdentifierUtils.guiTexture("tablet/tablet_entries_background"), getX(), getY(), 0, 0, width, height, 100, 132);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.literal("Fluid Outputs").withStyle(ChatFormatting.GRAY), getX() + width / 2, getY() + 7, ARGB.white(1f));

        for (Direction direction : directionButtons.keySet()) {
            TexturedButton button = directionButtons.get(direction);
            var fluid = fluidOutputManager.outputs.get(direction);

            String relativeRelation = Utils.getRelativeDirection(this.blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING), direction);

            String content = relativeRelation != null ? relativeRelation : direction.getName();
            MutableComponent basecomponent = Component.literal("Direction: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(Utils.capitalizeFirstLetter(content)).withStyle(ChatFormatting.WHITE));
            if (fluid != null) {
                basecomponent
                        .append(Component.literal("\nFluid Output: ").withStyle(ChatFormatting.GRAY))
                        .append(MutableComponent.create(PlainTextContents.EMPTY).append(fluid.getName()).withStyle(ChatFormatting.WHITE));
            }
            button.setTooltip(Tooltip.create(basecomponent));
            button.setColor(getColor(fluid));
            button.render(guiGraphics, mouseX, mouseY, partialTick);
            renderNeighboredBlock(guiGraphics, button.getX(), button.getY(), direction);
        }
    }

    public int getColor(FluidStack fluid) {
        if(fluid != null &&  this.blockEntity instanceof FluidOutputable outputable) {
            int index = outputable.getFluidsOutput().indexOf(fluid.getFluid());
            String[] colors = StellarisClient.CLIENT_CONFIG.fluidOutputConfig.fluidsColors;

            if (index >= 0 && colors != null && colors.length > 0) {
                return Utils.getMinecraftColor(colors[index % colors.length]);
            }
        }
        return ARGB.white(1f);
    }

    public void renderNeighboredBlock(GuiGraphics guiGraphics, int x, int y, Direction direction) {
        BlockState neighbor = this.blockEntity.getLevel().getBlockState(this.blockEntity.getBlockPos().relative(direction));
        Matrix3x2fStack matrixStack = guiGraphics.pose();
        matrixStack.pushMatrix();

        float tx = x + 8 - (16 * 0.7f) / 2f;

        matrixStack.translate(tx, y + 2);
        matrixStack.scale(0.7f, 0.7f);

        if(!neighbor.is(BlockTags.AIR)) {
            guiGraphics.renderItem(neighbor.getBlock().asItem().getDefaultInstance(), 0, 0);
        }

        matrixStack.popMatrix();
    }
}

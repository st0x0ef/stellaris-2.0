package org.exodusstudio.stellaris.client.overlays;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.items.modules.space_suit.OilFinderModuleItem;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.concurrent.atomic.AtomicBoolean;

public class SpaceSuitOverlay {

    public static final Identifier OXYGEN_TANK = IdentifierUtils.texture("overlay/oxygen_tank");
    public static final Identifier OXYGEN_TANK_FULL = IdentifierUtils.texture("overlay/oxygen_tank_full");


    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player != null && Utils.isLivingInSpaceSuit(player)) {
            Font font = mc.font;
            int yOffset = 5;

            yOffset += tryRenderOxygenOverlay(graphics, player, font, yOffset);
            yOffset += tryRenderOilFinderOverlay(graphics, player, font, yOffset);
        }
    }

    private static int tryRenderOxygenOverlay(GuiGraphics graphics, Player player, Font font, int yOffset) {
        ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);

        if (helmetStack.getItem() instanceof SpaceSuitHelmet helmet && helmet.getOxygenCapacity(helmetStack) > 0) {
            UniversalFluidStorage oxygenStorage = helmet.getFluidTank(helmetStack);

            if (oxygenStorage == null) {
                return 0;
            }

            long oxygen = oxygenStorage.getFluidInTank(0).getAmount();
            long maxOxygen = oxygenStorage.getTankCapacity(0);

            int x = 5;

            int textureWidth = 62;
            int textureHeight = 52;

            /** DRAW OXYGEN TANK */
            graphics.blit(RenderPipelines.GUI_TEXTURED, OXYGEN_TANK, x, yOffset, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

            int filledHeight = (int)Mth.clamp(((float) oxygen / maxOxygen) * textureHeight, 0, textureHeight);
            int yFilledOffset = textureHeight - filledHeight;
            graphics.blit(RenderPipelines.GUI_TEXTURED, OXYGEN_TANK_FULL, x, yOffset + yFilledOffset, 0, yFilledOffset, textureWidth, filledHeight, textureWidth, textureHeight);

            /** OXYGEN AMOUNT TEXT */
            Component text = Component.translatable("fluid.stellaris.oxygen").append(": ").withStyle(ChatFormatting.BLUE).append("§7" + Math.round(((float) oxygen / maxOxygen) * 100) + "%");
            graphics.drawString(font, text, (x + (textureWidth - font.width(text)) / 2), yOffset + textureHeight + 3, 0xFFFFFFFF);

            return textureHeight + 5;
        }

        return 0;
    }

    private static int tryRenderOilFinderOverlay(GuiGraphics graphics, Player player, Font font, int yOffset) {
        ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);

        if (ModuleUtils.hasSpaceSuitModule(helmetStack, SpaceSuitModule.OilFinderModule.class) && helmetStack.getItem() instanceof SpaceSuitHelmet helmet) {
            UniversalEnergyStorage energy = helmet.getEnergy(helmetStack);

            SpaceSuitModule.OilFinderModule oilFinderModule = ModuleUtils.getSpaceSuitModule(helmetStack, SpaceSuitModule.OilFinderModule.class);
            if (oilFinderModule != null) {
                int range = oilFinderModule.getRange();
                int offset = (range - 1) / 2;

                int oilLevel = 0;
                for (int x = -offset; x <= offset; x++) {
                    for (int z = -offset; z <= offset; z++) {
                        oilLevel += player.level().getChunk(player.getOnPos().offset(x * 16, 0, z * 16)).stellaris$getChunkOilLevel();
                    }
                }

                /** Oil amount text */
                Component cantSearchText = Component.translatable("text.stellaris.oil_finder.cant_search").withStyle(ChatFormatting.RED);
                Component noOilText = Component.translatable("text.stellaris.oil_finder.no_oil").withStyle(ChatFormatting.RED);
                Component oilFoundText = Component.translatable("text.stellaris.oil_finder.oil_found").append(": " + oilLevel + "mb").withStyle(ChatFormatting.GREEN);
                Component energyText = Component.translatable("tooltip.item.stellaris.energy", energy.getEnergy(), energy.getMaxEnergy()).withStyle(ChatFormatting.YELLOW);

                int height = 0;

                if (energy.getEnergy() < 1) {
                    graphics.drawString(font, cantSearchText, 5, yOffset, Utils.getMinecraftColor("red"));
                    height += font.lineHeight + 1;
                } else {
                    if (oilLevel > 0) {
                        graphics.drawString(font, oilFoundText, 5, yOffset, Utils.getMinecraftColor("green"));
                        height += font.lineHeight + 1;
                    } else {
                        graphics.drawString(font, noOilText, 5, yOffset, Utils.getMinecraftColor("red"));
                        height += font.lineHeight + 1;
                    }
                }

                graphics.drawString(font, energyText, 5, yOffset + height, Utils.getMinecraftColor("yellow"));
                height += font.lineHeight + 1;

                return height + 5;
            }
        }

        return 0;
    }
}

package org.exodusstudio.stellaris.client.overlay;

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
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public class OxygenTankOverlay {

    public static final Identifier OXYGEN_TANK = IdentifierUtils.texture("overlay/oxygen_tank");
    public static final Identifier OXYGEN_TANK_FULL = IdentifierUtils.texture("overlay/oxygen_tank_full");


    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Player player = Minecraft.getInstance().player;

        if (Utils.isLivingInSpaceSuit(player)) {
            ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);
            Minecraft mc = Minecraft.getInstance();

            if (helmetStack.getItem() instanceof SpaceSuitHelmet helmet && helmet.getOxygenCapacity(helmetStack) > 0) {
                UniversalFluidStorage oxygenStorage = helmet.getFluidTank(helmetStack);

                if (oxygenStorage == null) {
                    return;
                }

                long oxygen = oxygenStorage.getFluidInTank(0).getAmount();
                long maxOxygen = oxygenStorage.getTankCapacity(0);

                int x = 5;
                int y = 5;

                int textureWidth = 62;
                int textureHeight = 52;

                /** DRAW OXYGEN TANK */
                graphics.blit(RenderPipelines.GUI_TEXTURED, OXYGEN_TANK, x, y, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

                int filledHeight = (int)Mth.clamp(((float) oxygen / maxOxygen) * textureHeight, 0, textureHeight);
                int yOffset = textureHeight - filledHeight;
                graphics.blit(RenderPipelines.GUI_TEXTURED, OXYGEN_TANK_FULL, x, y + yOffset, 0, yOffset, textureWidth, filledHeight, textureWidth, textureHeight);

                /** OXYGEN AMOUNT TEXT */
                Font font = mc.font;
                Component text = Component.translatable("fluid.stellaris.oxygen").append(": ").withStyle(ChatFormatting.BLUE).append("§7" + Math.round(((float) oxygen / maxOxygen) * 100) + "%");
                graphics.drawString(font, text, (x + (textureWidth - font.width(text)) / 2), y + textureHeight + 3, 0xFFFFFFFF);
            }

        }

    }
}

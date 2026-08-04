package org.exodusstudio.stellaris.common.items.modules.space_suit;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Vector2i;

import java.util.function.Consumer;

public class OilFinderModuleItem extends Item implements SpaceSuitModule.OilFinderModule {
    private final int range;

    public OilFinderModuleItem(Properties properties, int range) {
        super(properties);
        this.range = range;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_HELMET.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Find oil in a " + this.range + " x " + this.range + " chunks area.").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_space_suit_helmet_module").withColor(Utils.getMinecraftColor("gray")));
    }

    @Override
    public Vector2i renderStackedGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Player player, ItemStack stack, int x, int y) {

        Font font = Minecraft.getInstance().font;

        y = 5;

        if (ModuleUtils.hasSpaceSuitModule(stack, SpaceSuitModule.OilFinderModule.class) && stack.getItem() instanceof SpaceSuitHelmet helmet) {
            UniversalEnergyStorage energy = helmet.getEnergy(stack);

            SpaceSuitModule.OilFinderModule oilFinderModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.OilFinderModule.class);
            if (oilFinderModule != null) {
                int range = oilFinderModule.getRange();
                int offset = (range - 1) / 2;

                int oilLevel = 0;
                for (int posX = -offset; posX <= offset; posX++) {
                    for (int posZ = -offset; posZ <= offset; posZ++) {
                        oilLevel += player.level().getChunk(player.getOnPos().offset(posX * 16, 0, posZ * 16)).stellaris$getChunkOilLevel();
                    }
                }

                /** Oil amount text */
                Component cantSearchText = Component.translatable("text.stellaris.oil_finder.cant_search").withStyle(ChatFormatting.RED);
                Component noOilText = Component.translatable("text.stellaris.oil_finder.no_oil").withStyle(ChatFormatting.RED);
                Component oilFoundText = Component.translatable("text.stellaris.oil_finder.oil_found").append(": " + oilLevel + "mb").withStyle(ChatFormatting.GREEN);
                Component energyText = Component.translatable("tooltip.item.stellaris.energy", energy.getEnergy(), energy.getMaxEnergy()).withStyle(ChatFormatting.YELLOW);

                int height = 0;

                if (energy.getEnergy() < 1) {
                    graphics.text(font, cantSearchText, x, y, Utils.getMinecraftColor("red"));
                    height += font.lineHeight + 1;
                } else {
                    if (oilLevel > 0) {
                        graphics.text(font, oilFoundText, x, y, Utils.getMinecraftColor("green"));
                    } else {
                        graphics.text(font, noOilText, x, y, Utils.getMinecraftColor("red"));
                    }
                    height += font.lineHeight + 1;
                }

                graphics.text(font, energyText, x, y + height, Utils.getMinecraftColor("yellow"));
                height += font.lineHeight + 1;

                return new Vector2i(font.width(oilLevel > 0 ? oilFoundText : (energy.getEnergy() < 1 ? cantSearchText : noOilText)), height);
            }
        }

        return OilFinderModule.super.renderStackedGui(graphics, deltaTracker, player, stack, x, y);
    }
}

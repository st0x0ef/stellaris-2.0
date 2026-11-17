package org.exodusstudio.stellaris.common.items.modules.space_suit;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.components.GeigerCounterComponent;
import org.exodusstudio.stellaris.common.items.GeigerCounterItem;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.RadiationUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;
import java.util.function.Consumer;

public class GeigerCounterModuleItem extends Item implements SpaceSuitModule.GeigerCounterModule, SpaceSuitModule.TickableModule {
    private final int range;
    private  float radiationDetected = 0f;

    private int lastRadiationCheckedTick = 0;
    public static int RADIATION_CHECK_INTERVAL = 20; // Check radiation every 20 ticks (1 second)


    public GeigerCounterModuleItem(Item.Properties properties, int range) {
        super(properties);
        this.range = range;
    }

    @Override
    public SpaceSuitModule.SpaceSuitFeature getSpaceSuitFeature() {
        return SpaceSuitModule.SpaceSuitFeature.RADIATION_FINDER;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.getItem() instanceof SpaceSuitHelmet;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.radiation_finder_module.range", this.range, this.range).withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_space_suit_helmet_module").withColor(Utils.getMinecraftColor("gray")));
    }

    @Override
    public Vector2i renderStackedGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Player player, ItemStack stack, int x, int y) {

        Font font = Minecraft.getInstance().font;

        y = 5;

        if (ModuleUtils.hasSpaceSuitModule(stack, SpaceSuitModule.GeigerCounterModule.class) && stack.getItem() instanceof SpaceSuitHelmet helmet) {
            UniversalEnergyStorage energy = helmet.getEnergy(stack);

            SpaceSuitModule.GeigerCounterModule geigerCounterModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.GeigerCounterModule.class);
            if (geigerCounterModule != null) {
                int range = geigerCounterModule.getRange();
                int offset = (range - 1) / 2;



                /** Oil amount text */
                Component cantSearchText = Component.translatable("text.stellaris.geiger_counter.cant_search").withStyle(ChatFormatting.RED);
                Component radiationFoundText = Component.translatable("text.stellaris.geiger_counter.avg_radiation.result", range, radiationDetected).withStyle(ChatFormatting.GREEN);
                Component energyText = Component.translatable("tooltip.item.stellaris.energy", energy.getEnergy(), energy.getMaxEnergy()).withStyle(ChatFormatting.YELLOW);

                int height = 0;

                if (energy.getEnergy() < 1) {
                    graphics.text(font, cantSearchText, x, y, Utils.getMinecraftColor("red"));
                    height += font.lineHeight + 1;
                } else {
                    graphics.text(font, radiationFoundText, x, y, Utils.getMinecraftColor("green"));

                    height += font.lineHeight + 1;
                }

                graphics.text(font, energyText, x, y + height, Utils.getMinecraftColor("yellow"));
                height += font.lineHeight + 1;

                return new Vector2i(font.width(energy.getEnergy() < 1 ? cantSearchText : radiationFoundText), height);
            }
        }

        return SpaceSuitModule.GeigerCounterModule.super.renderStackedGui(graphics, deltaTracker, player, stack, x, y);
    }

    public void setRadiationDetected(float radiationDetected) {
        this.radiationDetected = radiationDetected;
    }

    @Override
    public void tick(ItemStack itemStack, ServerPlayer serverPlayer, @Nullable EquipmentSlot equipmentSlot) {
        this.lastRadiationCheckedTick++;
        if(this.lastRadiationCheckedTick % RADIATION_CHECK_INTERVAL == 0) {
            this.lastRadiationCheckedTick = 0;

            ServerLevel serverLevel = serverPlayer.level();
            List<Pair<BlockPos, Float>> radioactiveBlocks = RadiationUtils.getRadioactiveBlocksInRadius(serverLevel, serverPlayer.blockPosition(), this.range);
            float avgRadiation = GeigerCounterComponent.Type.AVG_RADIATION.calculate(radioactiveBlocks);

            if(avgRadiation > 40f) {

                float randomPitch = 0.9f + (float) (Math.random() * 0.2f);
                float randomVolume = 0.3f + (float) (Math.random() * 0.2f);
                serverLevel.playSound(null, serverPlayer.blockPosition(), GeigerCounterItem.getGeigerSound(avgRadiation), SoundSource.PLAYERS, randomVolume, randomPitch);
            }

            this.setRadiationDetected(Math.round(avgRadiation));
        }
    }
}

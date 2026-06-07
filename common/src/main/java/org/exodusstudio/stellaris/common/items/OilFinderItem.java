package org.exodusstudio.stellaris.common.items;

import com.fej1fun.potentials.energy.ItemEnergyStorage;
import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.providers.EnergyProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.oil.OilUtils;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class OilFinderItem extends Item implements EnergyProvider.ITEM {

    public OilFinderItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) {
            return InteractionResult.FAIL;
        }

        ItemEnergyStorage energy = getEnergy(player.getItemInHand(usedHand));
        if (energy.getEnergy() < 1)
            return InteractionResult.FAIL;

        int oilLevel = level.getChunk(player.getOnPos()).stellaris$getChunkOilLevel();

        MutableComponent component = Component.literal("Found Oil " + oilLevel + "mb");
        if (oilLevel == 0) {
            component = Component.literal("No oil found");
        }
        component.withColor(OilUtils.getOilLevelColor(oilLevel));

        player.getItemInHand(usedHand).hurtAndBreak(2, player, EquipmentSlot.MAINHAND);
        energy.extract(1, false);

        player.sendOverlayMessage(component);

        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.oil_finder").withStyle(ChatFormatting.GRAY));
        UniversalEnergyStorage energy = getEnergy(stack);
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.energy", energy.getEnergy(), energy.getMaxEnergy()));
    }

    @Override
    public @NotNull ItemEnergyStorage getEnergy(@NotNull ItemStack stack) {
        return new ItemEnergyStorage(stack, DataComponentsRegistry.ENERGY.get(), 1000, 20, 1);
    }
}

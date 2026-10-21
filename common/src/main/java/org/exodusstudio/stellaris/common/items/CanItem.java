package org.exodusstudio.stellaris.common.items;

import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CanItem extends Item {

    private final int maxNutrition;

    public CanItem(Properties properties, int maxNutrition) {
        super(properties);
        this.maxNutrition = maxNutrition;

    }

    public static void setFoodProperties(ItemStack stack, FoodProperties foodProperties) {
        if (foodProperties != null && (foodProperties.nutrition() > 0 || foodProperties.saturation() > 0)) {
            stack.set(DataComponents.FOOD, foodProperties);
            stack.set(DataComponents.CONSUMABLE, Consumable.builder().build());
        } else {
            stack.remove(DataComponents.FOOD);
            stack.remove(DataComponents.CONSUMABLE);
        }
    }

    public static FoodProperties getFoodProperties(ItemStack stack) {
        return stack.get(DataComponents.FOOD);
    }

    public static int getNutrition(ItemStack stack) {
        FoodProperties properties = getFoodProperties(stack);
        return properties != null ? properties.nutrition() : 0;
    }

    public static float getSaturation(ItemStack stack) {
        FoodProperties properties = getFoodProperties(stack);
        return properties != null ? properties.saturation() : 0;
    }

    public static boolean addFoodToCan(ItemStack canStack, ItemStack foodStack) {
        int canNutrition = getNutrition(canStack) + getNutrition(foodStack);
        if (canNutrition <= ((CanItem) canStack.getItem()).getMaxNutrition()) {
            setFoodProperties(canStack, new FoodProperties(canNutrition, Math.round((getSaturation(canStack) + getSaturation(foodStack)) * 10F) / 10F, false));
            return true;
        }
        return false;
    }

    public int getMaxNutrition() {
        return maxNutrition;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        FoodProperties properties = getFoodProperties(itemStack);

        if (properties == null || (properties.nutrition() <= 0 && properties.saturation() <= 0)) {
            consumer.accept(Component.translatable("tooltip.item.stellaris.can.empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        if (properties.nutrition() > 0) {
            consumer.accept(Component.translatable("tooltip.item.stellaris.can.nutrition", properties.nutrition(), getMaxNutrition()).withStyle(ChatFormatting.GRAY));
        }

        if (properties.saturation() > 0) {
            consumer.accept(Component.translatable("tooltip.item.stellaris.can.saturation", properties.saturation()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack emptyCanStack = new ItemStack(stack.getItem());
        super.finishUsingItem(stack, level, entity);

        useDrink(level, entity);

        if (entity instanceof Player player && !player.hasInfiniteMaterials()) {
            if (stack.isEmpty()) {
                return emptyCanStack;
            }

            if (!player.getInventory().add(emptyCanStack)) {
                player.drop(emptyCanStack, false);
            }
        }
        return stack;
    }

    public static boolean hasDrink(LivingEntity entity) {
        return entity.getMainHandItem().is(Items.POTION) || entity.getOffhandItem().is(Items.POTION) || (entity instanceof Player player && findWaterCellSlot(player) != -1);
    }

    private static int findWaterCellSlot(Player player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (hasWaterForCan(inventory.getItem(slot))) {
                return slot;
            }
        }
        return -1;
    }

    private static boolean hasWaterForCan(ItemStack stack) {
        if (!(stack.getItem() instanceof FluidCellItem)) {
            return false;
        }

        FluidStack fluid = FluidUtil.readStoredFluid(stack, DataComponentsRegistry.FLUID_LIST.get(), 0);
        return fluid.getFluid().isSame(Fluids.WATER) && fluid.getAmount() >= Stellaris.CONFIG.foodConfig.fluidCellWaterPerCan;
    }

    private static void useDrink(Level level, LivingEntity entity) {
        if (!entity.getMainHandItem().is(Items.POTION) && !entity.getOffhandItem().is(Items.POTION)) {
            if (entity instanceof Player player && !player.hasInfiniteMaterials()) {
                int slot = findWaterCellSlot(player);
                if (slot != -1) {
                    drinkFromCell(player, slot);
                }
            }
            return;
        }

        InteractionHand hand = entity.getMainHandItem().is(Items.POTION) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack potionStack = entity.getItemInHand(hand);
        potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).onConsume(level, entity, potionStack, null);
        if (entity instanceof Player player)
            entity.setItemInHand(hand, ItemUtils.createFilledResult(potionStack, player, new ItemStack(Items.GLASS_BOTTLE)));
    }

    private static void drinkFromCell(Player player, int slot) {
        Inventory inventory = player.getInventory();
        ItemStack cellStack = inventory.getItem(slot);
        ItemStack single = cellStack.copyWithCount(1);
        UniversalFluidItemStorage storage = ((FluidCellItem) single.getItem()).getFluidTank(single);
        if (storage == null) {
            return;
        }

        storage.drain(FluidStack.create(Fluids.WATER, Stellaris.CONFIG.foodConfig.fluidCellWaterPerCan), false);
        ItemStack drained = storage.getContainer().copy();
        if (cellStack.getCount() == 1) {
            inventory.setItem(slot, drained);
        } else {
            cellStack.shrink(1);
            if (!inventory.add(drained)) {
                player.drop(drained, false);
            }
        }
    }
}

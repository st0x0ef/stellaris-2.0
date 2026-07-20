package org.exodusstudio.stellaris.common.items;

import com.fej1fun.potentials.fluid.ItemFluidStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FluidCellItem extends Item implements FluidProvider.ITEM {
    private final int capacity;

    public FluidCellItem(Properties properties, final int capacity) {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            UniversalFluidItemStorage cellFluidStorage = getFluidTank(stack);
            if (cellFluidStorage != null) {
                ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
                SpaceSuitModule.CustomFuelModule fuelModule = ModuleUtils.getSpaceSuitModule(chestplate, SpaceSuitModule.CustomFuelModule.class);
                if (chestplate.is(ItemsRegistry.SPACE_SUIT_CHESTPLATE.get()) && fuelModule != null && chestplate.getItem() instanceof FluidProvider.ITEM) {
                    UniversalFluidItemStorage suitFluidStorage = ((FluidProvider.ITEM) chestplate.getItem()).getFluidTank(chestplate);
                    if (suitFluidStorage != null) {
                        FluidStack fluidInCell = cellFluidStorage.getFluidInTank(0);
                        if (!fluidInCell.isEmpty()) {
                            FluidUtil.moveFluid(cellFluidStorage, suitFluidStorage, FluidStack.create(fuelModule.getFuel(), fluidInCell.getAmount()));
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack stack) {
        return new ItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), stack, 1, capacity);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        FluidStack fluidStack = FluidUtil.readStoredFluid(stack, DataComponentsRegistry.FLUID_LIST.get(), 0);
        if (!fluidStack.isEmpty()) {
            String fluidInfo = fluidStack.getName().getString() + " : " + fluidStack.getAmount() + " / " + capacity;
            tooltipAdder.accept(Component.literal(fluidInfo));
        } else {
            String fluidInfo = "Empty: 0 / " + capacity;
            tooltipAdder.accept(Component.literal(fluidInfo));
        }
    }
}

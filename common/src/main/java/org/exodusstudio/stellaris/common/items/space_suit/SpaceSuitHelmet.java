package org.exodusstudio.stellaris.common.items.space_suit;

import com.fej1fun.potentials.energy.ItemEnergyStorage;
import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.EnergyProvider;
import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.exodusstudio.stellaris.common.fluid.OxygenItemFluidStorage;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SpaceSuitHelmet extends SpaceSuitItem implements FluidProvider.ITEM, EnergyProvider.ITEM {
    public SpaceSuitHelmet(Properties properties) {
        super(properties, ArmorType.HELMET);
    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack itemStack) {
        return new OxygenItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), itemStack, 1, SpaceSuitHelmet.getOxygenCapacity(itemStack));
    }

    public static int getOxygenCapacity(ItemStack stack) {
        return stack.getItem() instanceof SpaceSuitHelmet helmet ? helmet.oxygenCapacity(stack) : 0;
    }

    protected int oxygenCapacity(ItemStack stack) {
        AtomicInteger oxygenCapacity = new AtomicInteger(0);
        ModuleUtils.getSpaceSuitModules(stack).getModules().forEach(module -> {
            if (module instanceof SpaceSuitModule.OxygenModule oxygenModule) {
                if (oxygenCapacity.get() < oxygenModule.getCapacity()) {
                    oxygenCapacity.set(oxygenModule.getCapacity());
                }
            }
        });

        return oxygenCapacity.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        appendEnergyHoverText(stack, tooltipAdder);

        appendOxygenHoverText(stack, tooltipAdder);

        SpaceSuitModule.OilFinderModule oilFinderModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.OilFinderModule.class);
        if (oilFinderModule != null) {
            tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.space_suit.oil_finder_module.header").withColor(Utils.getMinecraftColor("gold")));
            tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.space_suit.oil_finder_module.info", oilFinderModule.getRange(), oilFinderModule.getRange()).withColor(Utils.getMinecraftColor("gold")));
        }
    }

    protected void appendEnergyHoverText(ItemStack stack, Consumer<Component> tooltipAdder) {
        UniversalEnergyStorage energy = getEnergy(stack);
        if (energy != null) {
            tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.energy", energy.getEnergy(), energy.getMaxEnergy()));
        }
    }

    protected void appendOxygenHoverText(ItemStack stack, Consumer<Component> tooltipAdder) {
        if (!ModuleUtils.hasSpaceSuitModule(stack, SpaceSuitModule.OxygenModule.class)) {
            return;
        }

        UniversalFluidItemStorage oxygenTank = getFluidTank(stack);
        long oxygen = oxygenTank == null ? 0L : oxygenTank.getFluidInTank(0).getAmount();
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.space_suit.oxygen_module.header").withColor(Utils.getMinecraftColor("cyan")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.space_suit.oxygen_module.amount", oxygen, getOxygenCapacity(stack)).withColor(Utils.getMinecraftColor("cyan")));
    }

    public static final int ENERGY_CAPACITY = 4000;
    public static final int NIGHT_VISION_ENERGY_PER_SECOND = 2;

    @Override
    public @Nullable UniversalEnergyStorage getEnergy(@NotNull ItemStack stack) {
        return new ItemEnergyStorage(stack, DataComponentsRegistry.ENERGY.get(), ENERGY_CAPACITY, 20, 25);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);

        if (equipmentSlot != EquipmentSlot.HEAD || serverLevel.getGameTime() % 20 != 0) {
            return;
        }

        if (itemStack.getOrDefault(DataComponentsRegistry.NIGHT_VISION.get(), false)) {
            UniversalEnergyStorage energy = getEnergy(itemStack);
            if (energy == null || energy.extract(NIGHT_VISION_ENERGY_PER_SECOND, false) < NIGHT_VISION_ENERGY_PER_SECOND) {
                itemStack.set(DataComponentsRegistry.NIGHT_VISION.get(), false);
            }
        }
    }

    public static void tickOilFinderEnergy(ItemStack stack) {
        SpaceSuitModule.OilFinderModule oilFinderModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.OilFinderModule.class);

        if (stack.getItem() instanceof SpaceSuitHelmet spaceSuitHelmet && oilFinderModule != null) {
            spaceSuitHelmet.getEnergy(stack).extract(oilFinderModule.getRange() * oilFinderModule.getRange(), false);
        }
    }

    public static void loadOilAround(ServerPlayer player, int chunkX, int chunkZ) {
        SpaceSuitModule.OilFinderModule oilFinderModule = ModuleUtils.getSpaceSuitModule(player.getItemBySlot(EquipmentSlot.HEAD), SpaceSuitModule.OilFinderModule.class);
        if (oilFinderModule == null) {
            return;
        }

        ServerLevel level = player.level();
        int offset = (oilFinderModule.getRange() - 1) / 2;
        for (int x = chunkX - offset; x <= chunkX + offset; x++) {
            for (int z = chunkZ - offset; z <= chunkZ + offset; z++) {
                if (level.hasChunk(x, z)) {
                    level.getChunk(x, z).stellaris$getChunkOilLevel();
                }
            }
        }
    }
}

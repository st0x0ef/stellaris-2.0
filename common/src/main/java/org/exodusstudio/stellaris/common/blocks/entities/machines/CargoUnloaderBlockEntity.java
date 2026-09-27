package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.entities.vehicles.LanderEntity;
import org.exodusstudio.stellaris.common.menus.CargoUnloaderMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class CargoUnloaderBlockEntity extends BaseEnergyContainerBlockEntity {
    private static final int ENERGY_PER_ITEM = 100;
    private NonNullList<ItemStack> items = NonNullList.withSize(30, ItemStack.EMPTY);

    private LanderEntity targetLander;

    private int cooldown = 20;
    private final int cooldownMax = 20;

    public CargoUnloaderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.CARGO_UNLOADER.get(), blockPos, blockState, 3000);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new CargoUnloaderMenu(i, inventory, this, this);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("item.stellaris.cargo_unloader");
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    public void tick(Level level, BlockState state) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        cooldown = cooldownMax;

        int radius = Stellaris.CONFIG.vehicleConfig.cargoUnloadingRadius;
        AABB aabb = new AABB(this.getBlockPos()).inflate(radius, 3, radius);

        if (targetLander != null && !canUnloadLander(targetLander, aabb)) {
            targetLander = null;
        }

        // if the inventory is empty, find a lander in a 5x5 (configurable) area
        if (targetLander == null && this.items.equals(NonNullList.withSize(this.items.size(), ItemStack.EMPTY))) {
            List<LanderEntity> landerEntityList = level.getEntitiesOfClass(LanderEntity.class, aabb, lander -> canUnloadLander(lander, aabb));
            if (!landerEntityList.isEmpty()) {
                targetLander = landerEntityList.getFirst();
            }
        }

        // if found, pull 1 item slot out of it and put them in the inventory
        if (targetLander != null && energyContainer.getEnergy() >= ENERGY_PER_ITEM) {
            // transfer rocket, fuel input, fuel output in the corresponding slot
            for (int j = 0; j < 3; j++) {
                ItemStack stack = targetLander.inventory.getItem(j);
                if (!stack.isEmpty() && items.get(j).isEmpty()) {
                    items.set(j, stack.copy());
                    stack.setCount(0);
                    useEnergy(ENERGY_PER_ITEM);
                    return;
                }
            }

            // find the first empty slot
            int i = IntStream.range(3, getContainerSize()).filter(j -> items.get(j).isEmpty()).findFirst().orElse(-1);

            if (i == -1) {
                return;
            }

            targetLander.inventory.getItems().subList(3, targetLander.inventory.getContainerSize()).stream()
                    .filter(stack -> !stack.isEmpty())
                    .findFirst()
                    .ifPresent(stack -> {
                        ItemStack copy = stack.copy();
                        items.set(i, copy);
                        stack.setCount(0);
                        useEnergy(ENERGY_PER_ITEM);
                    });
        }
    }

    private boolean canUnloadLander(LanderEntity lander, AABB aabb) {
        return !lander.isRemoved() && lander.level() == this.level && lander.hasLanded() && !lander.isVehicle()
                && aabb.intersects(lander.getBoundingBox());
    }

    @Override
    public int getContainerSize() {
        return 30;
    }

    @Override
    public int getMaxEnergyUsage() {
        return ENERGY_PER_ITEM;
    }
}

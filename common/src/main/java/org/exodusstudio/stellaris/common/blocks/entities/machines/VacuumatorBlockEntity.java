package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.CoalGeneratorBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.items.CanItem;
import org.exodusstudio.stellaris.common.menus.VacuumatorMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VacuumatorBlockEntity extends BaseEnergyContainerBlockEntity {
    private int litTime;
    private int litDuration;
    public final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return litTime;
                }
                case 1 -> {
                    return litDuration;
                }
                default -> {
                    return 0;
                }
            }
        }

        public void set(int index, int value) {
            switch (index) {
                case 0 -> litTime = value;
                case 1 -> litDuration = value;
            }
        }

        public int getCount() {
            return 2;
        }
    };

    private ItemStack resultStack;
    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

    public VacuumatorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.VACUUMATOR.get(), blockPos, blockState, 3000);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new VacuumatorMenu(i, inventory, this, this, dataAccess);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.litTime = input.getInt("BurnTime").orElse(0);
        this.litDuration = input.getInt("BurnDuration").orElse(0);
        this.resultStack = input.read("ResultStack", ItemStack.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("BurnTime", this.litTime);
        output.putInt("BurnDuration", this.litDuration);
        if (this.resultStack != null && !this.resultStack.isEmpty()) {
            output.store("ResultStack", ItemStack.CODEC, this.resultStack);
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("item.stellaris.vacuumator");
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction direction) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
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
        if (!isLit() && canCraft()) {
            ItemStack canStack = getItem(0);
            resultStack = new ItemStack(canStack.getItem());
            CanItem.setFoodProperties(resultStack, CanItem.getFoodProperties(canStack));

            if (CanItem.addFoodToCan(resultStack, getItem(1))) {
                litDuration = getVacuumationDuration(getItem(1));
                litTime = litDuration;
                getBlockState().setValue(CoalGeneratorBlock.LIT, isLit());

                CanItem.addFoodToCan(resultStack, getItem(1));

                for (int i = 0; i < 3; i++) {
                    if (getItem(i).getCount() >= 1) {
                        removeItem(i, 1);
                    }
                }
            }

            setChanged();
        }

        if (isLit() && resultStack != null) {
            int energyPerTick = Stellaris.CONFIG.machineConfig.vacuumatorEnergyPerTick;

            if (getEnergy(null).getEnergy() >= energyPerTick) {
                getEnergy(null).extract(energyPerTick, false);
                litTime--;

                if (litTime <= 0) {
                    setItem(3, resultStack);
                    setItem(4, PotionContents.createItemStack(Items.POTION, Potions.WATER));

                    resultStack = null;

                    getBlockState().setValue(CoalGeneratorBlock.LIT, false);
                }
            }

            setChanged();
        }
    }

    public boolean canCraft() {
        if (getItem(0).getItem() instanceof CanItem) {
            return isFood(getItem(1)) && getItem(2).is(Items.GLASS_BOTTLE) && getItem(3).isEmpty() && getItem(4).getCount() < getItem(4).getMaxStackSize();
        }

        return false;
    }

    public static boolean isFood(ItemStack food) {
        return food.has(DataComponents.FOOD);
    }

    protected int getVacuumationDuration(ItemStack stack) {
        if (stack.isEmpty() || !isFood(stack)) {
            return 0;
        }

        return stack.get(DataComponents.FOOD).nutrition() * Stellaris.CONFIG.machineConfig.vacuumatorDurationMultiplier;
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public int getContainerSize() {
        return 5;
    }
}

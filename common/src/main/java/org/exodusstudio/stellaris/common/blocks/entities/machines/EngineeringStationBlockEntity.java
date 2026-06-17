package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.ImplementedInventory;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.TabSwitchableBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.TickingBlockEntity;
import org.exodusstudio.stellaris.common.data.recipes.RocketStationRecipe;
import org.exodusstudio.stellaris.common.data.recipes.input.RocketStationInput;
import org.exodusstudio.stellaris.common.menus.engineering_station.RocketStationMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EngineeringStationBlockEntity extends BaseContainerBlockEntity implements ImplementedInventory, RecipeInput, TickingBlockEntity, TabSwitchableBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(15, ItemStack.EMPTY);
    public NonNullList<ItemStack> engineUpgradeItems = NonNullList.withSize(2, ItemStack.EMPTY);
    public NonNullList<ItemStack> spaceStationPlannerItems = NonNullList.withSize(10, ItemStack.EMPTY);
    private boolean tabSwitching = false;
    private final RecipeManager.CachedCheck<RocketStationInput, RocketStationRecipe> quickCheck = RecipeManager.createCheck(RecipesRegistry.ROCKET_STATION_TYPE.get());

    public EngineeringStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.ENGINEERING_STATION.get(), blockPos, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("stellaris.screen.engineering_station");
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new RocketStationMenu(i, inventory, this, this);
    }

    public void setTabSwitching(boolean tabSwitching) { this.tabSwitching = tabSwitching; }

    public boolean isTabSwitching() { return this.tabSwitching; }

    @Override
    public void setChanged() {
        if (this.level != null) {
            this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            super.setChanged();
        }
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
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
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
    public void tick(Level level, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        ItemStack outputStack = getItem(14);
        if ((outputStack.isEmpty() || outputStack.getCount() < outputStack.getMaxStackSize()) && level instanceof ServerLevel serverLevel) {
            RocketStationInput input = new RocketStationInput(this, getItems());
            Optional<RecipeHolder<RocketStationRecipe>> recipeHolder = quickCheck.getRecipeFor(input, serverLevel);

            if (recipeHolder.isPresent()) {

                RocketStationRecipe recipe = recipeHolder.get().value();
                ItemStack resultStack = recipe.assemble(input);
                if (outputStack.isEmpty() || (ItemStack.isSameItemSameComponents(outputStack, resultStack)
                        && outputStack.getCount() + resultStack.getCount() <= outputStack.getMaxStackSize())) {

                    if (outputStack.isEmpty()) {
                        setItem(14, resultStack.copy());
                    }
                    else if (ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
                        outputStack.grow(1);
                    }
                    else {
                        return;
                    }

                    for (int i = 0; i < 14; i++) {
                        ItemStack stack = getItem(i);
                        stack.shrink(1);

                        if (stack.isEmpty()) {
                            setItem(i, ItemStack.EMPTY);
                        }
                    }
                    setChanged();
                }
            }
        }
    }

    @Override
    public int size() {
        return getContainerSize();
    }

    @Override
    public int getContainerSize() {
        return 15;
    }
}

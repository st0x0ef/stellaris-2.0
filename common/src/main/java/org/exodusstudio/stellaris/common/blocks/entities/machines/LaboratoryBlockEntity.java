package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.ImplementedInventory;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.TickingBlockEntity;
import org.exodusstudio.stellaris.common.menus.laboratory.ResearchMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LaboratoryBlockEntity extends BaseContainerBlockEntity implements ImplementedInventory, RecipeInput, TickingBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

    public int progressTickLeft = -1;

    public LaboratoryBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.LABORATORY.get(), blockPos, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("stellaris.screen.laboratory");
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new ResearchMenu(i, inventory, this, this);
    }

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
        if (progressTickLeft > 0) {
            progressTickLeft--;
        }
    }

    @Override
    public int size() {
        return getContainerSize();
    }

    @Override
    public int getContainerSize() {
        return 5;
    }
}
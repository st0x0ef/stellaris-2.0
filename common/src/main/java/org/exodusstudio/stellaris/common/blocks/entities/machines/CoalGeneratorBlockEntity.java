package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.CoalGeneratorBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.menus.CoalGeneratorMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyUtil;

public class CoalGeneratorBlockEntity extends BaseGeneratorBlockEntity {

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

    public CoalGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(BlockEntitiesRegistry.COAL_GENERATOR.get(), blockPos, blockState, 1, 12800);
    }

    public CoalGeneratorBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState, int energyGeneratedPT, int maxCapacity) {
        super(entityType, blockPos, blockState, energyGeneratedPT, maxCapacity);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new CoalGeneratorMenu(containerId, inventory, this, this, dataAccess);
    }

    public void tick(Level level, BlockState blockState) {
        boolean wasLit = isLit();
        boolean shouldUpdate = false;

        if (canGenerate()) {
            --litTime;
        }

        ItemStack stack = getItems().getFirst();
        if (!isLit() && !stack.isEmpty()) {
            litTime = getBurnDuration(stack);
            litDuration = litTime;
            if (isLit()) {
                shouldUpdate = true;
                Item item = stack.getItem();
                stack.shrink(1);
                if (stack.isEmpty()) {
                    Item item2 = item.getCraftingRemainder().getItem();
                    getItems().set(0, new ItemStack(item2));
                }
            }
        }

        if (wasLit != isLit()) {
            shouldUpdate = true;
            BlockState state = getBlockState().setValue(CoalGeneratorBlock.LIT, isLit());
            level.setBlock(getBlockPos(), state, 3);
        }
        if (shouldUpdate) {
            setChanged();
        }
        if (isLit()) {
            energyContainer.insertWithoutLimits(energyGeneratedPT, false);
        }

        EnergyUtil.distributeEnergyNearby(level, worldPosition, maxCapacity);
    }

    protected int getBurnDuration(ItemStack fuelStack) {
        if (fuelStack.isEmpty() || !fuelStack.is(TagsRegistry.ItemTags.COAL_GENERATOR_FUEL) || level == null) {
            return 0;
        }

        return level.fuelValues().burnDuration(fuelStack);
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public boolean canGenerate() {
        boolean isMaxEnergy = energyContainer.getEnergy() == energyContainer.getMaxEnergy();
        return isLit() && !isMaxEnergy;
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (input.getInt("BurnTime").isPresent()) {
            litTime = input.getInt("BurnTime").get();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("BurnTime", this.litTime);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("item.stellaris.coal_generator");
    }

    @Override
    public int getContainerSize() {
        return 1;
    }
}

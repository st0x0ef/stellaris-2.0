package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.base.BaseLitMachineBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.items.CanItem;
import org.exodusstudio.stellaris.common.menus.VacuumatorMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncFluidPacketWithoutDirection;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VacuumatorBlockEntity extends BaseEnergyContainerBlockEntity implements FluidProvider.BLOCK {
    /** Slot 0: food (consumed), 1: can (consumed), 2: canned result, 3: empty fluid container, 4: filled container. */
    public static final int FOOD_SLOT = 0;
    public static final int CAN_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    public static final int FLUID_CONTAINER_SLOT = 3;
    public static final int FILLED_CONTAINER_SLOT = 4;

    public static final int WATER_CAPACITY = 4000;

    /** Hoppers on top feed the food and can slots, hoppers underneath pull the canned result. */
    private static final int[] INPUT_SLOTS = new int[]{FOOD_SLOT, CAN_SLOT};
    private static final int[] OUTPUT_SLOTS = new int[]{RESULT_SLOT};
    private static final int[] NO_SLOTS = new int[0];

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

    private final SingleFluidStorage waterTank;

    public VacuumatorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.VACUUMATOR.get(), blockPos, blockState, 3000);

        this.waterTank = new SingleFluidStorage(WATER_CAPACITY, 0, WATER_CAPACITY) {
            @Override
            protected void onChange() {
                setChanged();
                if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                    NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                            new SyncFluidPacketWithoutDirection(new FluidAmountMapDataComponent(List.of(getFluidInTank(0).getFluid()), List.of(getFluidValueInTank())), 0, getBlockPos()));
                }
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return stack.getFluid().isSame(Fluids.WATER);
            }
        };
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory) {
        if (inventory.player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new SyncFluidPacketWithoutDirection(
                    new FluidAmountMapDataComponent(List.of(waterTank.getFluidInTank(0).getFluid()), List.of(waterTank.getFluidValueInTank())),
                    0, getBlockPos()));
        }
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
        this.waterTank.load(input, "water");
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
        this.waterTank.save(output, "water");
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("item.stellaris.vacuumator");
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction direction) {
        if (direction == Direction.UP) {
            return INPUT_SLOTS;
        }

        if (direction == Direction.DOWN) {
            return OUTPUT_SLOTS;
        }

        return NO_SLOTS;
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return canInsertIntoSlot(i, itemStack);
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return direction == Direction.UP && canInsertIntoSlot(i, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return direction == Direction.DOWN && i == RESULT_SLOT;
    }

    private static boolean canInsertIntoSlot(int slot, ItemStack stack) {
        return switch (slot) {
            case FOOD_SLOT -> isFood(stack) && !stack.is(TagsRegistry.ItemTags.CAN);
            case CAN_SLOT -> stack.is(TagsRegistry.ItemTags.CAN);
            default -> false;
        };
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
        boolean wasLit = isLit();

        FluidUtil.moveFluidToItem(0, waterTank, FLUID_CONTAINER_SLOT, FILLED_CONTAINER_SLOT, this, Long.MAX_VALUE);

        FluidUtil.distributeFluidNearby(level, worldPosition, waterTank.getFluidInTank(0));

        if (!isLit() && canCraft()) {
            resultStack = buildResult();
            litDuration = getVacuumationDuration(getItem(FOOD_SLOT));
            litTime = litDuration;

            getEnergy(null).extract(Stellaris.CONFIG.machineConfig.vacuumatorEnergyPerCraft, false);

            removeItem(FOOD_SLOT, 1);
            removeItem(CAN_SLOT, 1);

            setChanged();
        }

        if (isLit() && resultStack != null) {
            boolean canFinish = litTime > 1 || fitsInResultSlot(resultStack);

            if (canFinish) {
                litTime--;

                if (litTime <= 0) {
                    storeResult(resultStack);
                    waterTank.fillWithoutLimits(FluidStack.create(Fluids.WATER, Stellaris.CONFIG.machineConfig.vacuumatorWaterPerCraft), false);

                    resultStack = null;
                }
            }

            setChanged();
        }

        if (wasLit != isLit()) {
            level.setBlock(worldPosition, state.setValue(BaseLitMachineBlock.LIT, isLit()), 3);
        }
    }

    public boolean canCraft() {
        ItemStack result = buildResult();

        if (result.isEmpty() || !fitsInResultSlot(result)) {
            return false;
        }

        if (getEnergy(null).getEnergy() < Stellaris.CONFIG.machineConfig.vacuumatorEnergyPerCraft) {
            return false;
        }

        return waterTank.getTankCapacity(0) - waterTank.getFluidValueInTank() >= Stellaris.CONFIG.machineConfig.vacuumatorWaterPerCraft;
    }

    private ItemStack buildResult() {
        ItemStack canStack = getItem(CAN_SLOT);

        if (!(canStack.getItem() instanceof CanItem) || !isFood(getItem(FOOD_SLOT))) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(canStack.getItem());
        CanItem.setFoodProperties(result, CanItem.getFoodProperties(canStack));

        return CanItem.addFoodToCan(result, getItem(FOOD_SLOT)) ? result : ItemStack.EMPTY;
    }

    private boolean fitsInResultSlot(ItemStack result) {
        ItemStack output = getItem(RESULT_SLOT);

        if (output.isEmpty()) {
            return true;
        }

        return ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), getMaxStackSize());
    }

    private void storeResult(ItemStack result) {
        ItemStack output = getItem(RESULT_SLOT);

        if (output.isEmpty()) {
            setItem(RESULT_SLOT, result);
        }
        else {
            setItem(RESULT_SLOT, output.copyWithCount(output.getCount() + result.getCount()));
        }
    }

    public static boolean isFood(ItemStack food) {
        return food.has(DataComponents.FOOD);
    }

    protected int getVacuumationDuration(ItemStack stack) {
        if (stack.isEmpty() || !isFood(stack)) {
            return 0;
        }

        return Math.max(1, stack.get(DataComponents.FOOD).nutrition() * Stellaris.CONFIG.machineConfig.vacuumatorDurationMultiplier);
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    public SingleFluidStorage getWaterTank() {
        return waterTank;
    }

    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        return waterTank;
    }
}

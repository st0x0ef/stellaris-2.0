package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.data.recipes.BlendingRecipe;
import org.exodusstudio.stellaris.common.data.recipes.input.BlenderInput;
import org.exodusstudio.stellaris.common.menus.BlenderMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlenderBlockEntity extends BaseEnergyContainerBlockEntity {
    public static final int GRID_WIDTH = 3;
    public static final int GRID_HEIGHT = 3;

    public static final int INPUT_SLOT_COUNT = GRID_WIDTH * GRID_HEIGHT;
    public static final int RESULT_SLOT = INPUT_SLOT_COUNT;
    public static final int CONTAINER_SIZE = INPUT_SLOT_COUNT + 1;

    public static final int ENERGY_CAPACITY = 6000;

    public static final int PROGRESS_SCALE = 1000;

    private static final int RECIPE_RESCAN_INTERVAL = 100;

    private static final int[] INPUT_FACE_SLOTS = buildInputFaceSlots();
    private static final int[] OUTPUT_FACE_SLOTS = new int[]{RESULT_SLOT};

    private final RecipeManager.CachedCheck<BlenderInput, BlendingRecipe> blendingCheck =
            RecipeManager.createCheck(RecipesRegistry.BLENDING_TYPE.get());

    private int blendTime;
    private int blendDuration;

    private ItemStack resultStack = ItemStack.EMPTY;

    private final List<ItemStack> scannedGrid = new ArrayList<>();
    private @Nullable CraftingRecipe scannedRecipe;
    private int ticksSinceScan = Integer.MAX_VALUE;

    private boolean canBlend;
    private int ticksSinceCanBlendCheck = Integer.MAX_VALUE;
    private static final int CAN_BLEND_CHECK_INTERVAL = 5;

    public final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> blendDuration == 0 ? 0 : PROGRESS_SCALE - (int) ((long) blendTime * PROGRESS_SCALE / blendDuration);
                case 1 -> blendTime > 0 ? 1 : 0;
                case 2 -> canBlend ? 1 : 0;
                default -> 0;
            };
        }

        public void set(int index, int value) {
        }

        public int getCount() {
            return 3;
        }
    };

    public BlenderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.BLENDER.get(), blockPos, blockState, ENERGY_CAPACITY);
    }

    private static int[] buildInputFaceSlots() {
        int[] slots = new int[INPUT_SLOT_COUNT];
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            slots[slot] = slot;
        }

        return slots;
    }

    @Override
    public void tick(Level level, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (ticksSinceScan < Integer.MAX_VALUE) {
            ticksSinceScan++;
        }

        if (isBlending() && resultStack.isEmpty()) {
            blendTime = 0;
            blendDuration = 0;
            setChanged();
        }

        if (!isBlending()) {
            refreshCanBlend(serverLevel);
            return;
        }

        if (blendTime > 1 || fitsInResultSlot(resultStack)) {
            blendTime--;

            if (blendTime <= 0) {
                storeResult(resultStack);
                resultStack = ItemStack.EMPTY;
                blendDuration = 0;
            }

            setChanged();
        }
    }

    public void requestBlend() {
        if (!(level instanceof ServerLevel serverLevel) || isBlending()) {
            return;
        }

        Batch batch = planBatch(serverLevel);
        if (batch != null) {
            startBatch(serverLevel, batch);
        }
    }

    private void refreshCanBlend(ServerLevel level) {
        if (ticksSinceCanBlendCheck < CAN_BLEND_CHECK_INTERVAL) {
            ticksSinceCanBlendCheck++;
            return;
        }

        ticksSinceCanBlendCheck = 0;
        canBlend = planBatch(level) != null;
    }

    private record Batch(Blend blend, BlenderInput input, int size, List<Holder<Item>> picks) {
    }

    private @Nullable Batch planBatch(ServerLevel level) {
        BlenderInput input = gridInput();
        if (input.isEmpty()) {
            return null;
        }

        Blend blend = resolveBlend(level, input);
        if (blend == null) {
            return null;
        }

        List<Holder<Item>> picks = new ArrayList<>();
        int size = input.contents().getBiggestCraftableStack(blend.recipe(), affordableBatch(blend), picks::add);

        return size <= 0 ? null : new Batch(blend, input, size, picks);
    }

    private void startBatch(Level level, Batch batch) {
        Blend blend = batch.blend();

        energyContainer.extract(blend.energyPerCraft() * batch.size(), false);
        consumePicks(level, batch.picks(), batch.size(), remaindersFor(batch.input(), blend.recipe(), batch.picks()));

        resultStack = blend.unitResult().copyWithCount(blend.unitResult().getCount() * batch.size());
        blendDuration = Math.max(1, blend.ticksPerCraft() * batch.size());
        blendTime = blendDuration;

        canBlend = false;
        ticksSinceCanBlendCheck = Integer.MAX_VALUE;

        setChanged();
    }

    private int affordableBatch(Blend blend) {
        ItemStack unit = blend.unitResult();
        if (unit.isEmpty()) {
            return 0;
        }

        int limit = Math.min(unit.getMaxStackSize(), getMaxStackSize());
        ItemStack output = getItem(RESULT_SLOT);
        int room;

        if (output.isEmpty()) {
            room = limit;
        }
        else if (ItemStack.isSameItemSameComponents(output, unit)) {
            room = limit - output.getCount();
        }
        else {
            return 0;
        }

        int batch = room / unit.getCount();

        if (blend.energyPerCraft() > 0) {
            batch = Math.min(batch, energyContainer.getEnergy() / blend.energyPerCraft());
        }

        return batch;
    }

    private record Blend(Recipe<?> recipe, ItemStack unitResult, int energyPerCraft, int ticksPerCraft) {
    }

    private @Nullable Blend resolveBlend(ServerLevel level, BlenderInput input) {
        Optional<RecipeHolder<BlendingRecipe>> blending = blendingCheck.getRecipeFor(input, level);
        if (blending.isPresent()) {
            BlendingRecipe recipe = blending.get().value();
            return new Blend(recipe, recipe.assemble(input), recipe.energyCost(), recipe.blendingTime());
        }

        CraftingRecipe crafting = craftingRecipeFor(level, input);
        if (crafting == null) {
            return null;
        }

        List<Holder<Item>> picks = new ArrayList<>();
        if (!input.contents().canCraft(crafting, 1, picks::add)) {
            return null;
        }

        CraftingInput unitInput = craftingGridOf(input, picks);
        if (unitInput == null || !crafting.matches(unitInput, level)) {
            return null;
        }

        return new Blend(crafting, crafting.assemble(unitInput),
                Stellaris.CONFIG.machineConfig.blenderEnergyPerCraft,
                Stellaris.CONFIG.machineConfig.blenderTicksPerCraft);
    }

    private List<ItemStack> remaindersFor(BlenderInput input, Recipe<?> recipe, List<Holder<Item>> picks) {
        if (!(recipe instanceof CraftingRecipe crafting)) {
            return List.of();
        }

        CraftingInput grid = craftingGridOf(input, picks);
        return grid == null ? List.of() : crafting.getRemainingItems(grid);
    }

    private @Nullable CraftingInput craftingGridOf(BlenderInput input, List<Holder<Item>> picks) {
        if (picks.size() > INPUT_SLOT_COUNT) {
            return null;
        }

        List<ItemStack> cells = new ArrayList<>(INPUT_SLOT_COUNT);
        for (Holder<Item> pick : picks) {
            ItemStack found = input.findStack(pick);
            if (found.isEmpty()) {
                return null;
            }

            cells.add(found.copyWithCount(1));
        }

        while (cells.size() < INPUT_SLOT_COUNT) {
            cells.add(ItemStack.EMPTY);
        }

        return CraftingInput.of(GRID_WIDTH, GRID_HEIGHT, cells);
    }

    private @Nullable CraftingRecipe craftingRecipeFor(ServerLevel level, BlenderInput input) {
        if (ticksSinceScan < RECIPE_RESCAN_INTERVAL && gridMatchesScan()) {
            return scannedRecipe;
        }

        scannedRecipe = scanCraftingRecipes(level, input);
        ticksSinceScan = 0;

        scannedGrid.clear();
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            scannedGrid.add(getItem(slot).copy());
        }

        return scannedRecipe;
    }

    private boolean gridMatchesScan() {
        if (scannedGrid.size() != INPUT_SLOT_COUNT) {
            return false;
        }

        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            if (!ItemStack.matches(scannedGrid.get(slot), getItem(slot))) {
                return false;
            }
        }

        return true;
    }

    private static @Nullable CraftingRecipe scanCraftingRecipes(ServerLevel level, BlenderInput input) {
        List<Holder<Item>> present = input.distinctItems();
        if (present.isEmpty()) {
            return null;
        }

        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (!(holder.value() instanceof CraftingRecipe crafting) || crafting instanceof ShapedRecipe) {
                continue;
            }

            PlacementInfo placement = crafting.placementInfo();
            if (placement.isImpossibleToPlace() || !everyIngredientPresent(placement, present)) {
                continue;
            }

            if (input.contents().canCraft(crafting, 1, null)) {
                return crafting;
            }
        }

        return null;
    }

    private static boolean everyIngredientPresent(PlacementInfo placement, List<Holder<Item>> present) {
        for (Ingredient ingredient : placement.ingredients()) {
            boolean found = false;

            for (Holder<Item> item : present) {
                if (ingredient.acceptsItem(item)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }

    private BlenderInput gridInput() {
        List<ItemStack> grid = new ArrayList<>(INPUT_SLOT_COUNT);
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            grid.add(getItem(slot));
        }

        return new BlenderInput(grid);
    }

    private void consumePicks(Level level, List<Holder<Item>> picks, int batch, List<ItemStack> unitRemainders) {
        for (Holder<Item> pick : picks) {
            Item item = pick.value();
            int left = batch;

            for (int slot = 0; slot < INPUT_SLOT_COUNT && left > 0; slot++) {
                if (!getItem(slot).is(item)) {
                    continue;
                }

                int taken = Math.min(left, getItem(slot).getCount());
                removeItem(slot, taken);
                left -= taken;
            }
        }

        for (int index = 0; index < Math.min(picks.size(), unitRemainders.size()); index++) {
            ItemStack remainder = unitRemainders.get(index);

            if (!remainder.isEmpty()) {
                returnRemainder(level, remainder.copyWithCount(remainder.getCount() * batch));
            }
        }
    }

    private void returnRemainder(Level level, ItemStack remainder) {
        for (int slot = 0; slot < INPUT_SLOT_COUNT && !remainder.isEmpty(); slot++) {
            ItemStack stack = getItem(slot);

            if (stack.isEmpty()) {
                setItem(slot, remainder.split(Math.min(remainder.getCount(), remainder.getMaxStackSize())));
            }
            else if (ItemStack.isSameItemSameComponents(stack, remainder)) {
                int moved = Math.min(remainder.getCount(), stack.getMaxStackSize() - stack.getCount());

                if (moved > 0) {
                    stack.grow(moved);
                    remainder.shrink(moved);
                    setChanged();
                }
            }
        }

        if (!remainder.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0,
                    worldPosition.getZ() + 0.5, remainder);
        }
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

    public boolean isBlending() {
        return blendTime > 0;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.blendTime = input.getInt("BlendTime").orElse(0);
        this.blendDuration = input.getInt("BlendDuration").orElse(0);
        this.resultStack = input.read("ResultStack", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("BlendTime", this.blendTime);
        output.putInt("BlendDuration", this.blendDuration);
        if (!this.resultStack.isEmpty()) {
            output.store("ResultStack", ItemStack.CODEC, this.resultStack);
        }
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction direction) {
        return direction == Direction.DOWN ? OUTPUT_FACE_SLOTS : INPUT_FACE_SLOTS;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return slot < INPUT_SLOT_COUNT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        return direction != Direction.DOWN && canPlaceItem(slot, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return direction == Direction.DOWN && slot == RESULT_SLOT;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("item.stellaris.blender");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new BlenderMenu(containerId, inventory, this, this, dataAccess);
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }
}

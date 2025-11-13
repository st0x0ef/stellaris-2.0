package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.ElectrolyzerBlock;
import org.exodusstudio.stellaris.common.blocks.base.BaseMachineBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.data.recipe.ElectrolyzeRecipe;
import org.exodusstudio.stellaris.common.data.recipe.input.FluidInput;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.MultipleFluidStorage;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.network.packets.SyncFluidPacket;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ElectrolyzerBlockEntity extends BaseEnergyContainerBlockEntity implements FluidProvider.BLOCK{

    private final RecipeManager.CachedCheck<FluidInput, ElectrolyzeRecipe> cachedCheck = RecipeManager.createCheck(RecipesRegistry.ELECTROLYZE_TYPE.get());


    public final SingleFluidStorage ingredientTank = new SingleFluidStorage(3000, 3000, 0) {

        @Override
        protected void onChange() {
            setChanged();
            if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                        new SyncFluidPacket(new FluidAmountMapDataComponent(List.of(getFluidInTank(0).getFluid()), List.of(getFluidValueInTank())), 0, getBlockPos(), Direction.UP));
            }
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }
    };

    public final MultipleFluidStorage resultTanks = new MultipleFluidStorage(2, 6000, 0, 1000) {

        @Override
        protected void onChange(int tank) {
            setChanged();
            if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                        new SyncFluidPacket(new FluidAmountMapDataComponent(List.of(getFluidInTank(tank).getFluid()), List.of(getFluidValueInTank(tank))), tank, getBlockPos(), getBlockState().getValue(ElectrolyzerBlock.FACING).getClockWise()));
            }
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            if (level != null && level instanceof ServerLevel serverLevel) {
                Optional<RecipeHolder<ElectrolyzeRecipe>> recipeHolder = cachedCheck.getRecipeFor(new FluidInput(ElectrolyzerBlockEntity.this), serverLevel);
                if(recipeHolder.isPresent()) {
                    ElectrolyzeRecipe recipe = recipeHolder.get().value();

                    return recipe.resultStacks().get(tank).getFluid() == stack.getFluid();

                }
            }

            return false;
        }
    };



    public ElectrolyzerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.ELECTROLYZER.get(), blockPos, blockState);
    }

    @Override
    public void tick() {
        if (level == null) {
            return;
        }


        //Move fluid to item for results tanks
        FluidUtil.moveFluidToItem(0, resultTanks, 3, items, 1000);
        FluidUtil.moveFluidToItem(1, resultTanks, 2, items, 1000);

        //Move fluid to item for ingredient tank
        FluidUtil.moveFluidFromItem(0, 1, items, ingredientTank, 1000);

        Direction facing = getBlockState().getValue(ElectrolyzerBlock.FACING);
        FluidUtil.distributeFluidNearby(level, worldPosition, resultTanks.getFluidInTank(0), List.of(facing.getClockWise()));
        FluidUtil.distributeFluidNearby(level, worldPosition, resultTanks.getFluidInTank(1), List.of(facing.getCounterClockWise()));
        FluidUtil.distributeFluidNearby(level, worldPosition, ingredientTank.getFluidInTank(0), List.of(Direction.UP, Direction.DOWN, facing, facing.getOpposite()));

        if(level instanceof ServerLevel serverLevel) {
            Optional<RecipeHolder<ElectrolyzeRecipe>> recipeHolder = cachedCheck.getRecipeFor(new FluidInput(this), serverLevel);
            if (recipeHolder.isPresent()) {
                ElectrolyzeRecipe recipe = recipeHolder.get().value();

                if (energyContainer.getEnergy() >= recipe.energy()) {
                    boolean shouldDrainWaterAndEnergy = false;

                    if (resultTanks.getFluidValueInTank(0) < resultTanks.getTankCapacity(0)) {
                        resultTanks.fillWithoutLimits(recipe.resultStacks().getFirst(), false);
                        shouldDrainWaterAndEnergy = true;
                    }
                    if (resultTanks.getFluidValueInTank(1) < resultTanks.getTankCapacity(1)) {
                        resultTanks.fillWithoutLimits(recipe.resultStacks().get(1), false);
                        shouldDrainWaterAndEnergy = true;
                    }

                    if (shouldDrainWaterAndEnergy) {
                        ingredientTank.drainWithoutLimits(recipe.ingredientStack(), false);
                        energyContainer.extract(recipe.energy(), false);
                    }
                }
            }

        }

    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ingredientTank.save(output, "ingredient");
        resultTanks.save(output, "resultTanks");

    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ingredientTank.load(input, "ingredient");
        resultTanks.load(input, "resultTanks");
    }

    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        Direction facing = getBlockState().getValue(BaseMachineBlock.FACING);
        if (facing.getCounterClockWise() == direction || facing.getClockWise() == direction) {
            return resultTanks;
        }
        return ingredientTank;
    }


    @Override
    protected Component getDefaultName() {
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    public int getContainerSize() {
        return 4;
    }
}
